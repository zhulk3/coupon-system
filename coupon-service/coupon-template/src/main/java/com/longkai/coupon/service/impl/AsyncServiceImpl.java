package com.longkai.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.longkai.coupon.constant.Constant;
import com.longkai.coupon.dao.CouponTemplateDao;
import com.longkai.coupon.entity.CouponTemplate;
import com.longkai.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AsyncServiceImpl implements IAsyncService {
    private final CouponTemplateDao couponTemplateDao;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao couponTemplateDao, StringRedisTemplate stringRedisTemplate) {
        this.couponTemplateDao = couponTemplateDao;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Async("getAsyncExecutor")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Set<String> couponCodes = buildCouponCode(template);
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("push code to redis: {}", stringRedisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
        template.setAvailable(true);
        couponTemplateDao.save(template);
        stopwatch.stop();
        log.info("construct CouponCode By Template Cost: {}", stopwatch.elapsed());
    }

    /**
     * <h2>构造优惠券码</h2>
     *
     * @param template {@link CouponTemplate}实体类
     * @return 与template.count 相同数量的优惠券码
     */
    private Set<String> buildCouponCode(CouponTemplate template) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Set<String> result = new HashSet<>(template.getCount());
        String prefix4 = template.getProductLine().getCode().toString() + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());
        for (int i = 0; i < template.getCount(); i++) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        assert result.size() == template.getCount();
        stopwatch.stop();
        log.info("build coupon code cost {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

    private String buildCouponCodeSuffix14(String date) {
        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        List<Character> chars = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        String mid6 = chars.stream().map(Objects::toString).collect(Collectors.joining());
        String suffix8 = RandomStringUtils.random(1, bases) + RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }
}
