package com.longkai.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        long startTime = (long) context.get("startTime");
        String url = request.getRequestURI();
        long duration = System.currentTimeMillis() - startTime;
        log.info("url: {}, duration: {}", url, duration);
        return null;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}
