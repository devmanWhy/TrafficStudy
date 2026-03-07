package com.devy.orders.config.ratelimiter;

import io.github.resilience4j.core.exception.AcquirePermissionCancelledException;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RateLimiterFilter implements Filter {

    private final RateLimiterRegistry rateLimiterRegistry;
    private RateLimiter rateLimiter;

    public RateLimiterFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.rateLimiter = rateLimiterRegistry.rateLimiter("default");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            RateLimiter.waitForPermission(rateLimiter);
            chain.doFilter(request, response);
        } catch (RequestNotPermitted | AcquirePermissionCancelledException e) {
            HttpServletResponse servletResponse = (HttpServletResponse) response;
            servletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            servletResponse.getWriter().write("Server Too Busy!!!");
        }

    }
}
