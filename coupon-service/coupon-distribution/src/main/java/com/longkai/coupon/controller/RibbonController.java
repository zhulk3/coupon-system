package com.longkai.coupon.controller;

import com.longkai.coupon.annotation.IgnoreCommonResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j

public class RibbonController {
    private final RestTemplate restTemplate;

    @Autowired
    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 通过Ribbon组件调用模版微服务
     * @return
     */
    @GetMapping("/info")
    @IgnoreCommonResponseAdvice
    private TemplateInfo getTemplateInfo(){
        String url = "http://eureka-client-coupon-template/coupon-template/template/info";
        return restTemplate.getForEntity(url,TemplateInfo.class).getBody();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TemplateInfo {
        private Integer code;
        private String message;
        private List<Map<Integer, Object>> data;

    }
}
