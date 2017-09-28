# SpringCloud（第 027 篇）集成异构微服务系统到 SpringCloud 生态圈中(比如集成 nodejs 微服务)
-

## 一、大致介绍

``` 
1、在一些稍微复杂点系统中，往往都不是单一代码写的服务，而恰恰相反集成了各种语言写的系统，并且我们还要很好的解耦合集成到自己的系统中；
2、出于上述现状，SpringCloud 生态圈中给我们提供了很好的插件式服务，利用 sidecar 我们也可以轻松方便的集成异构系统到我们自己的系统来；
3、而本章节目的就是为了解决轻松简便的集成异构系统到自己的微服务系统中来的；
```


## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-sidecar</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.springms.cloud</groupId>
        <artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <dependencies>
        <!-- 异构系统模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-netflix-sidecar</artifactId>
        </dependency>

        <!-- 客户端发现模块，由于文档说 Zuul 的依赖里面不包括 eureka 客户端发现模块，所以这里还得单独添加进来 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-sidecar\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-sidecar
server:
  port: 8210
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
# 打印日志
logging:
  level:
    root: INFO
    com.springms: DEBUG
#####################################################################################################





#####################################################################################################
# 异构微服务的配置， port 代表异构微服务的端口；health-uri 代表异构微服务的操作链接地址
sidecar:
  port: 8205
  health-uri: http://localhost:8205/health.json
#####################################################################################################

```




### 2.3 添加sidecar微服务启动类（springms-sidecar\src\main\java\com\springms\cloud\MsSideCarApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

/**
 * 集成异构微服务系统到 SpringCloud 生态圈中(比如集成 nodejs 微服务)。
 *
 * 注意 EnableSidecar 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableSidecar --> { @EnableCircuitBreaker、@EnableDiscoveryClient、@EnableZuulProxy } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解，还包含了 zuul API网关模块。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/28
 *
 */
@SpringBootApplication
@EnableSidecar
public class MsSideCarApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSideCarApplication.class, args);
        System.out.println("【【【【【【 SideCar 微服务 】】】】】】已启动.");
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、集成异构微服务系统到 SpringCloud 生态圈中(比如集成 nodejs 微服务)（正常情况测试）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableSidecar 配置；
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-gateway-zuul-fallback 模块服务，启动1个端口；
 4、启动 springms-sidecar 模块服务，启动1个端口；
 5、启动 springms-node-service 微服务，启动1个端口；

 6、新起网页页签，输入 http://localhost:8205/ 正常情况下是能看到 "欢迎来到简单异构系统之 nodejs 服务首页" 信息被打印出来；
 7、新起网页页签，然后输入 http://localhost:8205/health.json，正常情况下是能看到 "{"status":"UP"}" 信息被打印出来；

 总结一：nodejs 微服务，自己访问自己都是正常的；

 8、新起网页页签，输入 http://localhost:8200/springms-sidecar/ 正常情况下是能看到 "欢迎来到简单异构系统之 nodejs 服务首页" 信息被打印出来；
 9、新起网页页签，然后输入 http://localhost:8200/springms-sidecar/health.json，正常情况下是能看到 "{"status":"UP"}" 信息被打印出来；

 总结二：通过在yml配置文件中添加 sidecar 属性，就可以将异构系统添加到SpringCloud生态圈中，完美无缝衔接；
 ****************************************************************************************/

/****************************************************************************************
 二、集成异构微服务系统到 SpringCloud 生态圈中(比如集成 nodejs 微服务)（除了包含异构微服务外，还添加 Ribbon 模块电影微服务）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableSidecar 配置；
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-gateway-zuul-fallback 模块服务，启动1个端口；
 4、启动 springms-sidecar 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-ribbon 模块服务，启动1个端口；
 6、启动 springms-node-service 微服务，启动1个端口；

 7、新起网页页签，输入 http://localhost:8205/ 正常情况下是能看到 "欢迎来到简单异构系统之 nodejs 服务首页" 信息被打印出来；
 8、新起网页页签，然后输入 http://localhost:8205/health.json 正常情况下是能看到 "{"status":"UP"}" 信息被打印出来；

 总结一：nodejs 微服务，自己访问自己都是正常的；

 9、新起网页页签，输入 http://localhost:8200/springms-sidecar/ 正常情况下是能看到 "欢迎来到简单异构系统之 nodejs 服务首页" 信息被打印出来；
 10、新起网页页签，然后输入 http://localhost:8200/springms-sidecar/health.json 正常情况下是能看到 "{"status":"UP"}" 信息被打印出来；

 总结二：通过 Zuul 代理模块，统一入口路径，也可以从 zuul 上成功访问异构系统；

 11、新起网页页签，输入 http://localhost:8010/sidecar/ 正常情况下是能看到 "欢迎来到简单异构系统之 nodejs 服务首页" 信息被打印出来；
 12、新起网页页签，然后输入 http://localhost:8010/sidecar/health.json 正常情况下是能看到 "{"status":"UP"}" 信息被打印出来；

 总结三：给 springms-consumer-movie-ribbon 微服务添加几个方法，也可以成功访问以异构系统，可见利用 SpringCloud 来集成异构系统简便了很多；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

SpringCloudTutorial交流QQ群: 235322432

欢迎关注，您的肯定是对我最大的支持!!!
```






























