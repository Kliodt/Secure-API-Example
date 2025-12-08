package ru.secureapiexample.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private static final JWTService jwtService = new JWTService();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader("Authorization");

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var maybeUserId = jwtService.parseToken(accessToken);
        if (maybeUserId.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        var auth = new UsernamePasswordAuthenticationToken(maybeUserId.get(), null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
