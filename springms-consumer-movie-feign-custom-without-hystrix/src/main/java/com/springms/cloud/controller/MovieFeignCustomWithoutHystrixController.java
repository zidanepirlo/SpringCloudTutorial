package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.feign.UserFeignCustomClient;
import com.springms.cloud.feign.UserFeignCustomSecondClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieFeignCustomWithoutHystrixController {

    @Autowired
    private UserFeignCustomClient userFeignCustomClient;

    @Autowired
    private UserFeignCustomSecondClient userFeignCustomSecondClient;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        System.out.println("======== findById Controller " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());
        return userFeignCustomClient.findById(id);
    }

    @GetMapping("/{serviceName}")
    public String findEurekaInfo(@PathVariable String serviceName){
        System.out.println("======== findEurekaInfo Controller " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());
        return userFeignCustomSecondClient.findEurekaInfo(serviceName);
    }
}
