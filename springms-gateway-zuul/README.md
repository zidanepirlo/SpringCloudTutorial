# SpringCloud（第 018 篇）Zuul 服务 API 网关微服务之代理与反向代理
-

## 一、大致介绍

``` 
1、API 服务网关顾名思义就是统一入口，类似 nginx、F5 等功能一样，统一代理控制请求入口，弱化各个微服务被客户端记忆功能；
2、本章节主要讲解了使用 zuul 的代理功能与反向代理功能，当然 zuul 还有很多属性设置，我就没一一列举所有的测试方法了；
3、http://localhost:8150/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-gateway-zuul</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.springms.cloud</groupId>
        <artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <dependencies>
        <!-- API网关模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zuul</artifactId>
        </dependency>

        <!-- 客户端发现模块，由于文档说 Zuul 的依赖里面不包括 eureka 客户端发现模块，所以这里还得单独添加进来 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-gateway-zuul\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-gateway-zuul
server:
  port: 8150
eureka:
  datacenter: SpringCloud   # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Data center 显示信息
  environment: Test         # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Environment 显示信息
  client:
    service-url:
      defaultZone: http://admin:admin@localhost:8761/eureka
#    healthcheck:  # 健康检查
#      enabled: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}


#####################################################################################################
# 测试二，自定义路径配置，给 springms-provider-user 微服务添加前缀地址，反向代理用户微服务
#zuul:
#  routes:
#    springms-provider-user: /user/**



## 测试三，自定义路径配置，给 springms-provider-user 微服务添加前缀地址，反向代理用户微服务，其它代理路径一律失效
#zuul:
#  ignoredServices: '*'
#  routes:
#    springms-provider-user: /user/**



# 测试四，自定义路径配置，给 springms-provider-user 微服务添加前缀地址，代理、反向代理用户微服务，忽略禁用 springms-consumer-movie 代理、反向代理路径
#zuul:
#  ignoredServices: springms-consumer-movie
#  routes:
#    springms-provider-user: /user/**
#####################################################################################################

#####################################################################################################
# 打印日志
logging:
  level:
    root: INFO
    com.springms: DEBUG
#####################################################################################################

#####################################################################################################
ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 60000
#####################################################################################################

#####################################################################################################
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
#####################################################################################################
```




### 2.3 添加zuul服务网关微服务启动类（springms-gateway-zuul\src\main\java\com\springms\cloud\MsGatewayZuulApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Zuul 服务 API 网关微服务之代理与反向代理。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8150/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@SpringBootApplication
@EnableZuulProxy
public class MsGatewayZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulApplication.class, args);
        System.out.println("【【【【【【 GatewayZuul微服务 】】】】】】已启动.");
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、Zuul 服务 API 网关微服务之代理与反向代理（正常情况测试）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-consumer-movie 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul 模块服务；

 6、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 7、新起网页页签，输入 http://localhost:7901/movie/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第6、7步正常，说明 springms-provider-user、springms-consumer-movie 两个服务目前正常；

 8、新起网页页签，然后输入 http://localhost:8150/springms-provider-user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 9、新起网页页签，然后输入 http://localhost:8150/springms-consumer-movie/movie/4，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结二：第8、9步也能正常打印用户信息，说明 API 网关已经生效了，可以通过API服务器地址链接各个微服务的 http://localhost:8150/serviceId/path 这样的路径来访问了；
 ****************************************************************************************/

/****************************************************************************************
 二、Zuul 服务 API 网关微服务之代理与反向代理（自定义路径配置，给 springms-provider-user 微服务添加前缀地址，反向代理用户微服务）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
     # 测试二，自定义路径配置，给 springms-provider-user 微服务添加前缀地址，反向代理用户微服务
     zuul:
        routes:
            springms-provider-user: /user/**
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-consumer-movie 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul 模块服务；

 6、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 7、新起网页页签，输入 http://localhost:7901/movie/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第6、7步正常，说明 springms-provider-user、springms-consumer-movie 两个服务目前正常；

 8、新起网页页签，然后输入 http://localhost:8150/springms-provider-user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 9、新起网页页签，然后输入 http://localhost:8150/springms-consumer-movie/movie/4，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结二：第8、9步也能正常打印用户信息，说明 API 网关已经生效了，可以通过API服务器地址链接各个微服务的 http://localhost:8150/serviceId/path 这样的路径来访问了；

 10、新起网页页签，然后输入 http://localhost:8150/user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来，可见【用户微服务】的地址被改变生效了，同时被 API 网关反向代理了，也就是说 http 的请求 /user 将被发送到【用户微服务】；
 11、新起网页页签，然后输入 http://localhost:8150/user/movie/4，正常情况下访问不通，理应访问不通的；

 总结三：zuul.routes 属性仅仅只是为了给 springms-provider-user 微服务添加了 user 前缀，所以电影微服务加 user 前缀当然访问不通的；
 ****************************************************************************************/

/****************************************************************************************
 三、Zuul 服务 API 网关微服务之代理与反向代理（自定义路径配置，给 springms-provider-user 微服务添加前缀地址，反向代理用户微服务，其它代理路径一律失效）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
     # 测试三，自定义路径配置，给User微服务添加前缀地址，反向代理User微服务，不反向代理电影微服务
     zuul:
        ignoredServices: '*'
        routes:
            springms-provider-user: /user/**
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-consumer-movie 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul 模块服务；

 6、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 7、新起网页页签，输入 http://localhost:7901/movie/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第6、7步正常，说明 springms-provider-user、springms-consumer-movie 两个服务目前正常；

 8、新起网页页签，然后输入 http://localhost:8150/springms-provider-user/simple/3，正常情况下不能被代理了，访问页面不存在，出现404错误码；
 9、新起网页页签，然后输入 http://localhost:8150/springms-consumer-movie/movie/4，正常情况下不能被代理了，访问页面不存在，出现404错误码；

 总结二：第8、9步访问出现404错误码，说明通过 http://localhost:8150/serviceId/path 代理路径访问 API 网关已经失效了；

 10、新起网页页签，然后输入 http://localhost:8150/user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来，可见【用户微服务】的地址被改变生效了，同时被 API 网关反向代理了，也就是说 http 的请求 /user 将被发送到【用户微服务】；
 11、新起网页页签，然后输入 http://localhost:8150/user/movie/4，正常情况下访问不通，理应访问不通的；

 总结三：zuul.routes ignoredServices 忽略禁用了所有代理路径，但仅仅只是为了给 springms-provider-user 微服务添加了 user 前缀供反向代理路径访问，所以电影微服务加 user 前缀当然访问不通的；
 ****************************************************************************************/

/****************************************************************************************
 四、Zuul 服务 API 网关微服务之代理与反向代理（自定义路径配置，给 springms-provider-user 微服务添加前缀地址，代理、反向代理用户微服务，忽略禁用 springms-consumer-movie 代理、反向代理路径）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
     # 测试四，自定义路径配置，给 springms-provider-user 微服务添加前缀地址，代理、反向代理用户微服务，忽略禁用 springms-consumer-movie 代理、反向代理路径
     zuul:
         ignoredServices: springms-consumer-movie
         routes:
            springms-provider-user: /user/**
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-consumer-movie 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul 模块服务；

 6、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 7、新起网页页签，输入 http://localhost:7901/movie/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第6、7步正常，说明 springms-provider-user、springms-consumer-movie 两个服务目前正常；

 8、新起网页页签，然后输入 http://localhost:8150/springms-provider-user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来，可见【用户微服务】的地址被改变生效了，同时被 API 网关反向代理了，也就是说 http 的请求 /user 将被发送到【用户微服务】；
 9、新起网页页签，然后输入 http://localhost:8150/springms-consumer-movie/movie/4，正常情况下不能被代理了，访问页面不存在，出现404错误码；

 总结二：zuul.routes ignoredServices 忽略禁用了 springms-consumer-movie 【电影微服务】的代理路径，所以电影微服务的代理路径当然访问不通的；

 10、新起网页页签，然后输入 http://localhost:8150/user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来，可见【用户微服务】的地址被改变生效了，同时被 API 网关反向代理了，也就是说 http 的请求 /user 将被发送到【用户微服务】；
 11、新起网页页签，然后输入 http://localhost:8150/user/movie/4，正常情况下访问不通，理应访问不通的；

 总结三：zuul.routes ignoredServices 忽略禁用了 springms-consumer-movie 【电影微服务】的代理路径，所以电影微服务的代理路径当然访问不通的；

 注意：测试三、测试四的区别在于，ignoredServices 属性的设置，影响的是 springms-consumer-movie 微服务的代理路径是否可以访问；
 ****************************************************************************************/
```


## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!






























