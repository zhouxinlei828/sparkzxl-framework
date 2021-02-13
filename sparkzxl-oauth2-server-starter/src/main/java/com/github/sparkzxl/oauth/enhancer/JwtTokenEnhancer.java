package com.github.sparkzxl.oauth.enhancer;

import com.github.sparkzxl.oauth.entity.AuthUserDetail;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
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
        Map<String, Object> info = Maps.newLinkedHashMap();
        info.put("id", userDetails.getId());
        info.put("username", userDetails.getUsername());
        info.put("name", userDetails.getName());
        String tenant = userDetails.getTenant();
        if (StringUtils.isNotEmpty(tenant)) {
            info.put("tenant", tenant);
        }
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);
        return oAuth2AccessToken;
    }
}
