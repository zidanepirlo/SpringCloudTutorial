# SpringCloud（第 013 篇）电影微服务使用定制化 Feign 在客户端进行负载均衡调度并为 Feign 配置帐号密码登录认证 Eureka
-

## 一、大致介绍

``` 
1、定制 Feign 实现访问远端微服务；
2、为 Feign 配置帐号密码来登录认证 Eureka 服务发现模块；
3、修改 Feign 的日志打印级别；
4、定制 Feign 也毫不掩饰的支持负载均衡调度功能；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-consumer-movie-feign-custom</artifactId>
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
    </dependencies>

</project>

```


### 2.2 添加应用配置文件（springms-consumer-movie-feign-custom\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-consumer-movie-feign-custom
server:
  port: 8050
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
# hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 5000
#
# 或者：
# 第二种方式：将 hystrix 的超时时间直接禁用掉，这样就没有超时的一说了，因为永远也不会超时了
# hystrix.command.default.execution.timeout.enabled: false
#
# 或者：
# 第三种方式：索性禁用feign的hystrix支持
feign.hystrix.enabled: false ## 索性禁用feign的hystrix支持

# 超时的issue：https://github.com/spring-cloud/spring-cloud-netflix/issues/768
# 超时的解决方案： http://stackoverflow.com/questions/27375557/hystrix-command-fails-with-timed-out-and-no-fallback-available
# hystrix配置： https://github.com/Netflix/Hystrix/wiki/Configuration#execution.isolation.thread.timeoutInMilliseconds
```

### 2.3 添加实体用户类User（springms-consumer-movie-feign-custom\src\main\java\com\springms\cloud\entity\User.java）
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


### 2.4 添加访问远端服务 Feign 客户端（springms-consumer-movie-feign-custom\src\main\java\com\springms\cloud\feign\UserFeignCustomClient.java）
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
 * @date 2017/9/19
 *
 */
@FeignClient(name = "springms-provider-user", configuration = TestFeignCustomConfiguration.class)
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


### 2.5 添加登录认证Eureka服务 Feign 客户端（springms-consumer-movie-feign-custom\src\main\java\com\springms\cloud\feign\UserFeignCustomSecondClient.java）
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
 * @date 2017/9/19
 *
 */
@FeignClient(name = "xxx", url = "http://localhost:8761/", configuration = TestEurekaAuthConfiguration.class)
public interface UserFeignCustomSecondClient {

    @RequestMapping(value = "/eureka/apps/{serviceName}")
    public String findEurekaInfo(@PathVariable("serviceName") String serviceName);
}
```


### 2.6 添加访问远端服务配置类（springms-consumer-movie-feign-custom\src\main\java\com\springms\config\TestFeignCustomConfiguration.java）
``` 
package com.springms.config;

import feign.Contract;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义配置。
 *
 * 不和 com.springms.cloud 在同级目录，因为文档有说明，该配置文件不需要被扫描到。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/19
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

### 2.7 添加登录认证Eureka服务配置（springms-consumer-movie-feign-custom\src\main\java\com\springms\config\TestEurekaAuthConfiguration.java）
``` 
package com.springms.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 认证配置，由于 UserFeignCustomSecondClient 访问 http://localhost:8761/ 需要密码登录，所以才有了此配置的出现。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/19
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
}
```

### 2.8 添加Web访问层Controller（springms-consumer-movie-feign-custom\src\main\java\com\springms\cloud\controller\MovieFeignCustomController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.feign.UserFeignCustomClient;
import com.springms.cloud.feign.UserFeignCustomSecondClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自定义 Feign 控制器。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/19
 *
 */
@RestController
public class MovieFeignCustomController {

    @Autowired
    private UserFeignCustomClient userFeignCustomClient;

    @Autowired
    private UserFeignCustomSecondClient userFeignCustomSecondClient;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        return userFeignCustomClient.findById(id);
    }

    @GetMapping("/{serviceName}")
    public String findEurekaInfo(@PathVariable String serviceName){
        return userFeignCustomSecondClient.findEurekaInfo(serviceName);
    }
}

```


### 2.9 添加电影微服务启动类（springms-consumer-movie-feign-custom\src\main\java\com\springms\cloud\MsConsumerMovieFeignCustomApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 *
 * 电影微服务使用定制化 Feign 在客户端进行负载均衡调度并为 Feign 配置帐号密码登录认证 Eureka。
 *
 * Feign: Java HTTP 客户端开发的工具。
 *
 * 注解 EnableFeignClients 表示该电影微服务已经接入 Feign 模块。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/19
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MsConsumerMovieFeignCustomApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieFeignCustomApplication.class, args);
		System.out.println("【【【【【【 电影自定义Feign微服务 】】】】】】已启动.");
	}
}
```


## 三、测试

``` 
/****************************************************************************************
 一、电影微服务使用定制化Feign在客户端进行负载均衡调度并为Feign配置帐号密码登录认证Eureka（测试接入 Feign 模块）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口；
 4、启动 springms-consumer-movie-feign-custom 模块服务；
 5、在浏览器输入地址http://localhost:8050/movie/1 可以看到信息成功的被打印出来；

 总结：说明接入 Feign 已经成功通过测试；
 ****************************************************************************************/

/****************************************************************************************
 二、电影微服务使用定制化Feign在客户端进行负载均衡调度并为Feign配置帐号密码登录认证Eureka（测试登录 Eureka 服务器需要认证配置）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口；
 4、启动 springms-consumer-movie-feign-custom 模块服务；
 5、在浏览器输入地址 http://localhost:8050/springms-provider-user 可以看到信息成功的被打印出来；

 总结：说明 TestEurekaAuthConfiguration 类中配置的帐号密码已经生效，可以正常访问 Eureka 服务；
 ****************************************************************************************/

/****************************************************************************************
 三、电影微服务使用定制化Feign在客户端进行负载均衡调度并为Feign配置帐号密码登录认证Eureka（测试接入 Feign 模块进行负载均衡）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 4、启动 springms-consumer-movie-feign-custom 模块服务；
 5、在浏览器输入地址http://localhost:8050/movie/1 连续刷新9次，正常情况可以看到 springms-provider-user 的3个端口轮询打印用户日志信息；

 总结：1、说明接入 Feign 已经成功的在客户端进行了负载均衡处理；
 2、之所以会在客户端进行轮询打印日志信息，是因为没有配置调度算法，而默认的调度算法就是轮询，所以会出现轮询打印日志信息；
 ****************************************************************************************/

/****************************************************************************************
 四、电影微服务使用定制化Feign在客户端进行负载均衡调度并为Feign配置帐号密码登录认证Eureka（配置日志级别）：

 1、application.yml 修改：logging.level.com.springms.cloud.feign.UserFeignCustomClient: DEBUG
 2、编写 TestFeignCustomConfiguration 新增日志级别的方法
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-feign-custom 模块服务；
 6、在浏览器输入地址http://localhost:8050/springms-provider-user 可以看到控制台中 DEBUG 日志级别的信息成功的被打印出来；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

欢迎关注，您的肯定是对我最大的支持!!!
```






























