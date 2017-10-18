# SpringCloud（第 014 篇）电影 Ribbon 微服务集成 Hystrix 断路器实现失败快速响应，达到熔断效果
-

## 一、大致介绍

``` 
1、Hystrix 断路器的原理很简单，如同电力过载保护器。它可以实现快速失败，如果它在一段时间内侦测到许多类似的错误，会强迫其以后的多个调用快速失败，不再访问远程服务器，从而防止应用程序不断地尝试执行可能会失败的操作，使得应用程序继续执行而不用等待修正错误，或者浪费CPU时间去等到长时间的超时产生。熔断器也可以使应用程序能够诊断错误是否已经修正，如果已经修正，应用程序会再次尝试调用操作；
2、而本章节主要简单的使用了当下游服务出现宕机或者意外情况不可用时，Hystrix实现了快速失败快速响应来达到熔断机制效果；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-consumer-movie-ribbon-with-hystrix</artifactId>
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

        <!-- Hystrix 断路器模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix</artifactId>
        </dependency>

        <!-- 监控和管理生产环境的模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

</project>

```


### 2.2 添加应用配置文件（springms-consumer-movie-ribbon-with-hystrix\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-consumer-movie-ribbon-with-hystrix
server:
  port: 8070
#做负载均衡的时候，不需要这个动态配置的地址
#user:
#  userServicePath: http://localhost:7900/simple/
eureka:
  client:
#    healthcheck:
#      enabled: true
    serviceUrl:
      defaultZone: http://admin:admin@localhost:8761/eureka
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}


# 解决第一次请求报超时异常的方案，因为 hystrix 的默认超时时间是 1 秒，因此请求超过该时间后，就会出现页面超时显示 ：
#
# 这里就介绍大概三种方式来解决超时的问题，解决方案如下：
#
# 第一种方式：将 hystrix 的超时时间设置成 5000 毫秒
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 5000
#
# 或者：
# 第二种方式：将 hystrix 的超时时间直接禁用掉，这样就没有超时的一说了，因为永远也不会超时了
# hystrix.command.default.execution.timeout.enabled: false
#
# 或者：
# 第三种方式：索性禁用feign的hystrix支持
# feign.hystrix.enabled: false ## 索性禁用feign的hystrix支持

# 超时的issue：https://github.com/spring-cloud/spring-cloud-netflix/issues/768
# 超时的解决方案： http://stackoverflow.com/questions/27375557/hystrix-command-fails-with-timed-out-and-no-fallback-available
# hystrix配置： https://github.com/Netflix/Hystrix/wiki/Configuration#execution.isolation.thread.timeoutInMilliseconds
```

### 2.3 添加实体用户类User（springms-consumer-movie-ribbon-with-hystrix\src\main\java\com\springms\cloud\entity\User.java）
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




### 2.4 添加电影Web访问层Controller（springms-consumer-movie-ribbon-with-hystrix\src\main\java\com\springms\cloud\controller\MovieRibbonHystrixController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Web控制器测试断路器功能。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/21
 *
 */
@RestController
public class MovieRibbonHystrixController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/movie/{id}")
    @HystrixCommand(fallbackMethod = "findByIdFallback")
    public User findById(@PathVariable Long id) {
        // http://localhost:7900/simple/
        // VIP：virtual IP
        // HAProxy Heartbeat

        return this.restTemplate.getForObject("http://springms-provider-user/simple/" + id, User.class);
    }

    /**
     * 当 springms-provider-user 服务宕机或者不可用时，即请求超时后会调用此方法。
     *
     * @param id
     * @return
     */
    public User findByIdFallback(Long id) {
        User user = new User();
        user.setId(0L);
        return user;
    }
}
```


### 2.5 添加电影微服务启动类（springms-consumer-movie-ribbon-with-hystrix\src\main\java\com\springms\cloud\MsConsumerMovieRibbonHystrixApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 电影 Ribbon 微服务集成 Hystrix 断路器实现失败快速响应，达到熔断效果。
 *
 * 注解 EnableCircuitBreaker 表明需要集成断路器模块；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/21
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class MsConsumerMovieRibbonHystrixApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieRibbonHystrixApplication.class, args);
		System.out.println("【【【【【【 电影微服务-Hystrix 】】】】】】已启动.");
	}
}
```


## 三、测试

``` 
/****************************************************************************************
 一、电影 Ribbon 微服务集成 Hystrix 断路器实现失败快速响应，达到熔断效果：

 1、注解：EnableCircuitBreaker、HystrixCommand 的编写；
 2、启动 springms-provider-user 模块服务，启动1个端口；
 3、启动 springms-consumer-movie-ribbon-with-hystrix 模块服务；
 4、在浏览器输入地址http://localhost:8070/movie/1，然后页面的信息是否有打印出来用户的Id=0的情况，正常情况下是没有用户Id=0的情况信息打印的；

 5、杀死 springms-provider-user 模块服务，停止提供服务；
 6、在浏览器输入地址http://localhost:8070/movie/1，然后页面的信息是否有打印出来用户的Id=0的情况，等了1秒中后有用户Id=0的情况信息打印出来；

 7、等一会儿在启动 springms-provider-user 模块服务，启动1个端口；
 8、在浏览器输入地址http://localhost:8070/movie/1，然后页面的信息又有Id!=0的用户信息打印出来；

 总结：当远端微服务宕机或者不可用时，Hystrix已经达到快速响应快速失败，起到了熔断机制的效果。
 ****************************************************************************************/
```


## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!






























