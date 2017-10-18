# SpringCloud（第 012 篇）电影微服务接入 Feign 进行客户端负载均衡，通过 FeignClient 调用远程 Http 微服务
-

## 一、大致介绍

``` 
1、本章节主要介绍在 SpringCloud 生态圈中，使用一个类似于 Java HTTP 客户端的工具 Feign 来访问远程 HTTP 服务器；
2、虽然说我们可以采用 RestTemplate、URLConnection、Netty、HttpClient都可以访问远端 HTTP 服务器，但是使用 Feign 来说，Feign 可以做到使用 HTTP 请求远程服务时就像调用本地的方法一样，让开发者完全感知不到这是在调用远端服务，感觉无非就是调用一个 API 方法一样；
3、当我们使用 Feign 的时候，SpringCloud 整合了 Ribbon 和 Eureka 去提供负载均衡；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-consumer-movie-feign</artifactId>
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

        <!-- 客户端发现模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>

        <!-- 监控和管理生产环境的模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Java HTTP 客户端开发的工具的模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-feign</artifactId>
        </dependency>
    </dependencies>

</project>

```


### 2.2 添加应用配置文件（springms-consumer-movie-feign/src/main/resources/application.yml）
``` 
spring:
  application:
    name: springms-consumer-movie-feign
server:
  port: 7910
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

### 2.3 添加实体用户类User（springms-consumer-movie-feign/src/main/java/com/springms/cloud/entity/User.java）
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


### 2.4 添加用户 Feign 客户端（springms-consumer-movie-feign/src/main/java/com/springms/cloud/feign/UserFeignClient.java）
``` 
package com.springms.cloud.feign;

import com.springms.cloud.entity.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Http请求的客户端。
 *
 * 注解FeignClient的传参：表示的是注册到 Eureka 服务上的模块名称，也就是需要访问的微服务名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/19
 *
 */
@FeignClient("springms-provider-user")
public interface UserFeignClient {

    /**
     * 这里有两个坑需要注意：
     *
     * 1、这里需要设置请求的方式为 RequestMapping 注解，用 GetMapping 注解是运行不成功的，即 GetMapping 不支持。
     *
     * 2、注解 PathVariable 里面需要填充变量的名字，不然也是运行不成功的。
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/simple/{id}", method = RequestMethod.GET)
    public User findById(@PathVariable("id") Long id);

    /**
     * 这里也有一个坑需要注意：
     *
     * 如果入参是一个对象的话，那么这个方法在 feign 里面默认为 POST 方法，就算你写成 GET 方式也无济于事。
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User postUser(@RequestBody User user);
}

```


### 2.5 添加电影Web访问层Controller（springms-consumer-movie-feign/src/main/java/com/springms/cloud/controller/MovieFeignController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * 电影 Feign 控制器，通过 Feign 访问远端服务。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/19
 *
 */
@RestController
public class MovieFeignController {

    @Autowired
    private UserFeignClient userFeignClient;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        return userFeignClient.findById(id);
    }

    @GetMapping("/movie/user")
    public User postUser(User user){
        Random random = new Random();
        User tmpUser = new User();
        long id = (long) random.nextInt(100);
        tmpUser.setId(id);
        tmpUser.setName("TempUser" + id);
        tmpUser.setAge((short) id);

        return userFeignClient.postUser(tmpUser);
    }
}

```


### 2.6 添加电影微服务启动类（springms-consumer-movie-feign/src/main/java/com/springms/cloud/MsConsumerMovieFeignApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 电影微服务接入 Feign 进行客户端负载均衡，通过 FeignClient 调用远程 Http 微服务。
 *
 * Feign: Java HTTP 客户端开发的工具。
 *
 * 注解 EnableFeignClients 表示该电影微服务已经接入 Feign 模块。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/19
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MsConsumerMovieFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieFeignApplication.class, args);
		System.out.println("【【【【【【 电影Feign微服务 】】】】】】已启动.");
	}
}
```


## 三、Feign 简述流程分析
``` 
 总到来说，Feign的源码实现的过程如下：

 首先通过@EnableFeignCleints注解开启FeignCleint
 根据Feign的规则实现接口，并加@FeignCleint注解
 程序启动后，会进行包扫描，扫描所有的@ FeignCleint的注解的类，并将这些信息注入到ioc容器中。
 当接口的方法被调用，通过jdk的代理，来生成具体的RequesTemplate
 RequesTemplate在生成Request
 Request交给Client去处理，其中Client可以是HttpUrlConnection、HttpClient也可以是Okhttp
 最后Client被封装到LoadBalanceClient类，这个类结合类Ribbon做到了负载均衡。
``` 


## 四、测试

``` 
/****************************************************************************************
 一、电影微服务接入 Feign 进行客户端负载均衡，通过 FeignClient 调用远程 Http 微服务（测试接入 Feign 模块）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口；
 4、启动 springms-consumer-movie-feign 模块服务；
 5、在浏览器输入地址http://localhost:7910/movie/1 或者 http://localhost:7910/movie/user 都可以看到信息成功的被打印出来；

 总结：说明接入 Feign 已经成功通过测试；
 ****************************************************************************************/

/****************************************************************************************
 一、电影微服务接入 Feign 进行客户端负载均衡，通过 FeignClient 调用远程 Http 微服务（测试接入 Feign 模块进行负载均衡）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 4、启动 springms-consumer-movie-feign 模块服务；
 5、在浏览器输入地址http://localhost:7910/movie/1 连续刷新9次，正常情况可以看到 springms-provider-user 的3个端口轮询打印用户日志信息；

 总结：1、说明接入 Feign 已经成功的在客户端进行了负载均衡处理；
      2、之所以会在客户端进行轮询打印日志信息，是因为没有配置调度算法，而默认的调度算法就是轮询，所以会出现轮询打印日志信息；
 ****************************************************************************************/
```


## 五、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!






























