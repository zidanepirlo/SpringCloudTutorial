# SpringCloud（第 011 篇）电影Ribbon微服务，脱离Eureka使用配置listOfServers进行客户端负载均衡调度
-

## 一、大致介绍

``` 
1、通过尝试脱离服务治理框架，脱离 eureka 生态圈，单独操作客户端负载均衡调度；
2、本章节仅仅只是使用了 restTemplate.getForObject 来测试客户端负载均衡算法；

```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<artifactId>springms-consumer-movie-ribbon-properties-without-eureka</artifactId>
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
	</dependencies>
</project>


```


### 2.2 添加应用配置文件（springms-consumer-movie-ribbon-properties-without-eureka\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-consumer-movie-ribbon-properties-without-eureka
server:
  port: 8040
#做负载均衡的时候，不需要这个动态配置的地址
#user:
#  userServicePath: http://localhost:7900/simple/
eureka:
  client:
    healthcheck:
      enabled: true
    serviceUrl:
      defaultZone: http://admin:admin@localhost:8761/eureka
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}


ribbon:
  eureka:
    enabled: false # 禁用 eureka
springms-provider-user:
  ribbon:
    # 测试一
    listOfServers: localhost:7899


#    # 测试二
#    listOfServers: localhost:7898,localhost:7899,localhost:7900
```


### 2.3 添加实体用户类User（springms-consumer-movie-ribbon-properties-without-eureka\src\main\java\com\springms\cloud\entity\User.java）
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


### 2.4 添加Web访问层Controller（springms-consumer-movie-ribbon-properties\src\main\java\com\springms\cloud\controller\MovieRibbonPropertiesController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MoviePropertiesWithoutEurekaController {

  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private LoadBalancerClient loadBalancerClient;

  @GetMapping("/movie/{id}")
  public User findById(@PathVariable Long id) {
      // http://localhost:7900/simple/
      // VIP virtual IP
      // HAProxy Heartbeat

      ServiceInstance serviceInstance = this.loadBalancerClient.choose("springms-provider-user");
      System.out.println(">>>>>" + " " + serviceInstance.getServiceId() + ":" + serviceInstance.getHost() + ":" + serviceInstance.getPort());

      return this.restTemplate.getForObject("http://springms-provider-user/simple/" + id, User.class);
  }
}

```


### 2.5 添加电影微服务启动类（springms-consumer-movie-ribbon-properties-without-eureka\src\main\java\com\springms\cloud\MsConsumerMoviePropertiesWithoutEurekaApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 电影Ribbon微服务，脱离Eureka使用配置listOfServers进行客户端负载均衡调度。
 *
 * LoadBalanced：该负载均衡注解，已经整合了 Ribbon；
 *
 * Ribbon 的默认负载均衡的算法为：轮询；
 *
 * 配置文件优先级最高，Java代码设置的配置其次，默认的配置优先级最低；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/18
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class MsConsumerMoviePropertiesWithoutEurekaApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MsConsumerMoviePropertiesWithoutEurekaApplication.class, args);
        System.out.println("【【【【【【 电影微服务-PropertiesWithoutEureka定制Ribbon 】】】】】】已启动.");
    }
}

```


## 三、测试

``` 
/****************************************************************************************
 一、电影Ribbon微服务，脱离Eureka使用配置listOfServers进行客户端负载均衡调度（正常使用服务测试）：

 1、application.yml 配置：ribbon.eureka.enabled: false
 2、application.yml 配置：springms-provider-user.ribbon.listOfServers: localhost:7900
 3、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 4、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；

 5、启动 springms-consumer-movie-ribbon-properties-without-eureka 模块服务；
 6、在浏览器输入地址 http://localhost:8040/movie/1，连续刷新9次，然后看看 springms-provider-user、springms-provider-user2 的这几个端口的服务打印日志情况，正常情况下只会有7999该端口才会有9条用户信息日志打印出来；

 总结：之所以只有用户微服务7999端口打印日志，首先禁用了eureka的使用，如果没禁用的话，3个端口按道理都会打印日志；其次配置 listOfServers 仅仅只选择了7999一个端口的微服务；
 ****************************************************************************************/

/****************************************************************************************
 二、电影Ribbon微服务，脱离Eureka使用配置listOfServers进行客户端负载均衡调度（负载均衡调度）：

 1、application.yml 配置：ribbon.eureka.enabled: false
 2、application.yml 配置：springms-provider-user.ribbon.listOfServers: localhost:7898,localhost:7899,localhost:7900
 3、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 4、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；

 5、启动 springms-consumer-movie-ribbon-properties-without-eureka 模块服务；
 6、在浏览器输入地址 http://localhost:8040/movie/1，连续刷新9次，然后看看 springms-provider-user、springms-provider-user2 的这几个端口的服务打印日志情况，正常情况下springms-provider-user的3个端口都会打印日志，而且是轮询打印；

 总结：之所以7900、7899、7898三个端口轮询打印，是因为没有配置任何调度算法，默认的调度算法是轮询，所以3个端口当然会轮询打印用户信息；
 ****************************************************************************************/
```




## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!




























