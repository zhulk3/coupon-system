package com.longkai.coupon.feign;

import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.feign.hystrix.SettlementClientHystrix;
import com.longkai.coupon.vo.CommonResponse;
import com.longkai.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//在调用过程中如果抛出异常，调用熔断器走回退策略
@FeignClient(value = "eureka-client-coupon-settlement", fallback = SettlementClientHystrix.class)
public interface SettlementClient {
    @RequestMapping(value = "/coupon-settlement/settlement/compute", method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRuler(@RequestBody SettlementInfo settlementInfo) throws CouponException;
}
