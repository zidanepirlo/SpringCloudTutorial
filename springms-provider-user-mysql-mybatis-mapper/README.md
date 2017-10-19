# SpringCloud（第 044 篇）链接Mysql数据库简单的集成Mybatis框架采用MapperXml访问数据库
-

## 一、大致介绍

``` 
1、前面章节讲解的是在方法上面添加sql语句操作，虽然说仅仅只是一种简单的操作，在测试期间可以多试试；
2、但是对于复杂的操作，那种简单也仅仅只是简单的自己用用而已，复杂的还是得采用xml配置；
3、因此本章节就试试采用Mybatis框架通过mapperxml进行对数据的操作；
```


## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-provider-user-mysql-mybatis-mapper</artifactId>
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

        <!-- Mybatis依赖 -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.1</version>
        </dependency>

        <!-- Mysql分页PageHelper -->
        <!--<dependency>-->
            <!--<groupId>com.github.pagehelper</groupId>-->
            <!--<artifactId>pagehelper</artifactId>-->
        <!--</dependency>-->
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-provider-user-mysql-mybatis-mapper\src\main\resources\application.yml）
``` 
server:
  port: 8330
spring:
  application:
    name: springms-provider-user-mysql-mybatis-mapper  #全部小写


#####################################################################################################
# mysql 属性配置
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://ip:3306/hmilyylimh
    username: username
    password: password
#  jpa:
#    hibernate:
#      #ddl-auto: create #ddl-auto:设为create表示每次都重新建表
#      ddl-auto: update #ddl-auto:设为update表示每次都不会重新建表
#    show-sql: true
#####################################################################################################

#####################################################################################################
# mybatis mapper xml 配置
mybatis:
  # mybatis.type-aliases-package：指定domain类的基包，即指定其在*Mapper.xml文件中可以使用简名来代替全类名（看后边的UserMapper.xml介绍）
  type-aliases-package:
  mapper-locations: classpath:mybatis/mapper/*.xml
  config-location: classpath:mybatis/mybatis-config.xml
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


### 2.3 添加mybatis配置文件（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/entity/User.java）
``` 
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <settings>
        <setting name="callSettersOnNulls" value="true"/>

        <setting name="cacheEnabled" value="true"/>

        <setting name="lazyLoadingEnabled" value="true"/>

        <setting name="aggressiveLazyLoading" value="true"/>

        <setting name="multipleResultSetsEnabled" value="true"/>

        <setting name="useColumnLabel" value="true"/>

        <setting name="useGeneratedKeys" value="false"/>

        <setting name="autoMappingBehavior" value="PARTIAL"/>

        <setting name="defaultExecutorType" value="SIMPLE"/>

        <setting name="mapUnderscoreToCamelCase" value="true"/>

        <setting name="localCacheScope" value="SESSION"/>

        <setting name="jdbcTypeForNull" value="NULL"/>

    </settings>

    <typeAliases>
        <typeAlias alias="Integer" type="java.lang.Integer" />
        <typeAlias alias="Long" type="java.lang.Long" />
        <typeAlias alias="HashMap" type="java.util.HashMap" />
        <typeAlias alias="LinkedHashMap" type="java.util.LinkedHashMap" />
        <typeAlias alias="ArrayList" type="java.util.ArrayList" />
        <typeAlias alias="LinkedList" type="java.util.LinkedList" />

        <typeAlias alias="User" type="com.springms.cloud.entity.User"/>
    </typeAliases>

</configuration>
``` 



### 2.4 添加用户mapperxml映射文件（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/entity/User.java）
``` 
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.springms.cloud.mapper.IUserMapper">


    <resultMap id="result" type="User">
        <id column="id" property="id" jdbcType="BIGINT"/>
        <result column="username" property="username" jdbcType="VARCHAR"/>
        <result column="name" property="name" jdbcType="VARCHAR"/>
        <result column="age" property="age" jdbcType="TINYINT"/>
        <result column="balance" property="balance" jdbcType="VARCHAR"/>
    </resultMap>


    <!-- 若不需要自动返回主键，将useGeneratedKeys="true" keyProperty="id"去掉即可(当然如果不需要自动返回主键，直接用注解即可) -->
    <insert id="insertUser" parameterType="User" useGeneratedKeys="false" keyProperty="id" >
        <![CDATA[
           INSERT INTO user
           (
               username,
               name,
               age,
               balance
           )
           VALUES
           (
               #{username, jdbcType=VARCHAR},
               #{name, jdbcType=VARCHAR},
               #{age, jdbcType=TINYINT},
               #{balance, jdbcType=VARCHAR}
           )
        ]]>
    </insert>

    <select id="findUserById" resultMap="result" parameterType="Long">
        select * from user
        where id = #{id,jdbcType=BIGINT}
    </select>

    <select id="findAllUsers" resultMap="result">
        select * from user
    </select>
</mapper>
``` 


### 2.5 添加实体用户类User（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/entity/User.java）
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
}

```



### 2.6 添加用户mapper接口（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/mapper/IUserMapper.java）
``` 
package com.springms.cloud.mapper;

import com.springms.cloud.entity.User;
import org.apache.ibatis.annotations.Mapper;

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
}
```



### 2.7 添加用户DAO接口类（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/dao/IUserDao.java）
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
 * @date 2017-10-19
 *
 */
public interface IUserDao {

    User findUserById(Long id);

    List<User> findAllUsers();

    int insertUser(User user);
}
```



### 2.8 添加用户DAO接口类实现类（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/dao/impl/UserDaoImpl.java）
``` 
package com.springms.cloud.dao.impl;

import com.springms.cloud.dao.IUserDao;
import com.springms.cloud.entity.User;
import com.springms.cloud.mapper.IUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 简单用户链接Mysql数据库微服务（通过@Repository注解标注该类为持久化操作对象）。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017-10-19
 *
 */
@Repository
public class UserDaoImpl implements IUserDao {

    @Autowired
    private IUserMapper iUserMapper;

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

```



### 2.9 添加用户Service接口类（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/service/IUserService.java）
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
 * @date 2017-10-19
 *
 */
public interface IUserService {

    User findUserById(Long id);

    List<User> findAllUsers();

    int insertUser(User user);
}

```


### 2.10 添加用户Service接口实现类（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/service/impl/UserServiceImpl.java）
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
 * @date 2017-10-19
 *
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    IUserDao iUserDao;

    @Override
    public User findUserById(Long id) {
        return iUserDao.findUserById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return iUserDao.findAllUsers();
    }

    @Override
    public int insertUser(User user) {
        return iUserDao.insertUser(user);
    }
}
```


### 2.11 添加用户Web访问层Controller（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/controller/ProviderUserMysqlMybatisMapperController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户微服务Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017-10-19
 *
 */
@RestController
public class ProviderUserMysqlMybatisMapperController {

    @Autowired
    private IUserService iUserService;

    @GetMapping("/simple/{id}")
    public User findUserById(@PathVariable Long id) {
        return this.iUserService.findUserById(id);
    }

    @GetMapping("/simple/list")
    public List<User> findUserList() {
        return this.iUserService.findAllUsers();
    }

    /**
     * 添加一个student,使用postMapping接收post请求
     *
     * http://localhost:8330/simple/addUser?username=user11&age=11&balance=11
     *
     * @return
     */
    @PostMapping("/simple/addUser")
    public User addUser(@RequestParam(value = "username", required=false) String username, @RequestParam(value = "age", required=false) Integer age, @RequestParam(value = "balance", required=false) String balance){
        User user=new User();

        user.setUsername(username);
        user.setName(username);
        user.setAge(age);
        user.setBalance(balance);

        int result = iUserService.insertUser(user);
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


### 2.12 添加微服务启动类（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/MsProviderUserMysqlMybatisMapperApplication.java）
``` 
package com.springms.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 链接Mysql数据库简单的集成Mybatis框架采用MapperXml访问数据库。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017-10-19
 *
 */
@SpringBootApplication
@MapperScan("com.springms.cloud.mapper.**")
public class MsProviderUserMysqlMybatisMapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProviderUserMysqlMybatisMapperApplication.class, args);
		System.out.println("【【【【【【 链接MysqlMybatisMapperXml数据库微服务 】】】】】】已启动.");
	}
}
```



## 三、测试

``` 
/****************************************************************************************
 注意：Mybatis 需要加上 entity 等注解才可以使用，不然启动都会报错；
 @MapperScan("com.springms.cloud.mapper.**") 或者在每个 Mapper 接口文件上添加 @Mapper 也可以；

 一、简单用户链接Mysql数据库微服务（通过 mybatis 链接 mysql 并用 MapperXml 编写数据访问）：

 1、启动 springms-provider-user-mysql-mybatis-mapper 模块服务，启动1个端口；
 2、在浏览器输入地址 http://localhost:8330/simple/10 可以看到用户ID=10的信息成功的被打印出来；

 3、使用 IDEA 自带工具 Test Restful WebService 发送 HTTP POST 请求,并添加 username、age、balance三个参数，然后执行请求，并去 mysql 数据库查看数据是否存在，正常情况下 mysql 数据库刚刚插入的数据成功了:
 4、使用 REST Client 执行 "/simple/list" 接口，也正常将 mysql 数据库中所有的用户信息打印出来了；
 ****************************************************************************************/

```




## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























