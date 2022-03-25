package com.longkai.coupon.service;

import com.longkai.coupon.constant.CouponCategory;
import com.longkai.coupon.constant.RulerFlag;
import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券结算规则执行管理器
 */

@Component
@Slf4j
public class ExecutorManager implements BeanPostProcessor {
    private static Map<RulerFlag, RulerExecutor> executorMap = new HashMap<>(RulerFlag.values().length);

    //优惠券结算规则入口
    public SettlementInfo computeRuler(SettlementInfo settlementInfo) throws CouponException {
        SettlementInfo result = null;
        if (settlementInfo.getCouponAndTemplateInfos().size() == 1) {
            CouponCategory category = CouponCategory.of(settlementInfo.getCouponAndTemplateInfos().get(0).getTemplateSDK().getCategory());
            switch (category) {
                case FULL_DISCOUNT:
                    result = executorMap.get(RulerFlag.FULL_DISCOUNT).computeRuler(settlementInfo);
                    break;
                case DISCOUNT:
                    result = executorMap.get(RulerFlag.DISCOUNT).computeRuler(settlementInfo);
                    break;
                case DISCOUNT_IMMEDIATE:
                    result = executorMap.get(RulerFlag.DISCOUNT_IMMEDIATE).computeRuler(settlementInfo);
                    break;
            }
        } else {
            List<CouponCategory> categories = new ArrayList<>(settlementInfo.getCouponAndTemplateInfos().size());
            settlementInfo.getCouponAndTemplateInfos().forEach(item -> categories.add(CouponCategory.of(item.getTemplateSDK().getCategory())));
            if (categories.size() != 2) {
                throw new CouponException("not support for more template coupon");
            } else {
                if (categories.contains(CouponCategory.FULL_DISCOUNT) && categories.contains(CouponCategory.DISCOUNT)) {
                    result = executorMap.get(RulerFlag.FULL_DISCOUNT_AND_DISCOUNT).computeRuler(settlementInfo);
                } else {
                    throw new CouponException("not support for other combine");
                }
            }
        }
        return result;
    }

    /**
     * 在bean初始化之前执行
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof RulerExecutor)) {
            return bean;
        }
        RulerExecutor executor = (RulerExecutor) bean;
        RulerFlag flag = executor.rulerConfig();
        if (executorMap.containsKey(executor)) {
            throw new IllegalStateException("there is already executor for " + flag);
        }
        executorMap.put(flag, executor);
        return null;
    }

    /**
     * 在bean初始化之后执行
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
