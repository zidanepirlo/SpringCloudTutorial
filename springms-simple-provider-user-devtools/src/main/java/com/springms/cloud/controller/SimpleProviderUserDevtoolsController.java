package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户微服务Controller（支持 idea 热部署）。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@RestController
public class SimpleProviderUserDevtoolsController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/simple/{id}")
    public User findById(@PathVariable Long id) {
        return this.userRepository.findOne(id);
    }

    @GetMapping("simple")
    public String simple() {
        return "simple-2017-09";
    }

    @GetMapping("simple2")
    public String simple2() {
        return "simple2-2017";
    }
}
