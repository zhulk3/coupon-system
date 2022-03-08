package com.longkai.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 再过滤器中存储请求到达网关的时间戳
 */
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter{
    @Override
    protected Object cRun() {
        context.set("startTime",System.currentTimeMillis());
        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
