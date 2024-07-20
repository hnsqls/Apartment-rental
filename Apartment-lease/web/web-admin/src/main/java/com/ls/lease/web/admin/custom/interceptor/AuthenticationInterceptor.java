package com.ls.lease.web.admin.custom.interceptor;

import com.ls.lease.common.exception.LeaseException;
import com.ls.lease.common.login.LoginUser;
import com.ls.lease.common.login.LoginUserHolder;
import com.ls.lease.common.result.ResultCodeEnum;
import com.ls.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("access-token");

        Claims claims = JwtUtil.parseToken(token);
        Long userId = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);

        LoginUserHolder.setLoginUser(new LoginUser(userId,username));

        return  true;

    }

    /**
     * 清除线程信息，避免内存泄露
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserHolder.clear();
    }
}
