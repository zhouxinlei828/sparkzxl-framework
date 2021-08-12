package org.springframework.web.servlet;

import com.github.sparkzxl.constant.BaseContextConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: 重写DispatcherServlet，处理feign请求被全局异常统一处理吃掉的逻辑
 *
 * @author zhouxinlei
 * @date 2021-08-03 08:44:48
 */
public class CustomDispatcherServlet extends DispatcherServlet {

    public CustomDispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
    }

    @Override
    protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) throws Exception {
        String header = request.getHeader(BaseContextConstants.REMOTE_CALL);
        if (StringUtils.isNotEmpty(header)) {
            throw ex;
        } else {
            return super.processHandlerException(request, response, handler, ex);
        }
    }

}
