# SpringCloud（第 002 篇）简单电影微服务类（消费方，而提供方为用户微服务）
-

## 一、大致介绍

``` 
微服务与微服务之间通过 Http 协议进行通信；
用户微服务作为提供方，电影微服务作为消费方，电影微服务消费用户微服务 ；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-simple-consumer-movie</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
	
    <parent>
		<groupId>com.springms.cloud</groupId>
		<artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
	
	<dependencies>
        <!-- web模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

</project>

```


### 2.2 添加应用配置文件（springms-simple-consumer-movie\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-simple-consumer-movie  #全部小写
server:
  port: 8005
user: 
  userServicePath: http://localhost:8000/simple/

```

### 2.3 添加实体用户类User（springms-simple-consumer-movie\src\main\java\com\springms\cloud\entity\User.java）
``` 
package com.springms.cloud.entity;

import java.math.BigDecimal;

public class User {
  private Long id;

  private String username;

  private String name;

  private Short age;

  private BigDecimal balance;

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

  public Short getAge() {
    return this.age;
  }

  public void setAge(Short age) {
    this.age = age;
  }

  public BigDecimal getBalance() {
    return this.balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

}


```

### 2.4 添加电影Web访问层Controller（springms-simple-consumer-movie\src\main\java\com\springms\cloud\controller\MsSimpleConsumerMovieController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 电影微服务Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@RestController
public class MsSimpleConsumerMovieController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.userServicePath}")
    private String userServicePath;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        return this.restTemplate.getForObject(this.userServicePath + id, User.class);
    }
}

```


### 2.5 添加简单电影微服务启动类（springms-simple-consumer-movie\src\main\java\com\springms\cloud\MsSimpleConsumerMovieApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 简单电影微服务类（消费方，而提供方为用户微服务）。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@SpringBootApplication
public class MsSimpleConsumerMovieApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MsSimpleConsumerMovieApplication.class, args);
      System.out.println("【【【【【【 简单电影微服务 】】】】】】已启动.");
    }
}


```



## 三、测试

``` 
/****************************************************************************************
 一、简单电影微服务类（消费方，而提供方为用户微服务）：

 1、启动 springms-simple-provider-user 模块服务，启动1个端口；
 2、启动 springms-simple-consumer-movie 模块服务，启动1个端口；
 3、在浏览器输入地址 http://localhost:8005/movie/1 可以看到信息成功的被打印出来；
 ****************************************************************************************/
```




## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























