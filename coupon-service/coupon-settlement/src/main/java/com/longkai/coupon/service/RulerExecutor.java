package com.longkai.coupon.service;

import com.longkai.coupon.constant.RulerFlag;
import com.longkai.coupon.vo.SettlementInfo;

public interface RulerExecutor {

    /**
     * 规则类型标记 {@link RulerFlag}
     * @return
     */
    RulerFlag rulerConfig();

    /**
     * 优惠券规则的计算
     * @param settlementInfo {@link SettlementInfo}
     * @return
     */

    SettlementInfo computeRuler(SettlementInfo settlementInfo);
}
