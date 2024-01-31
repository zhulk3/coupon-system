package com.longkai.coupon.controller;

import com.longkai.coupon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
public class HealthChecker {
    //服务发现客户端
    private final DiscoveryClient client;
    //服务注册接口，提供了获取服务id的方法
    private final Registration registration;

    @Autowired
    public HealthChecker(DiscoveryClient client, Registration registration) {
        this.client = client;
        this.registration = registration;
    }

    /**
     * <h2>健康检查接口</h2>
     * 127.0.0.1:7001/coupon-template/template/health
     */
    @GetMapping("/health")
    public String isHealthy() {
        log.debug(("view health API"));
        return "CouponTemplate Is OK";
    }

    /**
     * 异常测试接口
     * @return
     * @throws CouponException
     */
    @GetMapping("/exception")
    public String exception() throws CouponException {
        log.debug(("view exception opt"));
        throw new CouponException("CouponTemplate Has Some Problem");
    }

    /**
     * <h2>获取Eureka Server 上微服务的信息</h2>
     *
     * @return
     */
    @GetMapping("/template/info")
    public List<Map<String, Object>> info() {
        List<ServiceInstance> instances = client.getInstances(registration.getServiceId());
        List<Map<String, Object>> result = new ArrayList<>(instances.size());
        instances.forEach(t -> {
            Map<String, Object> info = new HashMap<>();
            info.put("serviceId", t.getServiceId());
            info.put("instanceId", t.getInstanceId());
            info.put("port", t.getPort());
            result.add(info);
        });
        return result;
    }
}
