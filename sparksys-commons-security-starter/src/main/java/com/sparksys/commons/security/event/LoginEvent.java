package com.sparksys.commons.security.event;

import org.springframework.context.ApplicationEvent;
import com.sparksys.commons.security.entity.LoginStatus;
/**
 * 登录事件
 *
 * @author zuihou
 * @date 2020年03月18日17:22:55
 */
public class LoginEvent extends ApplicationEvent {

    public LoginEvent(LoginStatus source) {
        super(source);
    }
}
