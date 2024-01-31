package com.longkai.coupon.advice;

import com.longkai.coupon.annotation.IgnoreCommonResponseAdvice;
import com.longkai.coupon.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//拦截Rest接口的返回，对Rest接口进行增强
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice {
    //返回值决定是否需要对方法进行处理
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreCommonResponseAdvice.class)) {
            return false;
        }
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreCommonResponseAdvice.class)) {
            return false;
        }
        return true;
    }

    /**
     * @param o                  controller 返回对象
     * @param methodParameter    rest 方法参数
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return 返回response的处理结果
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        CommonResponse<Object> response = new CommonResponse<>(0, "");
        if (o == null) {
            return response;
        } else if (o instanceof CommonResponse) {
            response = (CommonResponse<Object>) o;
        } else {
            response.setData(o);
        }
        return response;
    }
}
