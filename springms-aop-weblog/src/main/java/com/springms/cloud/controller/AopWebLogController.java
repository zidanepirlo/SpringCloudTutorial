package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.springms.cloud.repository.UserRepository;

/**
 * 用户微服务Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@RestController
public class AopWebLogController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/simple/{id}")
    public User findById(@PathVariable Long id) {
        return this.userRepository.findOne(id);
    }
}
