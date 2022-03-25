package com.longkai.coupon.service;

import com.longkai.coupon.constant.RulerFlag;
import com.longkai.coupon.vo.SettlementInfo;

public interface RulerExecutor {
    RulerFlag rulerConfig();

    SettlementInfo computeRuler(SettlementInfo settlementInfo);
}
