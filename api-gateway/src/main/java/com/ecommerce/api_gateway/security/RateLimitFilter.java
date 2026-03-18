package com.ecommerce.api_gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStart = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long WINDOW_SIZE_MS = 60_000;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIp(request);
        long now = System.currentTimeMillis();

        windowStart.putIfAbsent(clientIp, now);
        requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));

        long windowAge = now - windowStart.get(clientIp);
        if (windowAge > WINDOW_SIZE_MS) {
            windowStart.put(clientIp, now);
            requestCounts.get(clientIp).set(0);
        }

        int count = requestCounts.get(clientIp).incrementAndGet();

        if (count > MAX_REQUESTS_PER_MINUTE) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Max 60 requests per minute.\",\"status\":429}"
            );
            return;
        }

        response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(MAX_REQUESTS_PER_MINUTE - count));
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}