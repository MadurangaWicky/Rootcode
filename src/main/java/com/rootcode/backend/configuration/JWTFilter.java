package com.rootcode.backend.configuration;

import com.rootcode.backend.entity.User;
import com.rootcode.backend.repository.UserRepository;
import com.rootcode.backend.service.UserService;
import com.rootcode.backend.utility.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;


    public JWTFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (accessToken != null && jwtUtil.isTokenValid(accessToken)) {
            String username = jwtUtil.getClaimsFromToken(accessToken).getSubject();
            User user = userService.getUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        else if (accessToken != null && !jwtUtil.isTokenValid(accessToken) && refreshToken != null) {
            if (jwtUtil.isTokenValid(refreshToken)) {
                String username = jwtUtil.getClaimsFromToken(refreshToken).getSubject();

                Map<String, Object> claims = Map.of("username", username);
                String newAccessToken = jwtUtil.generateAccessToken(claims, username);

                Cookie newAccessTokenCookie = new Cookie("accessToken", newAccessToken);
                newAccessTokenCookie.setPath("/");
                newAccessTokenCookie.setMaxAge(60 * 60);
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setSecure(true);


                response.addCookie(newAccessTokenCookie);

                User user = userService.getUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Refresh token is invalid or expired");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
