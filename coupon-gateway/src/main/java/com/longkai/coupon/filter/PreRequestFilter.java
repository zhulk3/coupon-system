package com.longkai.coupon.filter;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 在过滤器中存储请求到达网关的时间戳
 */
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter{
    @Autowired
    EurekaClient eurekaClient;
    @Override
    protected Object cRun() {
        context.set("startTime",System.currentTimeMillis());
        try {
            List<Application> serviceNames = eurekaClient.getApplications().getRegisteredApplications();
            if (!serviceNames.isEmpty()) {
                log.info("Available services from Eureka: " + serviceNames.toString());
            } else {
                log.info("No services available from Eureka.");
            }
        } catch (Exception e) {
            log.error("Failed to get services from Eureka.", e);
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
