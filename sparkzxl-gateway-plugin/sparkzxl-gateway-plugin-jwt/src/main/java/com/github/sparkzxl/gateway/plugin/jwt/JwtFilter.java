package com.github.sparkzxl.gateway.plugin.jwt;

import cn.hutool.core.bean.OptionalBean;
import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.support.JwtExpireException;
import com.github.sparkzxl.core.support.JwtInvalidException;
import com.github.sparkzxl.core.util.DateUtils;
import com.github.sparkzxl.core.util.HuSecretUtil;
import com.github.sparkzxl.gateway.plugin.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.plugin.common.constant.enums.FilterEnum;
import com.github.sparkzxl.gateway.plugin.common.entity.FilterData;
import com.github.sparkzxl.gateway.plugin.common.utils.ReactorHttpHelper;
import com.github.sparkzxl.gateway.plugin.filter.AbstractGlobalFilter;
import com.github.sparkzxl.gateway.plugin.jwt.handle.JwtRuleHandle;
import com.github.sparkzxl.gateway.plugin.rule.RuleData;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: jwt filter
 *
 * @author zhouxinlei
 * @since 2022-01-08 23:35:22
 */
@Slf4j
public class JwtFilter extends AbstractGlobalFilter {

    @Override
    protected String named() {
        return FilterEnum.JWT.getName();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        FilterData filterData = loadFilterData();
        boolean needSkip = (boolean) exchange.getAttributes().get(GatewayConstant.NEED_SKIP);
        String jsonConfig =
                OptionalBean.ofNullable(filterData).getBean(FilterData::getConfig).orElseGet(() -> "{\"secretKey\":\"\",\"tokenKey\":\"Authorization\"}");
        JwtConfig jwtConfig = JsonUtil.toPojo(jsonConfig, JwtConfig.class);
        String dataHandle = OptionalBean.ofNullable(filterData.getRule()).getBean(RuleData::getHandle).orElseGet(
                () -> "{\"converter\":[{\"headerVal\":\"userid\",\"jwtVal\":\"id\"},{\"headerVal\":\"account\",\"jwtVal\":\"username\"},{\"headerVal\":\"name\",\"jwtVal\":\"name\"}]}");
        JwtRuleHandle ruleHandle = JsonUtil.toPojo(dataHandle, JwtRuleHandle.class);
        if (needSkip) {
            return removeAuthorization(exchange, chain, jwtConfig.getTokenKey());
        }
        String token = exchange.getRequest().getHeaders().getFirst(jwtConfig.getTokenKey());
        String authToken = StringUtils.removeStartIgnoreCase(token, BaseContextConstants.BEARER_TOKEN);
        JsonNode jwtBody = checkAuthorization(authToken, jwtConfig.getSecretKey());
        if (ObjectUtils.isNotEmpty(jwtBody)) {
            if (ObjectUtils.isNotEmpty(ruleHandle)) {
                return chain.filter(converter(exchange, jwtBody, ruleHandle.getConverter()));
            }
        }
        return ReactorHttpHelper.error(exchange.getResponse(), ExceptionErrorCode.USER_IDENTITY_VERIFICATION_ERROR);
    }

    @Override
    public int getOrder() {
        return FilterEnum.JWT.getCode();
    }

    /**
     * check Authorization.
     *
     * @param token     token
     * @param secretKey secretKey of authorization
     * @return Map
     */
    private JsonNode checkAuthorization(final String token,
                                        final String secretKey) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return Try.of(() -> {
            JWSObject jwsObject = JWSObject.parse(token);
            if (StringUtils.isNotEmpty(secretKey)) {
                JWSVerifier jwsVerifier = new MACVerifier(HuSecretUtil.encryptMd5(secretKey));
                if (!jwsObject.verify(jwsVerifier)) {
                    throw new JwtInvalidException("token验签失败");
                }
            }
            JsonNode jsonNode = JsonUtil.readTree(jwsObject.getPayload().toString());
            long expire = jsonNode.get("exp").asLong(0);
            DateTime dateTime = DateUtils.date(expire * 1000);
            if (dateTime.getTime() < System.currentTimeMillis()) {
                throw new JwtExpireException("token已过期");
            }
            return jsonNode;
        }).getOrElseThrow(throwable -> {
            log.error("JSON转换异常：", throwable);
            throw new JwtInvalidException(throwable);
        });
    }

    /**
     * remove Authorization.
     *
     * @param exchange exchange
     * @param chain    chain
     * @param tokenKey token请求头
     * @return Mono<Void>
     */
    private Mono<Void> removeAuthorization(ServerWebExchange exchange, GatewayFilterChain chain, String tokenKey) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove(tokenKey)).build();
        exchange.mutate().request(serverHttpRequest).build();
        return chain.filter(exchange.mutate().request(serverHttpRequest).build());
    }

    /**
     * The parameters in token are converted to request header.
     *
     * @param exchange exchange
     * @return ServerWebExchange exchange.
     */
    private ServerWebExchange converter(final ServerWebExchange exchange,
                                        final JsonNode jwtBody,
                                        final List<JwtRuleHandle.Convert> converters) {
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().headers(httpHeaders -> this.addHeader(httpHeaders, jwtBody, converters)).build();
        return exchange.mutate().request(modifiedRequest).build();
    }

    /**
     * add header.
     *
     * @param headers    headers
     * @param jsonNode   body
     * @param converters converters
     */
    private void addHeader(final HttpHeaders headers,
                           final JsonNode jsonNode,
                           final List<JwtRuleHandle.Convert> converters) {
        for (JwtRuleHandle.Convert converter : converters) {
            headers.add(converter.getHeaderVal(), jsonNode.get(converter.getJwtVal()).asText());
        }
    }
}
