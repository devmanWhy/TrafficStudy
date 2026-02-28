package com.devy.trafficstudypart3.config.rateLimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
public class RateLimiterFilter implements Filter {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final RateLimiter rateLimiter;


    public RateLimiterFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.rateLimiter = rateLimiterRegistry.rateLimiter("all");
    }

    /**
     * Filter 에 모든 요청에 대한 처리율 제한 설정을 했다.
     * 그러면 서버가 바쁠 때 대기 시키지 않고 바로 내보낼수있다.
     * Fast Fail
     * 기존에는 서버의 처리율 제한이 없어서 큐에 요청이 쌓이고, 요청을 처리하는 속도도 느려서
     * 무한정 대기를해야했음.
     *
     * 그런데 우리는 이제 처리율 제한을 해서
     * 원하는 만큼만 요청을 받고 나머지는 대기시키거나, 내보낼 수 있게 되었음.
     *
     * Admin 과 같은 곳에서 설정을 하면 동적으로 처리율 제한을 할수도 있는 초석이 되었습니다.
     *
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this filter to pass the request and response
     *                     to for further processing
     *
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            RateLimiter.waitForPermission(this.rateLimiter);
            chain.doFilter(request, response);
        } catch (RequestNotPermitted e) {
            e.printStackTrace();
            HttpServletResponse servletResponse = (HttpServletResponse) response;
            servletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            servletResponse.getWriter().write("Server Too Busy!!!");
        }

    }
}
