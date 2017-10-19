package com.springms.cloud.service;

import com.springms.cloud.entity.Account;
import com.springms.cloud.entity.User;

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
public interface IMovieService {

    int addMovie(User user, Account account);

    int insertMovie(User user, Account account) throws Exception;
}
