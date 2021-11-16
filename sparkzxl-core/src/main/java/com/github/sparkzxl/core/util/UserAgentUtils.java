package com.github.sparkzxl.core.util;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.sparkzxl.entity.core.UserAgentEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        String region = AddressUtil.getRegion(clientIp);
        if (StringUtils.isNotEmpty(region)) {
            JSONObject locationJsonObj = JSONObject.parseObject(region);
            String address = locationJsonObj.getString("region");
            String[] split = StringUtils.split(address, "|");
            split = ArrayUtils.removeAllOccurrences(split, "0");
            List<String> areaList = Arrays.stream(split).distinct().collect(Collectors.toList());
            String clientAddress = StringUtils.join(areaList, " ");
            userAgentEntity.setLocation(clientAddress);
        }
        userAgentEntity.setRequestIp(clientIp);
        userAgentEntity.setBrowser(userAgent.getBrowser().toString());
        userAgentEntity.setBrowserVersion(browserVersion);
        userAgentEntity.setOperatingSystem(userAgent.getPlatform().toString()
                .concat(" ").concat(userAgent.getOs().toString()));
        userAgentEntity.setUa(ua);
        userAgentEntity.setMobile(userAgent.isMobile());
        return userAgentEntity;
    }
}
