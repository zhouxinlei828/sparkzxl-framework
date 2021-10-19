package com.github.sparkzxl.distributed.cloud.http;

import com.github.sparkzxl.constant.AppContextConstants;
import com.github.sparkzxl.core.context.AppContextHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * description: 通过 RestTemplate 调用时，传递请求头和线程变量
 *
 * @author zhouxinlei
 * @date 2021-10-19 14:27:13
 */
@AllArgsConstructor
@Slf4j
public class RestTemplateHeaderInterceptor implements ClientHttpRequestInterceptor {

    public static final List<String> HEADER_NAME_LIST = Arrays.asList(
            AppContextConstants.TENANT_ID, AppContextConstants.JWT_KEY_USER_ID,
            AppContextConstants.JWT_KEY_ACCOUNT, AppContextConstants.JWT_KEY_NAME,
            AppContextConstants.TRACE_ID_HEADER, AppContextConstants.JWT_TOKEN_HEADER, "X-Real-IP", "x-forwarded-for"
    );

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes,
                                        ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders httpHeaders = request.getHeaders();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            HEADER_NAME_LIST.forEach((headerName) -> httpHeaders.add(headerName, AppContextHolder.get(headerName)));
            return execution.execute(request, bytes);
        }

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (request == null) {
            log.warn("path={}, 在FeignClient API接口未配置FeignConfiguration类， 故而无法在远程调用时获取请求头中的参数!", request.getURI());
            return execution.execute(request, bytes);
        }
        HEADER_NAME_LIST.forEach((headerName) -> {
            String header = httpServletRequest.getHeader(headerName);
            httpHeaders.add(headerName, StringUtils.isEmpty(header) ? AppContextHolder.get(headerName) : header);
        });
        return execution.execute(request, bytes);
    }
}
