package com.springms.cloud.service.impl;

import com.springms.cloud.dao.IUserDao;
import com.springms.cloud.entity.User;
import com.springms.cloud.exception.BusinessExtendsException;
import com.springms.cloud.exception.RollbackExceptionExtendsRuntimeException;
import com.springms.cloud.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
public class UserServiceImpl implements IUserService{

    @Autowired
    private IUserDao userDao;

    @Transactional(readOnly = true)
    @Override
    public User findUserById(Long id){
        return userDao.findUserById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllUsers(){
        return userDao.findAllUsers();
    }

    /**
     * 由于 RuntimeException、RollbackExceptionExtendsRuntimeException 是 Exception 的子类，所以属于 Exception 的子类异常抛出来，都会回滚数据。
     *
     * @param user
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation= Isolation.DEFAULT, rollbackFor = Exception.class)
    @Override
    public int insertUser(User user) {
        int result = userDao.insertUser(user);

        if(result > 0){
            // throw new RuntimeException("抛出 RuntimeException 异常，测试 rollbackFor = Exception.class 是否有效？");
            throw new RollbackExceptionExtendsRuntimeException("抛出 RollbackExceptionExtendsRuntimeException 异常，测试 rollbackFor = Exception.class 是否有效？");
        }

        return result;
    }

    @Override
    public int addUser(User user) {
        return userDao.addUser(user);
    }

    /**
     *
     * 由于 RollbackExceptionExtendsException 是 Exception 的子类，不是 BusinessExtendsException 的子类，所以抛出该异常，会回滚数据；<br/>
     *
     * <li>注意：如果要使得 noRollbackFor 属性生效，注解中 @Transactional 必须得只有 noRollbackFor 属性，然后 noRollbackFor 的异常必须得是自己定义的异常，然后抛 RuntimeException 异常，这样我们才可以测出 noRollbackFor 回滚与不回滚的场景出来；</li>
     *
     * @param user
     * @return
     */
    @Transactional(noRollbackFor = BusinessExtendsException.class)
    @Override
    public int replaceUser(User user) throws Exception {
        int result = userDao.insertUser(user);

        if(result > 0){
            // throw new NullPointerException("抛出 NullPointerException 异常，测试 noRollbackFor = RuntimeException.class 是否有效？");
            // throw new RollbackExceptionExtendsException("抛出 RollbackExceptionExtendsException 异常，测试 noRollbackFor = RuntimeException.class 是否有效？");
            // throw new BusinessSubExtendsException("抛出 BusinessSubExtendsException 异常，测试 noRollbackFor = RuntimeException.class 是否有效？");
            throw new RuntimeException("抛出 RuntimeException 异常，测试 noRollbackFor = RuntimeException.class 是否有效？");
        }

        return result;
    }
}