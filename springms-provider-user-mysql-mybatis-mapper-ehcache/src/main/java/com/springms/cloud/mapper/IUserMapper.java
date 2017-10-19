package com.springms.cloud.mapper;

import com.springms.cloud.entity.User;

import java.util.List;

/**
 * 用户 mybatis 接口文件。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017-10-19
 *
 */
public interface IUserMapper {

    User findUserById(Long id);

    List<User> findAllUsers();

    int insertUser(User user);

    int updateUser(User user);

    int deleteUser(Long id);
}