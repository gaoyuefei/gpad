package com.gpad.common.security.interceptor;

import com.gpad.common.core.constant.SecurityConstants;
import com.gpad.common.core.context.SecurityContextHolder;
import com.gpad.common.core.utils.ServletUtils;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.common.security.auth.AuthUtil;
import com.gpad.common.security.utils.SecurityUtils;
import com.gpad.system.api.model.LoginUser;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author by
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor
{
    private static final String TRACE_ID = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (!(handler instanceof HandlerMethod))
        {
            return true;
        }

        SecurityContextHolder.setUserId(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USER_ID));
        SecurityContextHolder.setUserName(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USERNAME));
        SecurityContextHolder.setUserKey(ServletUtils.getHeader(request, SecurityConstants.USER_KEY));

        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token))
        {
            LoginUser loginUser = AuthUtil.getLoginUser(token);
            if (StringUtils.isNotNull(loginUser))
            {
                AuthUtil.verifyLoginUserExpire(loginUser);
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
            }
        }
        String traceId = request.getHeader(TRACE_ID);
        if (StringUtils.isEmpty(traceId)) {
            String val = UUID.randomUUID().toString().replaceAll("-", "");
            MDC.put("traceId", val);
        } else {
            MDC.put("traceId", traceId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception
    {
        //防止内存泄露
        MDC.remove(TRACE_ID);
        SecurityContextHolder.remove();
    }
}
