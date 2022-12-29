package com.ifans.common.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifans.common.core.domain.R;
import com.ifans.common.core.domain.Rcode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token 无效自定义异常
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), R.fail(Rcode.INVALID_TOKEN));
    }
}
