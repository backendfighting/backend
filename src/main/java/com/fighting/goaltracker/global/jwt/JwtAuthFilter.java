package com.fighting.goaltracker.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 로그인, 회원가입, Swagger는 토큰 검증 없이 통과
        if (path.equals("/api/users/login") || path.equals("/api/users/signup")
                || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // 토큰이 없거나 형식이 잘못된 경우
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "로그인이 필요합니다.");
            return;
        }

        String token = authHeader.substring(7);

        // 토큰이 유효하지 않은 경우
        if (jwtUtil.isTokenExpired(token)) {
            sendUnauthorized(response, "토큰이 만료되었습니다. 다시 로그인해주세요.");
            return;
        }
        if (!jwtUtil.validateToken(token)) {
            sendUnauthorized(response, "유효하지 않은 토큰입니다.");
            return;
        }

        Integer userId = jwtUtil.extractUserId(token);
        request.setAttribute("userId", userId);
        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }
}
