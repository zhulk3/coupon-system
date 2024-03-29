package com.longkai.coupon.filter;

import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

//@Component
@Slf4j
@Component
public class TokenFilter extends AbstractPreZuulFilter {
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s request to %s",
                request.getMethod(), request.getRequestURL().toString()));
        Object token = request.getParameter("token");
        if (null == token) {
            return fail(401, "error is empty");
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
