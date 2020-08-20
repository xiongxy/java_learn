package com.lagou.demo.controller;

import com.lagou.edu.mvcframework.annotations.CustomController;
import com.lagou.edu.mvcframework.annotations.CustomRequestMapping;
import com.lagou.edu.mvcframework.annotations.CustomSecurity;

/**
 * @author xxyWindows@hotmail.com
 */
@CustomController
@CustomRequestMapping("/security")
public class SecurityController {
    @CustomRequestMapping("/demo1")
    public String demo1(String username) {
        System.out.println(username + "验证通过");
        return "Welcome:[" + username + "]into: /security/demo01";
    }

    @CustomSecurity(roles = "zhangsan")
    @CustomRequestMapping("/demo2")
    public String demo2(String username) {
        System.out.println(username + "验证通过");
        return "Welcome:[" + username + "]into: /security/demo02";
    }

    @CustomSecurity(roles = {"lisi", "wangwu"})
    @CustomRequestMapping("/demo3")
    public String demo3(String username) {
        System.out.println(username + "验证通过");
        return "Welcome:[" + username + "]into: /security/demo03";
    }
}
