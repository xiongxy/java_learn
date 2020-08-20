package com.lagou.demo.interceptor;

import com.lagou.edu.mvcframework.annotations.CustomSecurity;
import com.lagou.edu.mvcframework.annotations.CustomService;
import com.lagou.edu.mvcframework.interceptor.AdvancedInterceptor;
import com.lagou.edu.mvcframework.pojo.Handler;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xxyWi
 */
@CustomService
public class SecurityInterceptor implements AdvancedInterceptor {

    @Override
    public boolean preHandler(HttpServletRequest request, Handler handler) {
        Set<String> roles = new HashSet<>();
        if (handler.getController().getClass().isAnnotationPresent(CustomSecurity.class)) {
            CustomSecurity security = handler.getController().getClass().getAnnotation(CustomSecurity.class);
            if (security.roles().length > 0) {
                roles.addAll(Arrays.asList(security.roles()));
            }
        }

        if (handler.getMethod().isAnnotationPresent(CustomSecurity.class)) {
            CustomSecurity security = handler.getMethod().getAnnotation(CustomSecurity.class);
            if (security.roles().length > 0) {
                roles.addAll(Arrays.asList(security.roles()));
            }
        }
        if (roles.isEmpty()) {
            return true;
        }
        String username = request.getParameter("username");
        return roles.contains(username);
    }
}
