package com.github.sparkzxl.security.event;

import com.github.sparkzxl.security.entity.LoginStatus;
import org.springframework.context.ApplicationEvent;

/**
 * description: 登录事件
 *
 * @author zhouxinlei
 */
public class LoginEvent extends ApplicationEvent {

    public LoginEvent(LoginStatus source) {
        super(source);
    }
}
