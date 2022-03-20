package com.longkai.coupon.feign.hystrix;

import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.feign.SettlementClient;
import com.longkai.coupon.vo.CommonResponse;
import com.longkai.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结算微服务熔断策略实现
 */

@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    @Override
    public CommonResponse<SettlementInfo> computeRuler(SettlementInfo settlementInfo) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRuler request error");

        settlementInfo.setEmploy(false);
        settlementInfo.setCost(-1.0);
        return new CommonResponse<>(-1,
                "[eureka-client-coupon-settlement] computeRuler request error",
                settlementInfo);
    }
}
