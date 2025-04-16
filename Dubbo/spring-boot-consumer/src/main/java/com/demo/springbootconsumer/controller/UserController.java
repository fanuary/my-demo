package com.demo.springbootconsumer.controller;

import com.demo.springbootinterface.entity.User;
import com.demo.springbootinterface.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author tfan
 * @Description
 * @Date 2025/4/16 18:22
 **/

@RestController
public class UserController {


    @DubboReference(check = false) // dubbo提供的Reference注解，用于调用远程服务
    private UserService userService;

    @GetMapping("/test")
    public String getUser() {
        User userById = userService.getUserById(1L);
        System.out.println(userById);
        return "success";
    }
}
