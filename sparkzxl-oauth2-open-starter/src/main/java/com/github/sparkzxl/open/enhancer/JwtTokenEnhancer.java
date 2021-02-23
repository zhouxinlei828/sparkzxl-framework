package com.github.sparkzxl.open.enhancer;

import com.github.sparkzxl.open.entity.AuthUserDetail;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

/**
 * description: Jwt内容增强器
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:24:10
 */
public class JwtTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        AuthUserDetail userDetails = (AuthUserDetail) oAuth2Authentication.getPrincipal();
        Map<String, Object> additionalInfo = Maps.newLinkedHashMap();
        additionalInfo.put("id", userDetails.getId());
        additionalInfo.put("username", userDetails.getUsername());
        additionalInfo.put("name", userDetails.getName());
        String tenant = userDetails.getTenant();
        if (StringUtils.isNotEmpty(tenant)) {
            additionalInfo.put("tenant", tenant);
        }
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
        return oAuth2AccessToken;
    }
}
