/*
 * Copyright 2018-2022 guerlab.net and other contributors.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sparkzxl.sms.handler;

import com.github.sparkzxl.sms.event.SmsSendSuccessEvent;
import org.springframework.context.ApplicationListener;

/**
 * description: 发送成功事件监听接口.
 *
 * @author zhouxinlei
 * @since 2022-11-09 11:30:23
 */
public interface SmsSendSuccessEventListener extends ApplicationListener<SmsSendSuccessEvent> {

}
