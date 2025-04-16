package com.demo.springbootprovider.service.impl;

import com.demo.springbootinterface.entity.User;
import com.demo.springbootinterface.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @Author tfan
 * @Description
 * @Date 2025/4/16 18:09
 **/
@Slf4j
@DubboService
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Long id) {
        log.info("provider ====> getUserById");
        return new User(1L, "Tom", "123456");
    }
}
