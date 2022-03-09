package com.longkai.coupon.advice;

import com.longkai.coupon.exception.CouponException;
import com.longkai.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {
    //CouponException异常进行统一处理
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handleCouponException(
            HttpServletRequest request, CouponException exception
    ) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setData(exception.getMessage());
        return response;
    }
}
