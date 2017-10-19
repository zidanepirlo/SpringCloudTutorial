package com.springms.cloud.mapper;

import com.springms.cloud.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户 mybatis 映射文件。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017-10-19
 *
 */
public interface IUserMapper {

    @Select("select * from user where id = #{id}")
    User findUserById(Long id);

    @Select("select * from user")
    List<User> findAllUsers();

    @Insert("INSERT INTO user(username, name, age, balance) VALUES(#{username}, #{name}, #{age}, #{balance})")
    int insertUser(User user);
}