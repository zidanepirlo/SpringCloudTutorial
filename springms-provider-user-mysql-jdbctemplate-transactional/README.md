# SpringCloud（第 042 篇）链接Mysql数据库,通过JdbcTemplate编写数据库访问,而且支持事物处理机制
-

## 一、大致介绍

``` 
1、在数据库操作中，当遇到异常时，我们最关心的是部分回滚还是整体回滚还是不理会，怎么操作怎么来；
2、所以本章节就着重讲解了如何运用Transactional注解来处理事物回滚机制；
3、rollbackFor 属性：抛出的异常是 rollbackFor 异常的子类时都会回滚数据；
4、noRollbackFor 属性：抛出的异常是 noRollbackFor 异常的子类时不会回滚数据；抛出的异常不是 noRollbackFor 异常的子类时会回滚数据；
```


## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-provider-user-mysql-jdbctemplate-transactional</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
	
    <parent>
        <groupId>com.springms.cloud</groupId>
        <artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
	
	<dependencies>
        <!-- 访问数据库模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- web模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- MYSQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-provider-user-mysql-jdbctemplate-transactional\src\main\resources\application.yml）
``` 
server:
  port: 8335
spring:
  application:
    name: springms-provider-user-mysql-jdbctemplate-transactional  #全部小写


#####################################################################################################
# mysql 属性配置
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://ip:3306/hmilyylimh
    username: username
    password: password
  jpa:
    hibernate:
      #ddl-auto: create #ddl-auto:设为create表示每次都重新建表
      ddl-auto: update #ddl-auto:设为update表示每次都不会重新建表
    show-sql: true
#####################################################################################################







#####################################################################################################
# 打印日志
logging:
  level:
    root: INFO
    org.hibernate: INFO
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    org.hibernate.type.descriptor.sql.BasicExtractor: TRACE
    com.springms: DEBUG
#####################################################################################################

```



### 2.3 添加实体用户类User（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/entity/User.java）
``` 
package com.springms.cloud.entity;

public class User {

  private Long id;

  private String username;

  private String name;

  private Integer age;

  private String balance;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return this.age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getBalance() {
    return this.balance;
  }

  public void setBalance(String balance) {
    this.balance = balance;
  }

  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", name='" + name + '\'' +
            ", age=" + age +
            ", balance='" + balance + '\'' +
            '}';
  }
}
```





### 2.4 添加实体账户类Account（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/entity/Account.java）
``` 
package com.springms.cloud.entity;

public class Account {
    private int id ;
    private String name ;
    private double money;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }
}


```



### 2.5 添加用户DAO接口（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/dao/IUserDao.java）
``` 
package com.springms.cloud.dao;

import com.springms.cloud.entity.User;

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
public interface IUserDao {

    User findUserById(Long id);

    List<User> findAllUsers();

    int insertUser(User user);

    int addUser(User user);

    int replaceUser(User user);
}
```

### 2.6 添加账号DAO接口（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/dao/IAccountDao.java）
``` 
package com.springms.cloud.dao;

import com.springms.cloud.entity.Account;

import java.util.List;


public interface IAccountDao {

    int add(Account account);

    int update(Account account);

    int delete(int id);

    Account findAccountById(int id);

    List<Account> findAccountList();
}

```



### 2.7 添加用户DAO接口实现类（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/dao/impl/UserDaoImpl.java）
``` 
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
        int result = jdbcTemplate.update(execSQL,
                new Object[]{user.getUsername(), user.getName(), user.getAge(), user.getBalance()});

        return result;
    }

    @Override
    public int addUser(User user) {
        // 1. 定义一个sql语句
        String execSQL = "INSERT into user (username, name, age, balance) values (?, ?, ?, ?)";

        // 2. 执行查询方法
        int result = jdbcTemplate.update(execSQL,
                new Object[]{user.getUsername(), user.getName(), user.getAge(), user.getBalance()});

        return result;
    }

    @Override
    public int replaceUser(User user) {
        // 1. 定义一个sql语句
        String execSQL = "INSERT into user (username, name, age, balance) values (?, ?, ?, ?)";

        // 2. 执行查询方法
        int result = jdbcTemplate.update(execSQL,
                new Object[]{user.getUsername(), user.getName(), user.getAge(), user.getBalance()});

        return result;
    }
}
```

### 2.8 添加账号DAO接口实现类（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/dao/impl/AccountDaoImpl.java）
``` 
package com.springms.cloud.dao.impl;

import com.springms.cloud.dao.IAccountDao;
import com.springms.cloud.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class AccountDaoImpl implements IAccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int add(Account account) {
        return jdbcTemplate.update("insert into account(name, money) values(?, ?)",
              account.getName(),account.getMoney());

    }

    @Override
    public int update(Account account) {
        return jdbcTemplate.update("UPDATE  account SET NAME=? ,money=? WHERE id=?",
                account.getName(),account.getMoney(),account.getId());
    }

    @Override
    public int delete(int id) {
        return jdbcTemplate.update("DELETE from TABLE account where id=?",id);
    }

    @Override
    public Account findAccountById(int id) {
        List<Account> list = jdbcTemplate.query("select * from account where id = ?", new Object[]{id}, new BeanPropertyRowMapper(Account.class));
        if(list!=null && list.size()>0){
            Account account = list.get(0);
            return account;
        }else{
            return null;
        }
    }

    @Override
    public List<Account> findAccountList() {
        List<Account> list = jdbcTemplate.query("select * from account", new Object[]{}, new BeanPropertyRowMapper(Account.class));
        if(list!=null && list.size()>0){
            return list;
        }else{
            return null;
        }
    }
}
```



### 2.9 添加用户Service接口（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/service/IUserService.java）
``` 
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
     * 由于 RuntimeExcepackage com.springms.cloud.service;
                     
                     import com.springms.cloud.entity.User;
                     
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
                     public interface IUserService {
                     
                         User findUserById(Long id);
                     
                         List<User> findAllUsers();
                     
                         int insertUser(User user);
                     
                         int addUser(User user);
                     
                         int replaceUser(User user) throws Exception;
                     }

```

### 2.10 添加账号Service接口（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/service/IAccountService.java）
``` 
package com.springms.cloud.service;

import com.springms.cloud.entity.Account;

import java.util.List;


public interface IAccountService {


    int add(Account account);

    int update(Account account);

    int delete(int id);

    Account findAccountById(int id);

    List<Account> findAccountList();

}

```



### 2.11 添加电影Service接口（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/service/IMovieService.java）
``` 
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

```



### 2.12 添加用户Service接口实现类（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/service/impl/UserServiceImpl.java）
``` 
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
```

### 2.12 添加账号Service接口实现类（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/service/impl/AccountServiceImpl.java）
``` 
package com.springms.cloud.service.impl;

import com.springms.cloud.dao.IAccountDao;
import com.springms.cloud.entity.Account;
import com.springms.cloud.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    IAccountDao accountDAO;

    @Override
    public int add(Account account) {
        return accountDAO.add(account);
    }

    @Override
    public int update(Account account) {
        return accountDAO.update(account);
    }

    @Override
    public int delete(int id) {
        return accountDAO.delete(id);
    }

    @Override
    public Account findAccountById(int id) {
        return accountDAO.findAccountById(id);
    }

    @Override
    public List<Account> findAccountList() {
        return accountDAO.findAccountList();
    }
}
```


### 2.13 添加电影Service接口实现类（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/service/impl/MovieServiceImpl.java）
``` 
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
```



### 2.14 添加继承Exception的异常类BusinessExtendsException（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/exception/BusinessExtendsException.java）
``` 
package com.springms.cloud.exception;

/**
 *
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
public class BusinessExtendsException extends Exception {

    public BusinessExtendsException() {
    }

    public BusinessExtendsException(String message) {
        super(message);
    }

    public BusinessExtendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessExtendsException(Throwable cause) {
        super(cause);
    }

    public BusinessExtendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

```

### 2.15 添加继承BusinessExtendsException的异常类（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/exception/BusinessSubExtendsException.java）
``` 
package com.springms.cloud.exception;

/**
 *
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
public class BusinessSubExtendsException extends BusinessExtendsException {

    public BusinessSubExtendsException() {
    }

    public BusinessSubExtendsException(String message) {
        super(message);
    }

    public BusinessSubExtendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessSubExtendsException(Throwable cause) {
        super(cause);
    }

    public BusinessSubExtendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

```

### 2.16 添加继承Exception的异常类RollbackExceptionExtendsException（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/exception/RollbackExceptionExtendsException.java）
``` 
package com.springms.cloud.exception;

/**
 *
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
public class RollbackExceptionExtendsException extends Exception {

    public RollbackExceptionExtendsException() {
    }

    public RollbackExceptionExtendsException(String message) {
        super(message);
    }

    public RollbackExceptionExtendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RollbackExceptionExtendsException(Throwable cause) {
        super(cause);
    }

    public RollbackExceptionExtendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

```

### 2.17 添加继承RuntimeException的异常类（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/exception/RollbackExceptionExtendsRuntimeException.java）
``` 
package com.springms.cloud.exception;

/**
 * 回滚异常，测试语句：@Transactional(propagation = Propagation.REQUIRED, isolation= Isolation.DEFAULT, rollbackFor = Exception.class) 是否生效。
 *
 * 结果：含有该属性的 rollbackFor = Exception.class，只要抛出的异常属于 Exception 的子类的话，就可以正常回滚数据；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
public class RollbackExceptionExtendsRuntimeException extends RuntimeException {

    public RollbackExceptionExtendsRuntimeException() {
    }

    public RollbackExceptionExtendsRuntimeException(String message) {
        super(message);
    }

    public RollbackExceptionExtendsRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RollbackExceptionExtendsRuntimeException(Throwable cause) {
        super(cause);
    }

    public RollbackExceptionExtendsRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

```

### 2.18 添加用户Web访问层Controller（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/controller/UserMysqlJdbcTransactionalController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.service.IUserService;
import com.springms.cloud.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户微服务Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@RestController
public class UserMysqlJdbcTransactionalController {

    @Autowired
    private IUserService userService;

    @GetMapping("/simplejdbc/{id}")
    public User findUserById(@PathVariable Long id) {
        return this.userService.findUserById(id);
    }

    @GetMapping("/simplejdbc/list")
    public List<User> findAllUsers() {
        List<User> users  = this.userService.findAllUsers();

        List<User> resultUsers = new ArrayList<>(users.subList(users.size()-6, users.size()));

        return resultUsers;
    }

    /**
     * 添加一个student,使用postMapping接收post请求
     *
     * http://localhost:8315/simple/addUser?username=user11&age=11&balance=11
     *
     * @return
     */
    @PostMapping("/simplejdbc/insertUser")
    public User insertUser(@RequestParam(value = "username", required=false) String username, @RequestParam(value = "age", required=false) Integer age, @RequestParam(value = "balance", required=false) String balance){
        User user=new User();

        user.setUsername(username);
        user.setName(username);
        user.setAge(age);
        user.setBalance(balance);

        int result = userService.insertUser(user);
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
     * http://localhost:8315/simple/addUser?username=user11&age=11&balance=11
     *
     * @return
     */
    @PostMapping("/simplejdbc/addUser")
    public User addUser(@RequestParam(value = "username", required=false) String username, @RequestParam(value = "age", required=false) Integer age, @RequestParam(value = "balance", required=false) String balance){
        User user=new User();

        user.setUsername(username);
        user.setName(username);
        user.setAge(age);
        user.setBalance(balance);

        int result = userService.addUser(user);
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
     * http://localhost:8315/simple/addUser?username=user11&age=11&balance=11
     *
     * @return
     */
    @PostMapping("/simplejdbc/replaceUser")
    public User replaceUser(@RequestParam(value = "username", required=false) String username, @RequestParam(value = "age", required=false) Integer age, @RequestParam(value = "balance", required=false) String balance) throws Exception
    {
        User user=new User();

        user.setUsername(username);
        user.setName(username);
        user.setAge(age);
        user.setBalance(balance);

        int result = 0;
        result = userService.replaceUser(user);

        user.setId((long) 0);
        user.setName(null);
        user.setUsername(null);
        user.setBalance(null);
        return user;
    }
}
```




### 2.19 添加账号Web访问层Controller（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/controller/AccountController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.Account;
import com.springms.cloud.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    IAccountService accountService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public  List<Account> getAccounts(){
       return accountService.findAccountList();
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public  Account getAccountById(@PathVariable("id") int id){
        return accountService.findAccountById(id);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.PUT)
    public  String updateAccount(@PathVariable("id")int id , @RequestParam(value = "name",required = true)String name,
    @RequestParam(value = "money" ,required = true)double money){
        Account account=new Account();
        account.setMoney(money);
        account.setName(name);
        account.setId(id);
        int t=accountService.update(account);
        if(t==1){
            return account.toString();
        }else {
            return "fail";
        }
    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    public  String postAccount( @RequestParam(value = "name")String name,
                                 @RequestParam(value = "money" )double money){
        Account account=new Account();
        account.setMoney(money);
        account.setName(name);
        int t= accountService.add(account);
        if(t==1){
            return account.toString();
        }else {
            return "fail";
        }
    }
}
```


### 2.20 添加电影Web访问层Controller（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/controller/MovieMysqlJdbcTransactionalController.java）
``` 
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
```

### 2.21 添加微服务启动类（springms-provider-user-mysql-jdbctemplate-transactional/src/main/java/com/springms/cloud/MsProviderUserMysqlJdbcTransactionalApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 链接Mysql数据库,通过JdbcTemplate编写数据库访问,而且支持事物处理机制。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
public class MsProviderUserMysqlJdbcTransactionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProviderUserMysqlJdbcTransactionalApplication.class, args);
		System.out.println("【【【【【【 链接MysqlJdbcTransactional数据库微服务 】】】】】】已启动.");
	}
}
```



## 三、测试

``` 
/****************************************************************************************
 一、简单用户链接Mysql数据库微服务（通过JdbcTemplate编写数据访问，而且支持事物处理机制，使用 rollbackFor 属性，数据回滚）：

 1、给 insertUser 方法添加注解：@Transactional(propagation = Propagation.REQUIRED, isolation= Isolation.DEFAULT, rollbackFor = Exception.class)；
 2、insertUser 抛出 RollbackExceptionExtendsRuntimeException 异常；
 3、启动 springms-provider-user-mysql-jdbctemplate-transactional 模块服务，启动1个端口；
 4、在浏览器输入地址 http://localhost:8315/simplejdbc/list 可以看到所有用户信息成功的被打印出来；

 5、使用 REST Client 执行 "/simplejdbc/insertUser" 添加参数执行接口，结果数据没有入库，数据被回滚了；
 ****************************************************************************************/

/****************************************************************************************
 二、简单用户链接Mysql数据库微服务（通过JdbcTemplate编写数据访问，而且支持事物处理机制，使用 noRollbackFor 属性，数据回滚）：

 1、给 replaceUser 方法添加注解：@Transactional(noRollbackFor = BusinessExtendsException.class)；
 2、replaceUser 抛出 RuntimeException 异常；
 3、启动 springms-provider-user-mysql-jdbctemplate-transactional 模块服务，启动1个端口；
 4、在浏览器输入地址 http://localhost:8315/simplejdbc/list 可以看到所有用户信息成功的被打印出来；

 5、使用 REST Client 执行 "/simplejdbc/replaceUser" 添加参数执行接口，结果数据没有入库，数据被回滚了；
 ****************************************************************************************/

/****************************************************************************************
 三、简单用户链接Mysql数据库微服务（通过JdbcTemplate编写数据访问，而且支持事物处理机制，使用 noRollbackFor 属性，数据没有回滚）：

 1、给 replaceUser 方法添加注解：@Transactional(noRollbackFor = BusinessExtendsException.class)；
 2、replaceUser 抛出 BusinessSubExtendsException 异常；
 3、启动 springms-provider-user-mysql-jdbctemplate-transactional 模块服务，启动1个端口；
 4、在浏览器输入地址 http://localhost:8315/simplejdbc/list 可以看到所有用户信息成功的被打印出来；

 5、使用 REST Client 执行 "/simplejdbc/replaceUser" 添加参数执行接口，结果数据已经入库了，数据没有回滚了；

 注意：如果要使得 noRollbackFor 属性生效，注解中 @Transactional 必须得只有 noRollbackFor 属性，然后 noRollbackFor 的异常必须得是自己定义的异常，然后抛 RuntimeException 异常，这样我们才可以测出 noRollbackFor 回滚与不回滚的场景出来；
 ****************************************************************************************/

/****************************************************************************************
 四、简单用户链接Mysql数据库微服务（通过JdbcTemplate编写数据访问，而且支持事物处理机制，同时操作多个DAO文件入库，然后选择注解是否进行回滚数据）：

 这里就不做多的解释了，MovieServiceImpl 就是操作多个DAO文件入库，然后处理是否回滚数据的。

 注意：如果要使得 noRollbackFor 属性生效，注解中 @Transactional 必须得只有 noRollbackFor 属性，然后 noRollbackFor 的异常必须得是自己定义的异常，然后抛 RuntimeException 异常，这样我们才可以测出 noRollbackFor 回滚与不回滚的场景出来；

 rollbackFor 属性：抛出的异常是 rollbackFor 异常的子类时都会回滚数据；
 noRollbackFor 属性：抛出的异常是 noRollbackFor 异常的子类时不会回滚数据；抛出的异常不是 noRollbackFor 异常的子类时会回滚数据；
 ****************************************************************************************/

```




## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























