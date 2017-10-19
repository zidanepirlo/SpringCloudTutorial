package com.springms.cloud.controller;

import com.springms.cloud.entity.Account;
import com.springms.cloud.entity.User;
import com.springms.cloud.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户微服务电影Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@RestController
public class MovieMysqlJdbcTransactionalController {

    @Autowired
    private IMovieService moiveService;

    /**
     * 添加一个student,使用postMapping接收post请求
     *
     * http://localhost:8335/simple/addUser?username=user11&age=11&balance=11
     *
     * @return
     */
    @PostMapping("/movie/addMovie")
    public User addMovie(@RequestParam(value = "username", required=false) String username, @RequestParam(value = "age", required=false) Integer age, @RequestParam(value = "balance", required=false) String balance){
        User user=new User();

        user.setUsername(username);
        user.setName(username);
        user.setAge(age);
        user.setBalance(balance);

        Account account = new Account();
        account.setName(username);
        account.setMoney(Double.parseDouble(balance));

        int result = moiveService.addMovie(user, account);
        if(result > 0){
            return user;
        }

        user.setId(0L);
        user.setName(null);
        user.setUsername(null);
        user.setBalance(null);
        return user;
    }

    /**
     * 添加一个student,使用postMapping接收post请求
     *
     * http://localhost:8335/simple/addUser?username=user11&age=11&balance=11
     *
     * @return
     */
    @PostMapping("/movie/insertMovie")
    public User insertMovie(@RequestParam(value = "username", required=false) String username, @RequestParam(value = "age", required=false) Integer age, @RequestParam(value = "balance", required=false) String balance) throws Exception {
        User user=new User();

        user.setUsername(username);
        user.setName(username);
        user.setAge(age);
        user.setBalance(balance);

        Account account = new Account();
        account.setName(username);
        account.setMoney(Double.parseDouble(balance));

        int result = moiveService.insertMovie(user, account);
        if(result > 0){
            return user;
        }

        user.setId(0L);
        user.setName(null);
        user.setUsername(null);
        user.setBalance(null);
        return user;
    }
}