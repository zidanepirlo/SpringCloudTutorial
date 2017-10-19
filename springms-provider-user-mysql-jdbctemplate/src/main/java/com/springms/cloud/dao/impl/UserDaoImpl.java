package com.springms.cloud.dao.impl;

import com.springms.cloud.dao.IUserDao;
import com.springms.cloud.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 简单用户链接Mysql数据库微服务（通过@Repository注解标注该类为持久化操作对象）。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@Repository
public class UserDaoImpl implements IUserDao {

    /**
     * 通过@Resource注解引入JdbcTemplate对象。
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //@Transactional(readOnly = true)
    @Override
    public User findUserById(Long id){
        // 1. 定义一个sql语句
        String querySQL = "select * from user where id = ?";

        // 2. 定义一个RowMapper
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);

        // 3. 执行查询方法

        //List<User> list = jdbcTemplate.query("select * from account where id = ?", new Object[]{id}, new BeanPropertyRowMapper(User.class));

        User user = jdbcTemplate.queryForObject(querySQL, new Object[]{id}, rowMapper);

        return user;
    }

    //@Transactional(readOnly = true)
    @Override
    public List<User> findAllUsers(){
        // 1. 定义一个sql语句
        String querySQL = "select * from user";

        // 2. 定义一个RowMapper
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);

        // 3. 执行查询方法
        List<User> users = jdbcTemplate.query(querySQL, new Object[]{}, rowMapper);

        return users;
    }

    @Override
    public int insertUser(User user) {
        // 1. 定义一个sql语句
        String execSQL = "INSERT into user (username, name, age, balance) values (?, ?, ?, ?)";

        // 2. 执行查询方法
        return jdbcTemplate.update(execSQL,
                new Object[]{user.getUsername(), user.getName(), user.getAge(), user.getBalance()});
    }
}