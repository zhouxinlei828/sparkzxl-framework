package com.sparksys.oauth.event;

import com.sparksys.oauth.entity.LoginStatus;
import org.springframework.context.ApplicationEvent;

/**
 * description: 登录事件
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 13:29:58
 */
public class LoginEvent extends ApplicationEvent {

    public LoginEvent(LoginStatus source) {
        super(source);
    }
}
