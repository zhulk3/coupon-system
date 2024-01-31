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
 * 根据用户结算信息{@link SettlementInfo 选择结算执行器}
 * {@link BeanPostProcessor 所有的Bean都被创建以后再执行其中的方法}
 */

@Component
@Slf4j
public class ExecutorManager implements BeanPostProcessor {
    /**
     * 规则执行器映射
     */
    private static Map<RulerFlag, RulerExecutor> executorMap = new HashMap<>(RulerFlag.values().length);

    /**
     * 优惠券结算规则入口
     *
     * @param settlementInfo 需要确保其中存在优惠券
     * @return
     * @throws CouponException
     */
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
                //目前只支持两张优惠券
                throw new CouponException("not support for more template coupon");
            } else {
                if (categories.contains(CouponCategory.FULL_DISCOUNT) && categories.contains(CouponCategory.DISCOUNT)) {
                    result = executorMap.get(RulerFlag.FULL_DISCOUNT_AND_DISCOUNT).computeRuler(settlementInfo);
                } else {
                    //目前只支持满减+折扣一起使用，可以扩展
                    throw new CouponException("not support for other combination");
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
        log.info("load executor");
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
