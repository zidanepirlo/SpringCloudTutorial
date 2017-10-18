# SpringCloud（第 020 篇）Zuul 网关模块添加 listOfServers 属性，达到客户端负载均衡的能力
-

## 一、大致介绍

``` 
1、本章节添加另外一个属性 listOfServers 来给 zuul 赋上异样的功能色彩，提供负载均衡的能力；
2、而其实说到底 zuul 的负载能力还是在于 ribbon，因为 ribbon 才是真正做到让 zuul 达到客户端负载均衡能力的本质；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-gateway-zuul-cluster</artifactId>
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


### 2.2 添加应用配置文件（springms-gateway-zuul-cluster\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-gateway-zuul-cluster
server:
  port: 8165
eureka:
  datacenter: SpringCloud   # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Data center 显示信息
  environment: Test         # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Environment 显示信息
  client:
    service-url:
      defaultZone: http://admin:admin@localhost:8761/eureka
    healthcheck:  # 健康检查
      enabled: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}


#####################################################################################################
# 测试一，API网关模块发现应用入口（添加 listOfServers 属性，测试 zuul 的负载均衡功能）
zuul:
  routes:
    hmily:
      path: /custom-path/**
      serviceId: springms-provider-user

# 注意，这里在运行的时候有个坑，如果以树形展开写法的话，那么就会出错了，所以这个配置还是避免用树形写法
ribbon.eureka.enabled: false

springms-provider-user: # 这里是 ribbon 要请求的微服务的 service-id 值
  ribbon:
    listOfServers: http://localhost:7900,http://localhost:7899,http://localhost:7898
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




### 2.3 添加zuul服务网关微服务启动类（springms-gateway-zuul-cluster\src\main\java\com\springms\cloud\MsGatewayZuulClusterApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Zuul 网关模块添加 listOfServers 属性，达到客户端负载均衡的能力。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8165/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
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
public class MsGatewayZuulClusterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulClusterApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulCluster微服务 】】】】】】已启动.");
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、Zuul 网关模块添加 listOfServers 属性，达到客户端负载均衡的能力：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
     # 测试一，API网关模块发现应用入口（添加 listOfServers 属性，测试 zuul 的负载均衡功能）
     zuul:
         routes:
             hmily:
                 path: /user-serviceId/**
                 serviceId: springms-provider-user

     # 注意，这里在运行的时候有个坑，如果以树形展开写法的话，那么就会出错了，所以这个配置还是避免用树形写法
     ribbon.eureka.enabled: false

     springms-provider-user: # 这里是 ribbon 要请求的微服务的 service-id 值
         ribbon:
            listOfServers: http://localhost:7900,http://localhost:7899,http://localhost:7898
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-gateway-zuul-cluster 模块服务；

 5、新起网页页签，输入 http://localhost:7900/simple/5 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第5步正常，说明 springms-provider-user 服务目前正常；

 6、新起网页页签，然后输入 http://localhost:8165/springms-provider-user/simple/6，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结二：第6步也能正常打印用户信息，说明 API 网关已经生效了，可以通过API服务器地址链接各个微服务的 http://localhost:8150/serviceId/path 这样的路径来访问了；

 7、新起网页页签，然后输入 http://localhost:8165/user-serviceId/simple/1，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结三：path、serviceId 设置的反向代理路径也通了；

 8、清除 springms-provider-user 模块控制台的所有的日志，然后刷新 9 次该地址 http://localhost:8165/user-serviceId/simple/1 的网页，然后会发现 3 个用户服务都各打印了3次，再多刷新几次，会发现该负载均衡的调度的算法是轮论调，依次轮询调用每个用户服务微服务；

 总结四：listOfServers 属性也生效了，从而说明添加 listOfServers 也可以达到 zuul 负载均衡的能力；
 ****************************************************************************************/
```


## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!






























