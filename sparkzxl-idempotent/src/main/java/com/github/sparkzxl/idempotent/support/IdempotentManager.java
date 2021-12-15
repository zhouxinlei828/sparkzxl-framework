package com.github.sparkzxl.idempotent.support;

/**
 * description: 幂等管理器
 *
 * @author zhouxinlei
 */
public interface IdempotentManager {

    /**
     * 抢占锁
     *
     * @param idempotentInfo 幂等信息类
     * @return boolean
     */
    boolean tryLock(IdempotentInfo idempotentInfo);

    /**
     * 没有抢占到锁的处理逻辑
     *
     * @param idempotentInfo 幂等信息类
     * @return Object
     */
    Object handlerNoLock(IdempotentInfo idempotentInfo);

    /**
     * 占用到锁,并业务处理完成
     *
     * @param idempotentInfo 幂等信息类
     * @param result         结果
     */
    void complete(IdempotentInfo idempotentInfo, Object result);

}
