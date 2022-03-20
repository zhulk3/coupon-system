package com.longkai.coupon.feign.hystrix;

import com.longkai.coupon.feign.TemplateClient;
import com.longkai.coupon.vo.CommonResponse;
import com.longkai.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 熔断降级
 */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate request error");
        return new CommonResponse<>(-1,
                "[eureka-client-coupon-template] findAllUsableTemplate request error",
                Collections.emptyList());
    }

    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findId2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findId2TemplateSDK request error");

        return new CommonResponse<>(-1,
                "[eureka-client-coupon-template] findId2TemplateSDK request error",
                Collections.emptyMap());
    }
}
