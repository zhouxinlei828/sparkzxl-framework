package com.github.sparkzxl.security.event;

import com.github.sparkzxl.security.entity.LoginStatus;
import org.springframework.context.ApplicationEvent;

/**
 * description: 登录事件
 *
 * @author: zhouxinlei
 * @date: 2021-03-02 13:38:57
*/
public class LoginEvent extends ApplicationEvent {

    public LoginEvent(LoginStatus source) {
        super(source);
    }
}
