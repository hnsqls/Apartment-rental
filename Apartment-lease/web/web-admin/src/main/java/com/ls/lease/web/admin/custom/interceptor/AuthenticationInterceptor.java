package com.ls.lease.web.admin.custom.interceptor;

import com.ls.lease.common.exception.LeaseException;
import com.ls.lease.common.result.ResultCodeEnum;
import com.ls.lease.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("access-token");

        JwtUtil.parseToken(token);
        return  true;

    }
}
