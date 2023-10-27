package com.gpad.gpadtool.controller;

import com.gpad.common.core.web.controller.BaseController;
import com.gpad.gpadtool.domain.TestUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName TestUser.java
 * @Description TODO
 * @createTime 2023年09月20日 15:11:00
 */
@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

    @GetMapping("/hello")
    public TestUser test()
    {
        TestUser user= new TestUser();
        user.setUserName("张三.");
        user.setGender("男");
        user.setPhoneNumber("13666666666");
        return user;
    }
}
