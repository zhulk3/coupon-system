package com.longkai.coupon.advice;

import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * 1、不直接展示异常，对用户友好，
 * 2、异常分类，便于排查
 * 3、降低业务代码中对异常处理的耦合
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {
    //CouponException异常进行统一处理。对指定异常进行拦截
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handleCouponException(
            HttpServletRequest request, CouponException exception
    ) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setData(exception.getMessage());
        return response;
    }
}
