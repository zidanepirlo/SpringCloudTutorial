package com.springms.cloud.service.impl;

import com.springms.cloud.dao.IAccountDao;
import com.springms.cloud.dao.IUserDao;
import com.springms.cloud.entity.Account;
import com.springms.cloud.entity.User;
import com.springms.cloud.exception.BusinessExtendsException;
import com.springms.cloud.exception.BusinessSubExtendsException;
import com.springms.cloud.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
public class MovieServiceImpl implements IMovieService {

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IAccountDao accountDao;

    @Transactional(propagation = Propagation.REQUIRED, isolation= Isolation.DEFAULT, rollbackFor = Exception.class)
    @Override
    public int addMovie(User user, Account account) {

        int result1 = userDao.addUser(user);
        int result2 = accountDao.add(account);

        if(result1 > 0 || result2 > 0){
            throw new RuntimeException("抛出 RuntimeException 异常，测试 rollbackFor = Exception.class 是否有效？");
        }

        return 0;
    }

    @Transactional(noRollbackFor = BusinessExtendsException.class)
    @Override
    public int insertMovie(User user, Account account) throws Exception {

        int result1 = userDao.insertUser(user);
        int result2 = accountDao.add(account);

        if(result1 > 0 || result2 > 0){
            // throw new NullPointerException("抛出 NullPointerException 异常，测试 noRollbackFor = RuntimeException.class 是否有效？");
            // throw new RollbackExceptionExtendsException("抛出 RollbackExceptionExtendsException 异常，测试 noRollbackFor = RuntimeException.class 是否有效？");
            throw new BusinessSubExtendsException("抛出 BusinessSubExtendsException 异常，测试 noRollbackFor = RuntimeException.class 是否有效？");
            // throw new RuntimeException("抛出 RuntimeException 异常，测试 noRollbackFor = RuntimeException.class 是否有效？");
        }

        return 0;
    }
}