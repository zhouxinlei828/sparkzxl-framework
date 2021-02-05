package com.github.sparkzxl.oauth.config;

import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.core.utils.HuSecretUtils;
import com.github.sparkzxl.core.utils.ListUtils;
import com.github.sparkzxl.jwt.properties.KeyStoreProperties;
import com.github.sparkzxl.oauth.enhancer.JwtTokenEnhancer;
import com.github.sparkzxl.oauth.enums.StoreTypeEnum;
import com.github.sparkzxl.oauth.properties.Oauth2Properties;
import com.github.sparkzxl.oauth.supports.Oauth2ExceptionHandler;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * description: 认证服务器配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:23:36
 */
@Configuration
@EnableConfigurationProperties(Oauth2Properties.class)
@EnableAuthorizationServer
@Slf4j
@Import({Oauth2ExceptionHandler.class})
public class Oauth2ServerAutoConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;
    @Autowired(required = false)
    private UserDetailsService userDetailsService;
    @Autowired(required = false)
    private AuthenticationManager authenticationManager;
    @Autowired(required = false)
    private Oauth2Properties oAuth2Properties;
    @Autowired(required = false)
    private KeyStoreProperties keyStoreProperties;
    @Autowired(required = false)
    private DataSource dataSource;
    @Autowired(required = false)
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public JwtTokenEnhancer jwtTokenEnhancer() {
        return new JwtTokenEnhancer();
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        if (ObjectUtils.isNotEmpty(keyStoreProperties) && keyStoreProperties.isEnable()) {
            KeyPair keyPair = HuSecretUtils.keyPair(keyStoreProperties.getPath(),
                    keyStoreProperties.getAlias(), keyStoreProperties.getPassword());
            Optional.ofNullable(keyPair).ifPresent(jwtAccessTokenConverter::setKeyPair);
        }
        return jwtAccessTokenConverter;
    }

    /**
     * access_token存储器
     * 这里存储在数据库，大家可以结合自己的业务场景考虑将access_token存入数据库还是redis
     */
    @Bean
    public TokenStore tokenStore() {
        if (oAuth2Properties.getStore().equals(StoreTypeEnum.DATABASE)) {
            return new JdbcTokenStore(dataSource);
        } else {
            return new RedisTokenStore(redisConnectionFactory);
        }
    }

    @Bean
    public ClientDetailsService clientDetailsService() {
        if (oAuth2Properties.getStore().equals(StoreTypeEnum.DATABASE)) {
            return new JdbcClientDetailsService(dataSource);

        } else {
            InMemoryClientDetailsService memoryClientDetailsService = new InMemoryClientDetailsService();
            Map<String, ClientDetails> clientDetailsStore = Maps.newHashMap();
            BaseClientDetails clientDetails = new BaseClientDetails();
            clientDetails.setClientId(oAuth2Properties.getClientId());
            clientDetails.setClientSecret(passwordEncoder.encode(oAuth2Properties.getClientSecret()));
            clientDetails.setAccessTokenValiditySeconds(oAuth2Properties.getAccessTokenValiditySeconds());
            clientDetails.setRefreshTokenValiditySeconds(oAuth2Properties.getRefreshTokenValiditySeconds());
            clientDetails.setRegisteredRedirectUri(Sets.newHashSet(oAuth2Properties.getRegisteredRedirectUris()));
            clientDetails.setScope(ListUtils.arrayToList(oAuth2Properties.getScopes()));
            clientDetails.setAuthorizedGrantTypes(ListUtils.arrayToList(oAuth2Properties.getAuthorizedGrantTypes()));
            clientDetailsStore.put(oAuth2Properties.getClientId(), clientDetails);
            memoryClientDetailsService.setClientDetailsStore(clientDetailsStore);
            return memoryClientDetailsService;
        }
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        log.info("OAuth2Properties：{}", JSONUtil.toJsonPrettyStr(oAuth2Properties));
        clients.withClientDetails(clientDetailsService());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenEnhancer());
        //配置JWT的内容增强器
        delegates.add(accessTokenConverter());
        enhancerChain.setTokenEnhancers(delegates);
        endpoints.setClientDetailsService(clientDetailsService());
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                //配置令牌存储策略
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(enhancerChain);

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients() //如果使用表单认证则需要加上
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }
}
