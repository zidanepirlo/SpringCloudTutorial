# SpringCloud（第 040 篇）链接Mysql数据库,通过JdbcTemplate编写数据库访问
-

## 一、大致介绍

``` 
1、前面章节提到了JPA操作访问数据库，本章节我们讲解一下如何用JdbcTemplate操作访问数据库；
2、使用JdbcTemplate是一个相对更底层的操作，可以直接编写sql语句操作数据，更具灵活性；
3、另外配置文件中的mysql数据库配置，那么就得大家自己用自己的了哈；
```


## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-provider-user-mysql-jdbctemplate</artifactId>
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


### 2.2 添加应用配置文件（springms-provider-user-mysql-jdbctemplate\src\main\resources\application.yml）
``` 
server:
  port: 8315
spring:
  application:
    name: springms-provider-user-mysql-jdbctemplate  #全部小写


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



### 2.3 添加实体用户类User（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/entity/User.java）
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





### 2.4 添加实体账户类Account（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/entity/Account.java）
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



### 2.5 添加用户DAO接口（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/dao/IUserDao.java）
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
}
```

### 2.6 添加账号DAO接口（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/dao/IAccountDao.java）
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



### 2.7 添加用户DAO接口实现类（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/dao/impl/UserDaoImpl.java）
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
```

### 2.8 添加账号DAO接口实现类（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/dao/impl/AccountDaoImpl.java）
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



### 2.9 添加用户Service接口（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/service/IUserService.java）
``` 
package com.springms.cloud.service;

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
}

```

### 2.10 添加账号Service接口（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/service/IAccountService.java）
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



### 2.11 添加用户Service接口实现类（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/service/impl/UserServiceImpl.java）
``` 
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
```

### 2.12 添加账号Service接口实现类（springms-provider-user-mysql-jdbctemplate/src/main/java/com/springms/cloud/service/impl/AccountServiceImpl.java）
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


### 2.13 添加用户Web访问层Controller（springms-provider-user-mysql-crudrepository/src/main/java/com/springms/cloud/controller/ProviderUserMysqlCrudRepoController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.springms.cloud.entity.User;
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
public class ProviderUserMysqlJdbcController {

    @Autowired
    private IUserService userService;

    @GetMapping("/simplejdbc/{id}")
    public User findUserById(@PathVariable Long id) {
        return this.userService.findUserById(id);
    }

    @GetMapping("/simplejdbc/list")
    public List<User> findAllUsers() {
        return this.userService.findAllUsers();
    }

    /**
     * 添加一个student,使用postMapping接收post请求
     *
     * http://localhost:8315/simple/addUser?username=user11&age=11&balance=11
     *
     * @return
     */
    @PostMapping("/simplejdbc/addUser")
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
}
```




### 2.14 添加账号Web访问层Controller（springms-provider-user-mysql-crudrepository/src/main/java/com/springms/cloud/controller/ProviderUserMysqlCrudRepoController.java）
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

### 2.15 添加微服务启动类（springms-provider-user-mysql-crudrepository/src/main/java/com/springms/cloud/MsProviderUserMysqlCrudRepoApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 链接Mysql数据库,通过JdbcTemplate编写数据库访问。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
public class MsProviderUserMysqlJdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProviderUserMysqlJdbcApplication.class, args);
		System.out.println("【【【【【【 链接MysqlJdbc数据库微服务 】】】】】】已启动.");
	}
}

```



## 三、测试

``` 
/****************************************************************************************
 一、简单用户链接Mysql数据库微服务（通过JdbcTemplate编写数据访问）：

 1、启动 springms-provider-user-mysql-jdbctemplate 模块服务，启动1个端口；
 2、在浏览器输入地址 http://localhost:8315/simplejdbc/6 可以看到用户ID=10的信息成功的被打印出来；

 3、使用 IDEA 自带工具 Test Restful WebService 发送 HTTP POST 请求,并添加 username、age、balance三个参数，然后执行请求，并去 mysql 数据库查看数据是否存在，正常情况下 mysql 数据库刚刚插入的数据成功了:
 4、使用 REST Client 执行 "/simplejdbc/list" 接口，也正常将 mysql 数据库中所有的用户信息打印出来了；

 5、然后再操作 http://localhost:8315/account/ 该链接的接口操作，也是可以正常操作访问的；
 ****************************************************************************************/

```




## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























