# SpringCloud（第 005 篇）电影微服务，也注册到 EurekaServer 中，通过 Http 协议访问已注册到生态圈中的用户微服务
-

## 一、大致介绍

``` 
1、在 eureka 服务治理框架中，微服务与微服务之间通过 Http 协议进行通信；
2、用户微服务作为消费方、电影微服务作为提供方都注册到 EurekaServer 中；
3、在电影微服务Web层通过 application.yml 的硬编码配置方式实现服务之间的通信；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-consumer-movie</artifactId>
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

        <!-- 监控和管理生产环境的模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- 客户端发现模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
    </dependencies>

</project>


```


### 2.2 添加应用配置文件（springms-consumer-movie\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-consumer-movie
server:
  port: 7901
user: 
  userServicePath: http://localhost:7900/simple/
eureka:
  client:
#    healthcheck:
#      enabled: true
    serviceUrl:
      defaultZone: http://admin:admin@localhost:8761/eureka
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}


```

### 2.3 添加实体用户类User（springms-consumer-movie\src\main\java\com\springms\cloud\entity\User.java）
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

### 2.4 添加电影Web访问层Controller（springms-consumer-movie\src\main\java\com\springms\cloud\controller\MovieController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MovieController {

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


### 2.5 添加电影微服务启动类（springms-consumer-movie\src\main\java\com\springms\cloud\MsConsumerMovieApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 电影微服务，也注册到 EurekaServer 中，通过 Http 协议访问已注册到生态圈中的用户微服务。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class MsConsumerMovieApplication {

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieApplication.class, args);
		System.out.println("【【【【【【 电影微服务 】】】】】】已启动.");
	}
}

```



## 三、测试

``` 
/****************************************************************************************
 一、电影微服务，也注册到 EurekaServer 中，通过 Http 协议访问已注册到生态圈中的用户微服务：

 1、启动 springms-discovery-eureka 模块服务，启动1个端口；
 2、启动 springms-provider-user 模块服务，启动1个端口；
 3、启动 springms-consumer-movie 模块服务，启动1个端口；
 4、在浏览器输入地址 http://localhost:7900/simple/1 可以看到信息成功的被打印出来，说明用户微服务正常；
 5、在浏览器输入地址 http://localhost:8761 并输入用户名密码 admin/admin 进入Eureka微服务显示在网页中，验证用户微服务、电影微服务确实注册到了 eureka 服务中；

 6、在浏览器输入地址http://localhost:7901/movie/1 可以看到用户信息成功的被打印出来；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

欢迎关注，您的肯定是对我最大的支持!!!
```






























