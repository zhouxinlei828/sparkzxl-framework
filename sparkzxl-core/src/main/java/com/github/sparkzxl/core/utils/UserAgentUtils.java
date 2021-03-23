package com.github.sparkzxl.core.utils;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.github.sparkzxl.core.entity.UserAgentEntity;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * description: 用户代理工具类
 *
 * @author zhouxinlei
 */
public class UserAgentUtils extends UserAgentUtil {

    public static UserAgentEntity getUserAgentEntity() {
        UserAgentEntity userAgentEntity = new UserAgentEntity();
        HttpServletRequest request = RequestContextHolderUtils.getRequest();
        String ua = ServletUtil.getHeader(request, "user-agent", StandardCharsets.UTF_8);
        UserAgent userAgent = UserAgentUtil.parse(ua);
        String browserVersion = userAgent.getVersion();
        String clientIp = ServletUtil.getClientIP(request);
        userAgentEntity.setLocation(AddressUtil.getRegion(clientIp));
        userAgentEntity.setRequestIp(clientIp);
        userAgentEntity.setBrowser(userAgent.getBrowser().toString());
        userAgentEntity.setBrowserVersion(browserVersion);
        userAgentEntity.setOperatingSystem(userAgent.getPlatform().toString()
                .concat(" ").concat(userAgent.getOs().toString()));
        userAgentEntity.setUa(ua);
        return userAgentEntity;
    }
}
