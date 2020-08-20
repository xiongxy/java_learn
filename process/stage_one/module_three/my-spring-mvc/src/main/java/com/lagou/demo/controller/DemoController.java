package com.lagou.demo.controller;

import com.lagou.demo.service.IDemoService;
import com.lagou.edu.mvcframework.annotations.CustomAutowired;
import com.lagou.edu.mvcframework.annotations.CustomController;
import com.lagou.edu.mvcframework.annotations.CustomRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xxywindows@hotmail.com
 */
@CustomController
@CustomRequestMapping("/demo")
public class DemoController {

    @CustomAutowired
    private IDemoService demoService;

    /**
     * URL: /demo/query?name=lisi
     *
     * @param request
     * @param response
     * @param name
     * @return
     */
    @CustomRequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response, String name) {
        return demoService.get(name);
    }
}
