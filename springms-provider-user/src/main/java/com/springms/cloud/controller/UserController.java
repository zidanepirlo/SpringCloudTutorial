package com.springms.cloud.controller;

import com.google.common.collect.Lists;
import com.springms.cloud.entity.User;
import com.springms.cloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户界面控制层。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private EurekaClient discoveryClient;

    @GetMapping("/simple/{id}")
    public User findById(@PathVariable Long id){
        return userRepository.findOne(id);
    }

//    public String serviceUrl(){
//        InstanceInfo instance = this.discoveryClient.getNextServerFromEureka("SPRINGMS-PROVIDER-USER", false);
//        return instance.getHomePageUrl();
//    }

    @PostMapping("/user")
    public User postUser(@RequestBody User user){
        System.out.println("@GetMapping(\"user\") 接收参数对象 user: " + user);
        return user;
    }

    @GetMapping("listAll")
    public List<User> listAll(){
        ArrayList<User> list = Lists.newArrayList();
        User user1 = new User(1L, "user1");
        User user2 = new User(1L, "user2");
        User user3 = new User(1L, "user3");
        User user4 = new User(1L, "user4");
        User user5 = new User(1L, "user5");
        list.add(user1);
        list.add(user2);
        list.add(user3);
        list.add(user4);
        list.add(user5);
        return list;
    }
}
