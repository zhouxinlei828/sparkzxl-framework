package org.springframework.web.servlet;

import cn.hutool.core.convert.Convert;
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
 */
public class CustomDispatcherServlet extends DispatcherServlet {

    public CustomDispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
    }

    @Override
    protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) throws Exception {
        Boolean feign = Convert.toBool(request.getHeader(BaseContextConstants.REMOTE_CALL), Boolean.FALSE);
        if (feign) {
            throw ex;
        } else {
            return super.processHandlerException(request, response, handler, ex);
        }
    }

}
