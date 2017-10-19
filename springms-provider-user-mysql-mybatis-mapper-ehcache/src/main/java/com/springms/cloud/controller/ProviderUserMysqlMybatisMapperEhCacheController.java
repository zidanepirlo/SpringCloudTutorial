package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.service.IUserService;
import org.hibernate.cache.CacheException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户微服务Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017-10-19
 *
 */
@RestController
public class ProviderUserMysqlMybatisMapperEhCacheController {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ProviderUserMysqlMybatisMapperEhCacheController.class);

    @Autowired
    private IUserService iUserService;

    @GetMapping("/user/{id}")
    public User findUserById(@PathVariable Long id) {
        return this.iUserService.findUserById(id);
    }

    @GetMapping("/user/list")
    public List<User> findUserList() {
        return this.iUserService.findAllUsers();
    }

    /**
     * 添加一个student,使用postMapping接收post请求
     *
     * http://localhost:8330/simple/addUser?username=user11&age=11&balance=11
     *
     * @return
     */
    @PostMapping("/user/addUser")
    public User addUser(@RequestParam(value = "username", required=false) String username, @RequestParam(value = "age", required=false) Integer age, @RequestParam(value = "balance", required=false) String balance){
        User user=new User();

        user.setUsername(username);
        user.setName(username);
        user.setAge(age);
        user.setBalance(balance);

        int result = iUserService.insertUser(user);
        if(result > 0){
            return user;
        }

        user.setId(0L);
        user.setName(null);
        user.setUsername(null);
        user.setBalance(null);
        return user;
    }

    @GetMapping("/user/ehcache")
    public String ehcache() {
        Logger.info("===========  进行Encache缓存测试");

        List<User> allUsers = iUserService.findAllUsers();
        User lastUser = allUsers.get(allUsers.size() - 1);
        String lastUserUsername = lastUser.getUsername();
        String indexString = lastUserUsername.substring(4);

        Logger.info("===========  ====生成第一个用户====");
        User user1 = new User();
        //生成第一个用户的唯一标识符 UUID
        user1.setName("user" + (Integer.parseInt(indexString) + 1));
        user1.setUsername(user1.getName());
        user1.setAge(1000);
        user1.setBalance("1000");
        if (iUserService.insertUser(user1) == 0){
            throw new CacheException("用户对象插入数据库失败");
        }

        allUsers = iUserService.findAllUsers();
        lastUser = allUsers.get(allUsers.size() - 1);
        Long lastUserId = lastUser.getId();

        //第一次查询
        Logger.info("===========  第一次查询");
        Logger.info("===========  第一次查询结果: {}", iUserService.findUserById(lastUserId));
        //通过缓存查询
        Logger.info("===========  通过缓存第 1 次查询");
        Logger.info("===========  通过缓存第 1 次查询结果: {}", iUserService.findUserById(lastUserId));
        Logger.info("===========  通过缓存第 2 次查询");
        Logger.info("===========  通过缓存第 2 次查询结果: {}", iUserService.findUserById(lastUserId));
        Logger.info("===========  通过缓存第 3 次查询");
        Logger.info("===========  通过缓存第 3 次查询结果: {}", iUserService.findUserById(lastUserId));

        Logger.info("===========  ====准备修改数据====");
        User user2 = new User();
        user2.setName(lastUser.getName());
        user2.setUsername(lastUser.getUsername());
        user2.setAge(lastUser.getAge() + 1000);
        user2.setBalance(String.valueOf(user2.getAge()));
        user2.setId(lastUserId);
        try {
            int result = iUserService.updateUser(user2);
            Logger.info("===========  ==== 修改数据 == {} ==", (result > 0? "成功":"失败"));
        } catch (CacheException e){
            e.printStackTrace();
        }

        Logger.info("===========  ====修改后再次查询数据");
        Object resultObj = iUserService.findUserById(lastUser.getId());
        Logger.info("===========  ====修改后再次查询数据结果: {}", resultObj);
        return "success";
    }
}
