package org.springframework.security.oauth2.provider.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;

/**
 * description: 不走端点token获取
 *
 * @author: zhouxinlei
 * @date: 2021-02-24 12:17:37
 */
@Slf4j
public class CustomTokenGrantService {


    private final TokenEndpoint tokenEndpoint;

    private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();

    public CustomTokenGrantService(TokenEndpoint tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public OAuth2AccessToken getAccessToken(Map<String, String> parameters) {
        String clientId = parameters.get("client_id");
        ClientDetails authenticatedClient = tokenEndpoint.getClientDetailsService().loadClientByClientId(clientId);
        TokenRequest tokenRequest = tokenEndpoint.getOAuth2RequestFactory().createTokenRequest(parameters, authenticatedClient);
        if (clientId != null && !"".equals(clientId) && !clientId.equals(tokenRequest.getClientId())) {
            throw new InvalidClientException("Given client ID does not match authenticated client");
        } else {
            if (authenticatedClient != null) {
                this.oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
            }

            if (!StringUtils.hasText(tokenRequest.getGrantType())) {
                throw new InvalidRequestException("Missing grant type");
            } else if ("implicit".equals(tokenRequest.getGrantType())) {
                throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
            } else {
                if (this.isAuthCodeRequest(parameters) && !tokenRequest.getScope().isEmpty()) {
                    log.debug("Clearing scope of incoming token request");
                    tokenRequest.setScope(Collections.emptySet());
                }

                if (this.isRefreshTokenRequest(parameters)) {
                    tokenRequest.setScope(OAuth2Utils.parseParameterList(parameters.get("scope")));
                }

                OAuth2AccessToken token = tokenEndpoint.getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest);
                if (token == null) {
                    throw new UnsupportedGrantTypeException("Unsupported grant type: " + tokenRequest.getGrantType());
                } else {
                    return token;
                }
            }
        }
    }

    private boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return "refresh_token".equals(parameters.get("grant_type")) && parameters.get("refresh_token") != null;
    }

    private boolean isAuthCodeRequest(Map<String, String> parameters) {
        return "authorization_code".equals(parameters.get("grant_type")) && parameters.get("code") != null;
    }

}
