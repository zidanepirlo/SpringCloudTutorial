# SpringCloud（第 045 篇）链接Mysql数据库简单的集成Mybatis、ehcache框架采用MapperXml访问数据库
-

## 一、大致介绍

``` 
1、数据库频繁的操作也会影响性能，所以本章节准备给访问数据库前面添加一层缓存操作；
2、虽然说缓存框架存在很多且各有各的优势，本章节仅仅只是为了测试缓存的操作实现，所以就采用了一个简单的缓存框架ehcache；
```


## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-provider-user-mysql-mybatis-mapper-ehcache</artifactId>
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

        <!-- 开启 cache 缓存 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>

        <!-- ehcache 缓存模块 -->
        <dependency>
            <groupId>net.sf.ehcache</groupId>
            <artifactId>ehcache</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-provider-user-mysql-mybatis-mapper-ehcache\src\main\resources\application.yml）
``` 
server:
  port: 8385
spring:
  application:
    name: springms-provider-user-mysql-mybatis-mapper-ehcache  #全部小写


#####################################################################################################
# mysql 属性配置
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://120.77.176.162:3306/hmilyylimh
    username: root
    password: mysqladmin
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


### 2.3 添加mybatis配置文件（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/entity/User.java）
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



### 2.4 添加用户mapperxml映射文件（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/entity/User.java）
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

    <select id="deleteUser" parameterType="Long">
        DELETE from user where id = #{id}
    </select>

    <update id="updateUser" parameterType="User" >
        update user set userName=#{username},name=#{name},age=#{age},balance=#{balance} where id=#{id}
    </update>
</mapper>
``` 



### 2.5 添加缓存配置文件（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/resources/ehcache.xml）
``` 
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">
    <defaultCache
            eternal="false"
            maxElementsInMemory="900"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="0"
            timeToLiveSeconds="500"
            memoryStoreEvictionPolicy="LRU" />

    <!-- 这里的 users 缓存空间是为了下面的 demo 做准备 -->
    <cache
            name="cache-b"
            eternal="false"
            maxElementsInMemory="200"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="0"
            timeToLiveSeconds="400"
            memoryStoreEvictionPolicy="LRU" />


</ehcache>


<!--Ehcache 相关资料：-->

<!--diskStore：为缓存路径，ehcache分为内存和磁盘两级，此属性定义磁盘的缓存位置。-->
<!--defaultCache：默认缓存策略，当ehcache找不到定义的缓存时，则使用这个缓存策略。只能定义一个。-->
<!--name:缓存名称。-->
<!--maxElementsInMemory:缓存最大数目-->
<!--maxElementsOnDisk：硬盘最大缓存个数。-->
<!--eternal:对象是否永久有效，一但设置了，timeout将不起作用。-->
<!--overflowToDisk:是否保存到磁盘，当系统当机时-->
<!--timeToIdleSeconds:设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。-->
<!--timeToLiveSeconds:设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。-->
<!--diskPersistent：是否缓存虚拟机重启期数据 Whether the disk store persists between restarts of the Virtual Machine. The default value is false.diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。-->
<!--diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。-->
<!--memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）。-->
<!--clearOnFlush：内存数量最大时是否清除。-->
<!--memoryStoreEvictionPolicy:可选策略有：LRU（最近最少使用，默认策略）、FIFO（先进先出）、LFU（最少访问次数）。-->


<!--FIFO，first in first out，先进先出。-->
<!--LFU， Less Frequently Used，一直以来最少被使用的。如上面所讲，缓存的元素有一个hit属性，hit值最小的将会被清出缓存。-->
<!--LRU，Least Recently Used，最近最少使用的，缓存的元素有一个时间戳，当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存。-->


<!--一般情况下，我们在Sercive层进行对缓存的操作。先介绍 Ehcache 在 Spring 中的注解：在支持 Spring Cache 的环境下，-->
<!--* @Cacheable : Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。-->
<!--* @CacheEvict : 清除缓存。-->
<!--* @CachePut : @CachePut也可以声明一个方法支持缓存功能。使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。-->
<!--* 这三个方法中都有两个主要的属性：value 指的是 ehcache.xml 中的缓存策略空间；key 指的是缓存的标识，同时可以用 # 来引用参数。-->
``` 


### 2.6 添加实体用户类User（sspringms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/entity/User.java）
``` 
package com.springms.cloud.entity;


public class User {

  private Long id;

  private String username;

  private String name;

  private Integer age;

  private String balance;

  /** 来自于哪里，默认来自于数据库 */
  private String from = "";

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

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", name='" + name + '\'' +
            ", age=" + age +
            ", balance='" + balance + '\'' +
            ", from='" + from + '\'' +
            '}';
  }
}

```



### 2.7 添加用户mapper接口（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/mapper/IUserMapper.java）
``` 
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
```



### 2.8 添加用户DAO接口类（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/dao/IUserDao.java）
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

    int updateUser(User user);

    int deleteUser(Long id);
}
```



### 2.9 添加用户DAO接口类实现类（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/dao/impl/UserDaoImpl.java）
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

    @Override
    public int updateUser(User user) {
        return iUserMapper.updateUser(user);
    }

    @Override
    public int deleteUser(Long id) {
        return iUserMapper.deleteUser(id);
    }
}
```



### 2.10 添加用户Service接口类（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/service/IUserService.java）
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

    int updateUser(User user);

    int deleteUser(Long id);
}
```


### 2.11 添加用户Service接口实现类（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/service/impl/UserServiceImpl.java）
``` 
package com.springms.cloud.service.impl;

import com.springms.cloud.dao.IUserDao;
import com.springms.cloud.entity.User;
import com.springms.cloud.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简单用户链接Mysql数据库微服务（通过@Service注解标注该类为持久化操作对象）。<br/>
 *
 * <li>注意：CACHE_KEY、CACHE_NAME_B 的单引号不能少，否则会报错，被识别是一个对象。</li>
 *
 * <li>value 指的是 ehcache.xml 中的缓存策略空间；key 指的是缓存的标识</li>
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

    private static final String CACHE_KEY = "'user'";
    private static final String CACHE_NAME_B = "cache-b";

    //* @Cacheable : Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。
    //* @CacheEvict : 清除缓存。
    //* @CachePut : @CachePut也可以声明一个方法支持缓存功能。使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。

    @Autowired
    IUserDao iUserDao;

    /**
     * 查找用户数据
     *
     * @param id
     * @return
     */
    @Cacheable(value=CACHE_NAME_B, key="'user_'+#id")
    @Override
    public User findUserById(Long id) {
        return iUserDao.findUserById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return iUserDao.findAllUsers();
    }

    /**
     * 保存用户数据
     *
     * @param user
     * @return
     */
    @CacheEvict(value=CACHE_NAME_B, key=CACHE_KEY)
    @Override
    public int insertUser(User user) {
        return iUserDao.insertUser(user);
    }

    /**
     * 更新用户数据
     *
     * @param user
     * @return
     */
    @CachePut(value = CACHE_NAME_B, key = "'user_'+#user.id")
    @Override
    public int updateUser(User user) {
        return iUserDao.updateUser(user);
    }

    /**
     * 删除用户数据
     *
     * @param id
     * @return
     */
    @CacheEvict(value = CACHE_NAME_B, key = "'user_' + #id") //这是清除缓存
    @Override
    public int deleteUser(Long id) {
        return iUserDao.deleteUser(id);
    }
}
```



### 2.12 添加缓存配置Config（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/config/CacheConfiguration.java）
``` 
package com.springms.cloud.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;  
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.core.io.ClassPathResource;  

/**
 * 缓存配置。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017-10-19
 *
 */
@Configuration  
@EnableCaching//标注启动缓存.  
public class CacheConfiguration {  
     
    /** 
     * ehcache 主要的管理器
     *
     * @param bean 
     * @return 
     */  
    @Bean  
    public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean){
       System.out.println("CacheConfiguration.ehCacheCacheManager()");  
       return new EhCacheCacheManager(bean.getObject());  
    }  
     
    /*
     * 据shared与否的设置,
     * Spring分别通过CacheManager.create()
     * 或new CacheManager()方式来创建一个ehcache基地.
     *
     * 也说是说通过这个来设置cache的基地是这里的Spring独用,还是跟别的(如hibernate的Ehcache共享)
     *
     */
    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean(){
    System.out.println("CacheConfiguration.ehCacheManagerFactoryBean()");
    EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean ();
    cacheManagerFactoryBean.setConfigLocation (new ClassPathResource("ehcache.xml"));
    cacheManagerFactoryBean.setShared(true);
    return cacheManagerFactoryBean;
    }
} 
``` 


### 2.13 添加用户Web访问层Controller（springms-provider-user-mysql-mybatis-mapper/src/main/java/com/springms/cloud/controller/ProviderUserMysqlMybatisMapperController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.service.IUserService;
import org.hibernate.cache.CacheException;
import org.slf4j.LoggerFactory;
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
public class ProviderUserMysqlMybatisMapperEhCacheController {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ProviderUserMysqlMybatisMapperEhCacheController.class);

    @Autowired
    private IUserService iUserService;

    @GetMapping("/user/{id}")
    public User findUserById(@PathVariable Long id) {
        return this.iUserService.findUserById(id);
    }

    @GetMapping("/user/list")
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
    @PostMapping("/user/addUser")
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

    @GetMapping("/user/ehcache")
    public String ehcache() {
        Logger.info("===========  进行Encache缓存测试");

        List<User> allUsers = iUserService.findAllUsers();
        User lastUser = allUsers.get(allUsers.size() - 1);
        String lastUserUsername = lastUser.getUsername();
        String indexString = lastUserUsername.substring(4);

        Logger.info("===========  ====生成第一个用户====");
        User user1 = new User();
        //生成第一个用户的唯一标识符 UUID
        user1.setName("user" + (Integer.parseInt(indexString) + 1));
        user1.setUsername(user1.getName());
        user1.setAge(1000);
        user1.setBalance("1000");
        if (iUserService.insertUser(user1) == 0){
            throw new CacheException("用户对象插入数据库失败");
        }

        allUsers = iUserService.findAllUsers();
        lastUser = allUsers.get(allUsers.size() - 1);
        Long lastUserId = lastUser.getId();

        //第一次查询
        Logger.info("===========  第一次查询");
        Logger.info("===========  第一次查询结果: {}", iUserService.findUserById(lastUserId));
        //通过缓存查询
        Logger.info("===========  通过缓存第 1 次查询");
        Logger.info("===========  通过缓存第 1 次查询结果: {}", iUserService.findUserById(lastUserId));
        Logger.info("===========  通过缓存第 2 次查询");
        Logger.info("===========  通过缓存第 2 次查询结果: {}", iUserService.findUserById(lastUserId));
        Logger.info("===========  通过缓存第 3 次查询");
        Logger.info("===========  通过缓存第 3 次查询结果: {}", iUserService.findUserById(lastUserId));

        Logger.info("===========  ====准备修改数据====");
        User user2 = new User();
        user2.setName(lastUser.getName());
        user2.setUsername(lastUser.getUsername());
        user2.setAge(lastUser.getAge() + 1000);
        user2.setBalance(String.valueOf(user2.getAge()));
        user2.setId(lastUserId);
        try {
            int result = iUserService.updateUser(user2);
            Logger.info("===========  ==== 修改数据 == {} ==", (result > 0? "成功":"失败"));
        } catch (CacheException e){
            e.printStackTrace();
        }

        Logger.info("===========  ====修改后再次查询数据");
        Object resultObj = iUserService.findUserById(lastUser.getId());
        Logger.info("===========  ====修改后再次查询数据结果: {}", resultObj);
        return "success";
    }
}

```


### 2.14 添加微服务启动类（springms-provider-user-mysql-mybatis-mapper-ehcache/src/main/java/com/springms/cloud/MsProviderUserMysqlMybatisMapperEhCacheApplication.java）
``` 
package com.springms.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 链接Mysql数据库简单的集成Mybatis、ehcache框架采用MapperXml访问数据库。
 *
 * 简单用户链接Mysql数据库微服务（通过 mybatis 链接 mysql 并用 MapperXml 编写数据访问，并且通过 EhCache 缓存来访问）。
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
@EnableCaching
public class MsProviderUserMysqlMybatisMapperEhCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProviderUserMysqlMybatisMapperEhCacheApplication.class, args);
		System.out.println("【【【【【【 链接MysqlMybatisMapperEhCache数据库微服务 】】】】】】已启动.");
	}
}
```



## 三、测试

``` 
/****************************************************************************************
 注意：Mybatis 需要加上 entity 等注解才可以使用，不然启动都会报错；
 @MapperScan("com.springms.cloud.mapper.**") 或者在每个 Mapper 接口文件上添加 @Mapper 也可以；

 一、简单用户链接Mysql数据库微服务（通过 mybatis 链接 mysql 并用 MapperXml 编写数据访问，并且通过 EhCache 缓存来访问）：

 1、启动 springms-provider-user-mysql-mybatis-mapper-ehcache 模块服务，启动1个端口；
 2、在浏览器输入地址 http://localhost:8385/user/10 可以看到用户ID=10的信息成功的被打印出来；

 3、使用 IDEA 自带工具 Test Restful WebService 发送 HTTP POST 请求,并添加 username、age、balance三个参数，然后执行请求，并去 mysql 数据库查看数据是否存在，正常情况下 mysql 数据库刚刚插入的数据成功了:
 4、使用 REST Client 执行 "/user/ehcache" 接口，也正常将 mysql 数据库中所有的用户信息打印出来了，并且打印的信息如下：

 5、在浏览器输入地址 http://localhost:8385/user/ehcache 可以看到如下信息被打印出来；

 2017-10-19 17:48:54.561  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  进行Encache缓存测试
 2017-10-19 17:48:54.610 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : ==>  Preparing: select * from user
 2017-10-19 17:48:54.629 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : ==> Parameters:
 2017-10-19 17:48:54.657 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : <==      Total: 65
 2017-10-19 17:48:54.658  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ====生成第一个用户====
 2017-10-19 17:48:54.661 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.insertUser  : ==>  Preparing: INSERT INTO user ( username, name, age, balance ) VALUES ( ?, ?, ?, ? )
 2017-10-19 17:48:54.662 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.insertUser  : ==> Parameters: user66(String), user66(String), 1000(Integer), 1000(String)
 2017-10-19 17:48:54.678 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.insertUser  : <==    Updates: 1
 2017-10-19 17:48:54.691 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : ==>  Preparing: select * from user
 2017-10-19 17:48:54.692 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : ==> Parameters:
 2017-10-19 17:48:54.707 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : <==      Total: 66
 2017-10-19 17:48:54.708  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  第一次查询
 2017-10-19 17:48:54.714 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : ==>  Preparing: select * from user where id = ?
 2017-10-19 17:48:54.714 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : ==> Parameters: 147(Long)
 2017-10-19 17:48:54.721 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : <==      Total: 1
 2017-10-19 17:48:54.722  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  第一次查询结果: User{id=147, username='user66', name='user66', age=1000, balance='1000', from=''}
 2017-10-19 17:48:54.722  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 1 次查询
 2017-10-19 17:48:54.723  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 1 次查询结果: User{id=147, username='user66', name='user66', age=1000, balance='1000', from=''}
 2017-10-19 17:48:54.723  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 2 次查询
 2017-10-19 17:48:54.724  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 2 次查询结果: User{id=147, username='user66', name='user66', age=1000, balance='1000', from=''}
 2017-10-19 17:48:54.724  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 3 次查询
 2017-10-19 17:48:54.724  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 3 次查询结果: User{id=147, username='user66', name='user66', age=1000, balance='1000', from=''}
 2017-10-19 17:48:54.724  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ====准备修改数据====
 2017-10-19 17:48:54.725 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.updateUser  : ==>  Preparing: update user set userName=?,name=?,age=?,balance=? where id=?
 2017-10-19 17:48:54.725 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.updateUser  : ==> Parameters: user66(String), user66(String), 2000(Integer), 2000(String), 147(Long)
 2017-10-19 17:48:54.738 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.updateUser  : <==    Updates: 1
 2017-10-19 17:48:54.747  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ==== 修改数据 == 成功 ==
 2017-10-19 17:48:54.747  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ====修改后再次查询数据
 2017-10-19 17:48:54.747 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : ==>  Preparing: select * from user where id = ?
 2017-10-19 17:48:54.747 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : ==> Parameters: 147(Long)
 2017-10-19 17:48:54.747 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : <==      Total: 1
 2017-10-19 17:48:54.747  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ====修改后再次查询数据结果: User{id=147, username='user66', name='user66', age=2000, balance='2000', from=''}

 总结：可以看出查询过一次后，就不会再查询数据库了，所以第二次、第三次都会去查找缓存的数据；
 ****************************************************************************************/



/****************************************************************************************
 Ehcache 相关资料：

 diskStore：为缓存路径，ehcache分为内存和磁盘两级，此属性定义磁盘的缓存位置。
 defaultCache：默认缓存策略，当ehcache找不到定义的缓存时，则使用这个缓存策略。只能定义一个。
 name:缓存名称。
 maxElementsInMemory:缓存最大数目
 maxElementsOnDisk：硬盘最大缓存个数。
 eternal:对象是否永久有效，一但设置了，timeout将不起作用。
 overflowToDisk:是否保存到磁盘，当系统当机时
 timeToIdleSeconds:设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
 timeToLiveSeconds:设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。
 diskPersistent：是否缓存虚拟机重启期数据 Whether the disk store persists between restarts of the Virtual Machine. The default value is false.diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。
 diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。
 memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）。
 clearOnFlush：内存数量最大时是否清除。
 memoryStoreEvictionPolicy:可选策略有：LRU（最近最少使用，默认策略）、FIFO（先进先出）、LFU（最少访问次数）。


 FIFO，first in first out，先进先出。
 LFU， Less Frequently Used，一直以来最少被使用的。如上面所讲，缓存的元素有一个hit属性，hit值最小的将会被清出缓存。
 LRU，Least Recently Used，最近最少使用的，缓存的元素有一个时间戳，当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存。


 一般情况下，我们在Sercive层进行对缓存的操作。先介绍 Ehcache 在 Spring 中的注解：在支持 Spring Cache 的环境下，
 * @Cacheable : Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。
 * @CacheEvict : 清除缓存。
 * @CachePut : @CachePut也可以声明一个方法支持缓存功能。使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。
 * 这三个方法中都有两个主要的属性：value 指的是 ehcache.xml 中的缓存策略空间；key 指的是缓存的标识，同时可以用 # 来引用参数。
 ****************************************************************************************/

```




## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























