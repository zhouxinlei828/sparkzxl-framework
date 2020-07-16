package com.sparksys.core.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import com.sparksys.core.entity.UserAgentEntity;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

import javax.servlet.http.HttpServletRequest;

/**
 * description: 用户代理工具类
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 17:16:41
 */
public class UserAgentUtils extends UserAgentUtil {

    private final static String[] BROWSER = new String[]{
            "Chrome", "Firefox", "Microsoft Edge", "Safari", "Opera"
    };
    private final static String[] OPERATING_SYSTEM = new String[]{
            "Android", "Linux", "Mac OS X", "Ubuntu", "Windows 10", "Windows 8", "Windows 7", "Windows XP", "Windows Vista"
    };

    private static String simplifyOperatingSystem(String operatingSystem) {
        for (String b : OPERATING_SYSTEM) {
            if (StrUtil.containsIgnoreCase(operatingSystem, b)) {
                return b;
            }
        }
        return operatingSystem;
    }

    private static String simplifyBrowser(String browser) {
        for (String b : BROWSER) {
            if (StrUtil.containsIgnoreCase(browser, b)) {
                return b;
            }
        }
        return browser;
    }

    public static UserAgentEntity getUserAgentEntity() {
        UserAgentEntity userAgentEntity = new UserAgentEntity();
        HttpServletRequest request = HttpCommonUtils.getRequest();
        String ua = StrUtil.sub(request.getHeader("user-agent"), 0, 500);
        String ip = HttpCommonUtils.getIpAddress();
        UserAgent userAgent = UserAgent.parseUserAgentString(ua);
        Browser browser = userAgent.getBrowser();
        Version browserVersion = userAgent.getBrowserVersion();
        String version = browserVersion != null ? browserVersion.getVersion() : null;
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        userAgentEntity.setLocation(AddressUtil.getRegion(ip));
        userAgentEntity.setRequestIp(ip);
        userAgentEntity.setBrowser(simplifyBrowser(browser.getName()));
        userAgentEntity.setBrowserVersion(version);
        userAgentEntity.setOperatingSystem(simplifyOperatingSystem(operatingSystem.getName()));
        userAgentEntity.setUa(ua);
        return userAgentEntity;

    }

}
