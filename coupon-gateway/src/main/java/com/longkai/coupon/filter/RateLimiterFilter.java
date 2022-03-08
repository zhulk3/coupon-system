package com.longkai.coupon.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 限流过滤器
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class RateLimiterFilter extends AbstractPreZuulFilter {

    RateLimiter rateLimiter = RateLimiter.create(100.0);

    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        if (rateLimiter.tryAcquire()) {
            log.info("get rate token success");
            return success();
        } else {
            log.info("rate limite: {}", request.getRequestURI());
            return fail(402, "error: rate limit");
        }
    }

    //通过token校验以后再进入本次过滤
    @Override
    public int filterOrder() {
        return 2;
    }
}
