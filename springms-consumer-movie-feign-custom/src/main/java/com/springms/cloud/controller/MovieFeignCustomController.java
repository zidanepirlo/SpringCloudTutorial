package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.feign.UserFeignCustomClient;
import com.springms.cloud.feign.UserFeignCustomSecondClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自定义 Feign 控制器。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/19
 *
 */
@RestController
public class MovieFeignCustomController {

    @Autowired
    private UserFeignCustomClient userFeignCustomClient;

    @Autowired
    private UserFeignCustomSecondClient userFeignCustomSecondClient;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        return userFeignCustomClient.findById(id);
    }

    @GetMapping("/{serviceName}")
    public String findEurekaInfo(@PathVariable String serviceName){
        return userFeignCustomSecondClient.findEurekaInfo(serviceName);
    }
}
