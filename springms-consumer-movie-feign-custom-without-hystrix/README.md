# SpringCloud（第 016 篇）电影微服务，定制Feign，一个Feign功能禁用Hystrix，另一个Feign功能启用Hystrix
-

## 一、大致介绍

``` 
1、在一些场景中，部分功能需要使用断路器功能，部分功能不需要断路器功能，所以才有了本章节的介绍；
2、定制 Feign 的时候，可以为 Feign 设置 Configuration 配置，在该配置中可以设置是否需要断路器功能；
3、该定制的 Configuration 在官方文件介绍中特意提到不需要被启动类所在的包下，因为该配置类不不要被扫描到；
4、当然，此章节定制后的 Feign 同样支持客户端负载均衡功能，只要开启多个用户微服务即可；
5、这里提到为什么开启多个微服务就能做到客户端负载均衡呢？
	- 简答的说就是 Feign 可以看作仅仅只是一个请求网络已被封装好的客户端HTTP请求工具类，将发出去的请求封装好;
	- 至于要请求到哪台远端微服务的话，剩下的就交给 Ribbon 来处理了，而且默认集成使用了负载均衡的Ribbon客户端;
	- 自然而然添加多个用户微服务，Feign自然就支持负载均衡的功能。
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-consumer-movie-feign-custom-without-hystrix</artifactId>
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

        <!-- Java HTTP 客户端开发的工具的模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-feign</artifactId>
        </dependency>

        <!-- Hystrix 断路器模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix</artifactId>
        </dependency>
    </dependencies>

</project>

```


### 2.2 添加应用配置文件（springms-consumer-movie-feign-custom-without-hystrix\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-consumer-movie-feign-custom-without-hystrix
server:
  port: 8110
eureka:
  client:
#    healthcheck:
#      enabled: true
    serviceUrl:
      defaultZone: http://admin:admin@localhost:8761/eureka
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}

logging:
  level:
    com.springms.cloud.feign.UserFeignCustomClient: DEBUG

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

### 2.3 添加实体用户类User（springms-consumer-movie-feign-custom-without-hystrix\src\main\java\com\springms\cloud\entity\User.java）
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


### 2.4 添加访问远端用户微服务 Feign 客户端（springms-consumer-movie-feign-custom-without-hystrix\src\main\java\com\springms\cloud\feign\UserFeignCustomClient.java）
``` 
package com.springms.cloud.feign;

import com.springms.cloud.entity.User;
import com.springms.config.TestFeignCustomConfiguration;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 用户Http请求的客户端，FeignClient 注解地方采用了自定义的配置。
 *
 * 注解FeignClient的传参：表示的是注册到 Eureka 服务上的模块名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@FeignClient(name = "springms-provider-user", configuration = TestFeignCustomConfiguration.class, fallback = UserFeignCustomClientFallback.class)
public interface UserFeignCustomClient {

    /**
     * 这里的注解 RequestLine、Param 是 Feign 的配置新的注解，详细请参考链接：https://github.com/OpenFeign/feign
     * 
     * @param id
     * @return
     */
    @RequestLine("GET /simple/{id}")
    public User findById(@Param("id") Long id);
}


/****************************************************************************************
 参考代码如下：

    interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    static class Contributor {
        String login;
        int contributions;
    }

    public static void main(String... args) {
        GitHub github = Feign.builder().decoder(new GsonDecoder()).target(GitHub.class, "https://api.github.com");

        // Fetch and print a list of the contributors to this library.
        List<Contributor> contributors = github.contributors("OpenFeign", "feign");
        for (Contributor contributor : contributors) {
            System.out.println(contributor.login + " (" + contributor.contributions + ")");
        }
    }
 ****************************************************************************************/
```


### 2.5 添加登录认证Eureka服务 Feign 客户端（springms-consumer-movie-feign-custom-without-hystrix\src\main\java\com\springms\cloud\feign\UserFeignCustomSecondClient.java）
``` 
package com.springms.cloud.feign;

import com.springms.config.TestEurekaAuthConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户Http请求的客户端，FeignClient 注解地方采用了自定义的配置。
 *
 * 注解FeignClient的传参：表示的是注册到 Eureka 服务上的模块名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@FeignClient(name = "xxx", url = "http://localhost:8761/", configuration = TestEurekaAuthConfiguration.class, fallback = UserFeignCustomSecondClientFallback.class)
public interface UserFeignCustomSecondClient {

    @RequestMapping(value = "/eureka/apps/{serviceName}")
    public String findEurekaInfo(@PathVariable("serviceName") String serviceName);
}
```


### 2.6 添加访问远端用户微服务 Fallback 类（springms-consumer-movie-feign-custom\src\main\java\com\springms\config\TestFeignCustomConfiguration.java）
``` 
package com.springms.cloud.feign;

import com.springms.cloud.entity.User;
import org.springframework.stereotype.Component;

/**
 * Hystrix 客户端回退机制类。
 *
 * 这里加上注解 Component 的目的：就是因为没有这个注解，运行时候会报错，报错会说没有该类的这个实例，所以我们就想到要实例化这个类，因此加了这个注解。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@Component
public class UserFeignCustomClientFallback implements  UserFeignCustomClient{

    /**
     * Fallback 回退降级的方法，返回一个默认的用户信息。
     *
     * @param id
     * @return
     */
    @Override
    public User findById(Long id) {
        System.out.println("========== findById Fallback " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());

        User tmpUser = new User();
        tmpUser.setId(0L);
        return tmpUser;
    }
}
```

### 2.7 添加登录认证Eureka服务 Fallback 类（springms-consumer-movie-feign-custom\src\main\java\com\springms\config\TestEurekaAuthConfiguration.java）
``` 
package com.springms.cloud.feign;

import org.springframework.stereotype.Component;

/**
 * Hystrix 客户端回退机制类。
 *
 * 这里加上注解 Component 的目的：就是因为没有这个注解，运行时候会报错，报错会说没有该类的这个实例，所以我们就想到要实例化这个类，因此加了这个注解。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@Component
public class UserFeignCustomSecondClientFallback implements UserFeignCustomSecondClient{

    @Override
    public String findEurekaInfo(String serviceName) {
        System.out.println("========== findEurekaInfo Fallback " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());
        return "springms-provider-user";
    }
}
```


### 2.8 添加访问远端用户微服务配置类（springms-consumer-movie-feign-custom-without-hystrix\src\main\java\com\springms\config\TestFeignCustomConfiguration.java）
``` 
package com.springms.config;

import feign.Contract;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义配置。
 *
 * 不和 com.springms.cloud 在同级目录，因为文档有说明，不要被扫描到即可。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@Configuration
public class TestFeignCustomConfiguration {

    @Bean
    public Contract feignContract(){
        return new feign.Contract.Default();
    }

    /**
     * 日志级别配置
     *
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
```


### 2.9 添加访问远端Eureka微服务配置类（springms-consumer-movie-feign-custom-without-hystrix\src\main\java\com\springms\config\TestEurekaAuthConfiguration.java）
``` 
package com.springms.config;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 认证配置，由于 UserFeignCustomSecondClient 访问 http://localhost:8761/ 需要密码登录，所以才有了此配置的出现。
 *
 * 不和 com.springms.cloud 在同级目录，因为文档有说明，不要被扫描到即可。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@Configuration
public class TestEurekaAuthConfiguration {

    /**
     * 此方法主要配置登录 Eureka 服务器的帐号与密码。
     *
     * @return
     */
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(){
        return new BasicAuthRequestInterceptor("admin", "admin");
    }

    /**
     * 在该配置中，加入这个方法的话，表明使用了该配置的地方，就会禁用该模块使用 Hystrix 容灾降级的功能；
     *
     * @return
     */
    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder(){
        return Feign.builder();
    }
}
```



### 2.10 添加Web访问层Controller（springms-consumer-movie-feign-custom-without-hystrix\src\main\java\com\springms\cloud\controller\MovieFeignCustomWithoutHystrixController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.feign.UserFeignCustomClient;
import com.springms.cloud.feign.UserFeignCustomSecondClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieFeignCustomWithoutHystrixController {

    @Autowired
    private UserFeignCustomClient userFeignCustomClient;

    @Autowired
    private UserFeignCustomSecondClient userFeignCustomSecondClient;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        System.out.println("======== findById Controller " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());
        return userFeignCustomClient.findById(id);
    }

    @GetMapping("/{serviceName}")
    public String findEurekaInfo(@PathVariable String serviceName){
        System.out.println("======== findEurekaInfo Controller " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());
        return userFeignCustomSecondClient.findEurekaInfo(serviceName);
    }
}
```


### 2.11 添加电影微服务启动类（springms-consumer-movie-feign-custom-without-hystrix\src\main\java\com\springms\cloud\MsConsumerMovieFeignCustomWithoutHystrixApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 *
 * 电影微服务，定制Feign，一个Feign功能禁用Hystrix，另一个Feign功能启用Hystrix。
 *
 * Feign: Java HTTP 客户端开发的工具。
 *
 * 注解 EnableFeignClients 表示该电影微服务已经接入 Feign 模块。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MsConsumerMovieFeignCustomWithoutHystrixApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieFeignCustomWithoutHystrixApplication.class, args);
		System.out.println("【【【【【【 电影自定义FeignWithoutHystrix微服务 】】】】】】已启动.");
	}
}
```


## 三、测试

``` 
/****************************************************************************************
 一、电影微服务，定制Feign，一个Feign功能禁用Hystrix，另一个Feign功能启用Hystrix（正常功能测试）：

 1、编写：UserFeignCustomClientFallback、UserFeignCustomSecondClientFallback 断路器回退客户端类；
 2、编写 TestEurekaAuthConfiguration 配置，加入禁用 Hystrix 模块功能的代码，表示禁用该配置的客户端模块；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-feign-custom-without-hystrix 模块服务；
 6、在浏览器输入地址 http://localhost:8110/movie/1 可以看到信息成功的被打印出来，表明正常情况下一切正常；
 7、在浏览器输入地址 http://localhost:8110/springms-provider-user 可以看到信息成功的被打印出来，表明正常情况下一切正常；

 注意：如果第6步如果一开始就输入ID = 0 的用户信息，不要灰心，耐心等等，说不定服务之间还没通信链接成功呢。。。
      如果第6步的地址输入错误，请回头看看 springms-provider-user 该微服务 appname 的名字，再次输入http://localhost:8110/ + appname 即可试试；

 ****************************************************************************************/

/****************************************************************************************
 二、电影微服务，定制Feign，一个Feign功能禁用Hystrix，另一个Feign功能启用Hystrix（禁用其中一个 Feign 的断路器功能）：

 1、编写：UserFeignCustomClientFallback、UserFeignCustomSecondClientFallback 断路器回退客户端类；
 2、编写 TestEurekaAuthConfiguration 配置，加入禁用 Hystrix 模块功能的代码，表示禁用该配置的客户端模块；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-feign-custom-without-hystrix 模块服务；
 6、在浏览器输入地址 http://localhost:8110/movie/1 可以看到信息成功的被打印出来，表明正常情况下一切正常；
 7、在浏览器输入地址 http://localhost:8110/springms-provider-user 可以看到信息成功的被打印出来，表明正常情况下一切正常；

 8、关闭 springms-provider-user 模块服务；
 9、在浏览器输入地址 http://localhost:8110/movie/1 可以看到用户ID = 0 的用户信息被打印出来，表明该模块的Hystrix断路器模块起作用了；

 10、再关闭 springms-discovery-eureka 模块服务；
 11、再在浏览器输入地址 http://localhost:8110/appname-springms-provider-user 可以看到网页已经打印出链接不上服务的错误页面了，表明该模块禁用Hystrix断路器模块也起作用了；
 ****************************************************************************************/
```


## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!






























