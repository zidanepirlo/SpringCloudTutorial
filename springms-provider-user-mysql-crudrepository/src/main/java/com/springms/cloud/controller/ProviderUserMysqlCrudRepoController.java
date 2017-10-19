package com.springms.cloud.controller;

import com.springms.cloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.springms.cloud.entity.User;

/**
 * 用户微服务Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@RestController
public class ProviderUserMysqlCrudRepoController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/simple/{id}")
    public User findUserById(@PathVariable Long id) {
        return this.userRepository.findOne(id);
    }

    @GetMapping("/simple/list")
    public Iterable<User> findUserList() {
        return this.userRepository.findAll();
    }

    /**
     * 添加一个student,使用postMapping接收post请求
     *
     * http://localhost:8320/simple/addUser?username=user11&age=11&balance=11
     *
     * @return
     */
    @PostMapping("/simple/addUser")
    public User addUser(@RequestParam(value = "username", required=false) String username, @RequestParam(value = "age", required=false) Integer age, @RequestParam(value = "balance", required=false) String balance){
        User user=new User();

        user.setUsername(username);
        user.setName(username);
        user.setAge(age);
        user.setBalance(balance);

        return userRepository.save(user);
    }
}
