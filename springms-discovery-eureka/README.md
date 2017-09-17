# SpringCloud（第 003 篇）服务发现服务端EurekaServer微服务
-

## 一、大致介绍

``` 

1、众所周知，在现在互联网开发中，访问地址的IP和端口号是动态的，一个服务停掉再重新启用后IP和端口就可能发生了改变，所以用硬编码是肯定不行了。于是我们尝试使用新的技术来解决这一难题。
2、于是 SpringCloud 生态圈中的服务发现脱颖而出，采用服务发现组件动态维护访问路径等关系，只需要服务提供者把IP和端口注册到服务发现组件当中，当有服务消费者需要消费服务的时候，它只需要去服务发现组件中去获取访问路径即可。
3、那么它们的关系是如何维持的呢？其实也是用到了心跳机制，说白的就是服务提供者provider和服务消费者consumer在服务发现组件当中注册之后每隔固定的时间就会发送一次心跳，服务发现组件接收到心跳便认为被管理的对象是可用的，如果长时间接收不到心跳，那么服务发现组件便认为该对象已经挂掉，便把它的注册信息删除掉。再对外提供服务的时候便不再使用挂掉的服务提供者的IP和端口。

4、而本章节仅仅只是阐述了如何搭建 EurekaServer 微服务，并且访问该 EurekaServer 需要用户名密码登录，至于后序怎么注册到该服务发现的服务端上请看后序章节。

```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-discovery-eureka</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
		<groupId>com.springms.cloud</groupId>
		<artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <dependencies>
        <!-- 服务端发现模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka-server</artifactId>
        </dependency>

        <!-- 服务端登录验证模块：进入 eureka 的网页时候需要输入登录密码的模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    </dependencies>

</project>

```


### 2.2 添加应用配置文件（springms-discovery-eureka\src\main\resources\application.yml）
``` 
# 配置 eureka 登录密码，输入地址 http://localhost:8761 时候就需要输入这个用户名密码登录进去
security:
  basic:
    enabled: true
  user:
    name: admin
    password: admin
server:
  port: 8761
eureka:
  datacenter: SpringCloud   # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Data center 显示信息
  environment: Test         # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Environment 显示信息
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://admin:admin@localhost:8761/eureka
    healthcheck:  # 健康检查
      enabled: true
  instance:

#  # 配置 eureka 首页的路径
#  dashboard:
#    enabled: true   # 如果这里配置为 false 的话，那么 Eureka 的首页将无法访问
#    path: /x        # 默认配置是 / ，但是这里配置成 /x 的话，那么访问的首页路径为: http://localhost:8761/x
#  # 目前我们这个用处就行，先暂时注释这个，不影响后面的测试用

```



### 2.3 添加 EurekaServer 微服务启动类（springms-discovery-eureka\src\main\java\com\springms\cloud\EurekaServerApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 服务发现服务端EurekaServer微服务。<br/>
 *
 * Eureka默认端口是8761
 * http://localhost:8761/eureka/apps 可以查看注册到该服务器上的一堆微服务实例的信息。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        System.out.println("【【【【【【 Eureka微服务 】】】】】】已启动.");
    }
}

```


## 三、测试

``` 
/****************************************************************************************
 一、服务发现服务端EurekaServer微服务：

 1、启动 springms-discovery-eureka 模块服务，启动1个端口；
 2、在浏览器输入地址 http://localhost:8761 后，并且输入用户名密码即可登录服务发现服务端；
 ****************************************************************************************/
```





## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

欢迎关注，您的肯定是对我最大的支持!!!
```




























