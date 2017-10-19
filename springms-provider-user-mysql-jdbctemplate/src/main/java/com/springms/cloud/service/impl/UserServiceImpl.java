package com.springms.cloud.service.impl;

import com.springms.cloud.dao.IUserDao;
import com.springms.cloud.entity.User;
import com.springms.cloud.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简单用户链接Mysql数据库微服务（通过@Service注解标注该类为持久化操作对象）。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserDao userDao;

    @Override
    public User findUserById(Long id){
        return userDao.findUserById(id);
    }

    @Override
    public List<User> findAllUsers(){
        return userDao.findAllUsers();
    }

    @Override
    public int insertUser(User user) {
        return userDao.insertUser(user);
    }
}