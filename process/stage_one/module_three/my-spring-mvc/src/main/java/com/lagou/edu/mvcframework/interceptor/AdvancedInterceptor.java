package com.lagou.edu.mvcframework.interceptor;

import com.lagou.edu.mvcframework.pojo.Handler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xxyWi
 */
public interface AdvancedInterceptor {
    /**
     * 预处理
     *
     * @param request
     * @param handler
     * @return
     */
    boolean preHandler(HttpServletRequest request, Handler handler);
}
