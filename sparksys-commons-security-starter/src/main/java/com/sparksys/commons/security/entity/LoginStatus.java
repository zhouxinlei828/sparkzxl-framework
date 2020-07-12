package com.sparksys.commons.security.entity;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.sparksys.commons.core.utils.ip2region.AddressUtil;
import com.sparksys.commons.web.utils.HttpUtils;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 登录态
 *
 * @author zuihou
 * @date 2020年03月18日17:25:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class LoginStatus implements Serializable {
    private static final long serialVersionUID = -3124612657759050173L;
    /***
     * 用户id
     */
    private Long id;
    /**
     * 账号
     */
    private String account;

    /**
     * 登录类型
     */
    private Type type;
    /**
     * 登录描述
     */
    private String description;

    /**
     * 登录浏览器
     */
    private String ua;
    /**
     * 登录IP
     */
    private String ip;
    /**
     * 登录地址
     */
    private String location;

    public static LoginStatus success(Long id) {
        LoginStatus loginStatus = LoginStatus.builder()
                .id(id)
                .type(Type.SUCCESS).description("登录成功")
                .build().setInfo();
        return loginStatus;
    }

    public static LoginStatus success(String account) {
        LoginStatus loginStatus = LoginStatus.builder()
                .account(account)
                .type(Type.SUCCESS).description("登录成功")
                .build().setInfo();
        return loginStatus;
    }

    public static LoginStatus fail(Long id, String description) {
        return LoginStatus.builder()
                .id(id)
                .type(Type.FAIL).description(description)
                .build().setInfo();
    }

    public static LoginStatus fail(String account, String description) {
        return LoginStatus.builder()
                .account(account)
                .type(Type.FAIL).description(description)
                .build().setInfo();
    }

    public static LoginStatus pwdError(Long id, String description) {
        return LoginStatus.builder()
                .id(id)
                .type(Type.PWD_ERROR).description(description)
                .build().setInfo();
    }

    private LoginStatus setInfo() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return this;
        }
        HttpServletRequest request = HttpUtils.getRequest();
        String ua = StrUtil.sub(request.getHeader("user-agent"), 0, 500);
        String ip = ServletUtil.getClientIP(request);
        this.ua = ua;
        this.ip = ip;
        this.location = AddressUtil.getRegion(ip);
        return this;
    }

    @Getter
    public enum Type {
        SUCCESS,
        PWD_ERROR,
        FAIL;
    }

}
