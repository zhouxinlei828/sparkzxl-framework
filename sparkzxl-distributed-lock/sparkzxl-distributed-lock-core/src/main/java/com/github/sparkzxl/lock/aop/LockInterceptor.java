/*
 *  Copyright (c) 2018-2022, baomidou (63976799@qq.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.sparkzxl.lock.aop;

import com.github.sparkzxl.lock.LockFailureStrategy;
import com.github.sparkzxl.lock.LockInfo;
import com.github.sparkzxl.lock.LockKeyBuilder;
import com.github.sparkzxl.lock.LockTemplate;
import com.github.sparkzxl.lock.annotation.DistributedLock;
import com.github.sparkzxl.lock.autoconfigure.DistributedLockProperties;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


/**
 * description: 分布式锁aop处理器
 *
 * @author zhouxinlei
 * @since 2022-05-27 12:30:03
 */
@Slf4j
@RequiredArgsConstructor
public class LockInterceptor implements MethodInterceptor {

    private final LockTemplate lockTemplate;

    private final LockKeyBuilder lockKeyBuilder;

    private final LockFailureStrategy lockFailureStrategy;

    private final DistributedLockProperties distributedLockProperties;

    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        //fix 使用其他aop组件时,aop切了两次.
        Class<?> cls = AopProxyUtils.ultimateTargetClass(Objects.requireNonNull(invocation.getThis()));
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        DistributedLock distributedLock = invocation.getMethod().getAnnotation(DistributedLock.class);
        AtomicReference<LockInfo> lockInfo = new AtomicReference<>();
        return Try.of(() -> {
            String prefix = distributedLockProperties.getLockKeyPrefix() + ":";
            prefix += StringUtils.hasText(distributedLock.name()) ? distributedLock.name() :
                    invocation.getMethod().getDeclaringClass().getName() + invocation.getMethod().getName();
            String key = prefix + "#" + lockKeyBuilder.buildKey(invocation, distributedLock.keys());
            LockInfo info = lockTemplate.lock(key, distributedLock.expire(), distributedLock.acquireTimeout(), distributedLock.executor());
            lockInfo.set(info);
            if (null != lockInfo.get()) {
                log.info("Thread[{}] -> get lock key[{}] success", lockInfo.get().getThreadId(), key);
                return invocation.proceed();
            }
            // lock failure
            lockFailureStrategy.onLockFailure(key, invocation.getMethod(), invocation.getArguments());
            return null;
        }).andFinally(() -> {
            if (null != lockInfo.get() && distributedLock.autoRelease()) {
                log.info("Thread[{}] -> releaseLock key [{}]", lockInfo.get().getThreadId(), lockInfo.get().getLockKey());
                final boolean releaseLock = lockTemplate.releaseLock(lockInfo.get());
                if (!releaseLock) {
                    log.error("releaseLock fail,lock Key={},lock Value={}", lockInfo.get().getLockKey(),
                            lockInfo.get().getLockValue());
                }
            }
        });
    }

}
