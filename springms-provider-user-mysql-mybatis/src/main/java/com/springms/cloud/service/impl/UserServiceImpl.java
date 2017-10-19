package com.springms.cloud.service.impl;

import com.springms.cloud.entity.User;
import com.springms.cloud.mapper.IUserMapper;
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
 * @date 2017-10-19
 *
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    IUserMapper iUserMapper;

    @Override
    public User findUserById(Long id) {
        return iUserMapper.findUserById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return iUserMapper.findAllUsers();
    }

    @Override
    public int insertUser(User user) {
        return iUserMapper.insertUser(user);
    }
}