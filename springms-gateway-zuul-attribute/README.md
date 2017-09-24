# SpringCloud（第 019 篇）Zuul 网关微服务的一些属性应用测试
-

## 一、大致介绍

``` 
1、本章节根据官网资料，尝试了一些其它属性的设置，比如 path、serviceId、prefix、strip-prefix 等应用；
2、这些组合试用的场景大多数在一些地址方面需要重新映射或者针对特殊地址做特殊处理等，至于其它一些深层次的应用大家做过知道的话也可以告尽情回帖让大家都来学习学习。
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-gateway-zuul-attribute</artifactId>
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


### 2.2 添加应用配置文件（springms-gateway-zuul-attribute\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-gateway-zuul-attribute
server:
  port: 8155
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
# 测试一，自定义路径配置，给 springms-provider-user 微服务添加 path、serviceId 属性前缀地址，反向代理用户微服务
zuul:
  routes:
    hmily:
      path: /custom-path/**
      serviceId: springms-provider-user # 注意这个名称是注册在eureka服务中的名称




# 测试二，自定义路径配置，给 springms-provider-user 微服务添加 path、serviceId 属性前缀地址，反向代理用户微服务
#zuul:
#  routes:
#    hmily:
#      path: /custom-path/**
#      serviceId: http://localhost:7900/ # 注意这个名称是 url 地址




# 测试三：自定义路径配置，给微服务添加 prefix 属性前缀地址，反向代理所有微服务
#zuul:
#  prefix: /api




# 测试四：自定义路径配置，给微服务添加 strip-prefix 属性前缀地址，反向代理所有微服务
#zuul:
#  prefix: /api
#  strip-prefix: false




# 测试五：自定义路径配置，针对测试四，再次修改 prefix 属性前缀地址，反向代理用户微服务
#zuul:
#  prefix: /simple
#  strip-prefix: false
#####################################################################################################



#####################################################################################################
# 打印日志
logging:
  level:
    root: INFO
    com.springms: DEBUG
    com.netflix: DEBUG
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




### 2.3 添加zuul服务网关微服务启动类（springms-gateway-zuul-attribute\src\main\java\com\springms\cloud\GatewayZuulAttributeApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Zuul 网关微服务的一些属性应用测试；
 *
 * API网关模块发现应用入口（自定义路径配置添加 serviceId 属性，给User微服务添加前缀地址，反向代理所有服务器）。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8155/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
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
public class GatewayZuulAttributeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayZuulAttributeApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulAttribute微服务 】】】】】】已启动.");
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、Zuul 网关微服务的一些属性应用测试（自定义路径配置，给 springms-provider-user 微服务添加 path、serviceId 属性前缀地址，反向代理用户微服务）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 # 测试一，自定义路径配置，给 springms-provider-user 微服务添加 path、serviceId 属性前缀地址，反向代理用户微服务
    zuul:
        routes:
            hmily:
                path: /custom-path/**
                serviceId: springms-provider-user # 注意这个名称是注册在eureka服务中的名称
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-consumer-movie 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul 模块服务；

 6、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 7、新起网页页签，输入 http://localhost:7901/movie/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第6、7步正常，说明 springms-provider-user、springms-consumer-movie 两个服务目前正常；

 8、新起网页页签，然后输入 http://localhost:8155/springms-provider-user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 9、新起网页页签，然后输入 http://localhost:8155/springms-consumer-movie/movie/4，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结二：第8、9步也能正常打印用户信息，说明 API 网关已经生效了，可以通过API服务器地址链接各个微服务的 http://localhost:8150/serviceId/path 这样的路径来访问了；

 10、新起网页页签，然后输入 http://localhost:8155/custom-path/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来，可见【用户微服务】的地址被改变生效了，同时被 API 网关反向代理了，也就是说 http 的请求 /custom-path 将被发送到【用户微服务】；
 11、新起网页页签，然后输入 http://localhost:8155/custom-path/movie/4，正常情况下访问不通，理应访问不通的；

 总结三：path、serviceId 属性仅仅只是为了给 springms-provider-user 微服务添加了 custom-path 前缀，所以电影微服务加 custom-path 前缀当然访问不通的；
 ****************************************************************************************/

/****************************************************************************************
 二、Zuul 网关微服务的一些属性应用测试（自定义路径配置，给 springms-provider-user 微服务添加 path、serviceId 属性前缀地址，反向代理用户微服务）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 # 测试二，自定义路径配置，给 springms-provider-user 微服务添加 path、serviceId 属性前缀地址，反向代理用户微服务
    zuul:
        routes:
            hmily:
                path: /custom-path/**
                serviceId: http://localhost:7900/ # 注意这个名称是 url 地址
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-consumer-movie 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul 模块服务；

 6、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 7、新起网页页签，输入 http://localhost:7901/movie/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第6、7步正常，说明 springms-provider-user、springms-consumer-movie 两个服务目前正常；

 8、新起网页页签，然后输入 http://localhost:8155/springms-provider-user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 9、新起网页页签，然后输入 http://localhost:8155/springms-consumer-movie/movie/4，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结二：第8、9步也能正常打印用户信息，说明 API 网关已经生效了，可以通过API服务器地址链接各个微服务的 http://localhost:8150/serviceId/path 这样的路径来访问了；

 10、新起网页页签，然后输入 http://localhost:8155/custom-path/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来，可见【用户微服务】的地址被改变生效了，同时被 API 网关反向代理了，也就是说 http 的请求 /custom-path 将被发送到【用户微服务】；
 11、新起网页页签，然后输入 http://localhost:8155/custom-path/movie/4，正常情况下访问不通，理应访问不通的；

 总结三：path、serviceId 属性仅仅只是为了给 springms-provider-user 微服务添加了 custom-path 前缀，所以电影微服务加 custom-path 前缀当然访问不通的；

 注意：测试一、测试二的区别在于 serviceId 的不同，一个是注入eureka中的服务名称，一个是url地址，但是两者效果都是一样的；
 ****************************************************************************************/

/****************************************************************************************
 三、Zuul 网关微服务的一些属性应用测试（自定义路径配置，给微服务添加 prefix 属性前缀地址，反向代理所有微服务）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 # 测试三，自定义路径配置，给微服务添加 prefix 属性前缀地址，反向代理所有微服务
    zuul:
        prefix: /api
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-consumer-movie 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul 模块服务；

 6、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 7、新起网页页签，输入 http://localhost:7901/movie/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第6、7步正常，说明 springms-provider-user、springms-consumer-movie 两个服务目前正常；

 8、新起网页页签，然后输入 http://localhost:8155/springms-provider-user/simple/3，正常情况下访问不通，理应访问不通的；
 9、新起网页页签，然后输入 http://localhost:8155/springms-consumer-movie/movie/4，正常情况下访问不通，理应访问不通的；

 总结二：第8、9步访问不通，说明 prefix 属性生效了，通过 http://localhost:8150/serviceId/path 这样的路径来访问已经行不通了；

 10、新起网页页签，然后输入 http://localhost:8155/api/simple/3，正常情况下访问不通，理应访问不通的；
 11、新起网页页签，然后输入 http://localhost:8155/api/movie/4，正常情况下访问不通，理应访问不通的；

 总结三：第10、11步访问不通，说明 prefix 属性生效了，通过 http://localhost:8150/api/path 这样的路径来访问也行不通了；

 12、新起网页页签，然后输入 http://localhost:8155/api/springms-provider-user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来，可见 prefix 属性添加前缀地址被改变生效了；
 13、新起网页页签，然后输入 http://localhost:8155/api/springms-consumer-movie/movie/4，正常情况下是能看到 ID != 0 一堆用户信息被打印出来，可见 prefix 属性添加前缀地址被改变生效了；

 总结四：zuul.prefix 属性给所有路径添加了一个前缀，即需要通过 http://localhost:8150/api/serviceId/path 这样的地址才可以访问成功；
 ****************************************************************************************/

/****************************************************************************************
 四、Zuul 网关微服务的一些属性应用测试（自定义路径配置，给微服务添加 strip-prefix 属性前缀地址，反向代理所有微服务）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 # 测试三，自定义路径配置，给微服务添加 prefix 属性前缀地址，反向代理所有微服务
    zuul:
        prefix: /api
        strip-prefix: false
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-consumer-movie 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul 模块服务；

 6、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 7、新起网页页签，输入 http://localhost:7901/movie/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第6、7步正常，说明 springms-provider-user、springms-consumer-movie 两个服务目前正常；

 8、新起网页页签，然后输入 http://localhost:8155/springms-provider-user/simple/3，正常情况下访问不通，理应访问不通的；
 9、新起网页页签，然后输入 http://localhost:8155/springms-consumer-movie/movie/4，正常情况下访问不通，理应访问不通的；

 总结二：第8、9步访问不通，说明 prefix 属性生效了，通过 http://localhost:8150/serviceId/path 这样的路径来访问已经行不通了；

 10、新起网页页签，然后输入 http://localhost:8155/api/simple/3，正常情况下访问不通，理应访问不通的；
 11、新起网页页签，然后输入 http://localhost:8155/api/movie/4，正常情况下访问不通，理应访问不通的；

 总结三：第10、11步访问不通，说明 prefix 属性生效了，通过 http://localhost:8150/api/path 这样的路径来访问也行不通了；

 12、新起网页页签，然后输入 http://localhost:8155/api/springms-provider-user/simple/3，正常情况下访问不通，理应访问不通的；
 13、新起网页页签，然后输入 http://localhost:8155/api/springms-consumer-movie/movie/4，正常情况下访问不通，理应访问不通的；
 14、那么问题来了，这种配置为什么访问不通呢？由于查看日志，发现这种请求的被打印出来的路径为：
    http://localhost:8155/api/springms-provider-user/simple/3
    springms-provider-user using LB returned Server: 192.168.3.101:7900 for request /api/simple/3

    http://localhost:8155/api/springms-consumer-movie/movie/4
    springms-consumer-movie using LB returned Server: 192.168.3.101:7901 for request /api/movie/4

 15、试想，要么去掉 /api 前缀，如果这样的话，那么就没有设置 prefix 的必要了，然后做了这样的测试如下：

 16、新起网页页签，然后输入 http://localhost:8155/api/springms-provider-user/3，正常情况下访问不通，理应访问不通的；
 17、新起网页页签，然后输入 http://localhost:8155/api/springms-consumer-movie/4，正常情况下访问不通，理应访问不通的；
 18、这种配置为什么访问不通呢？由于查看日志，发现这种请求的被打印出来的路径为：
    http://localhost:8155/api/springms-provider-user/3
    springms-provider-user using LB returned Server: 192.168.3.101:7900 for request /api/3

    http://localhost:8155/api/springms-consumer-movie/4
    springms-consumer-movie using LB returned Server: 192.168.3.101:7901 for request /api/4

 19、由步骤14、18的日志可以看出来，请求的地址非常有规律，那我们试想，如果将 api 的值改掉呢？是否可以成功呢？请看下面测试五！！！
 ****************************************************************************************/

/****************************************************************************************
 五、Zuul 网关微服务的一些属性应用测试（自定义路径配置，针对测试四，再次修改 prefix 属性前缀地址，反向代理用户微服务）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 # 测试四，自定义路径配置，针对测试四，再次修改 prefix 属性前缀地址，反向代理用户微服务
    zuul:
        prefix: /simple
        strip-prefix: false
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-consumer-movie 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul 模块服务；

 6、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 7、新起网页页签，输入 http://localhost:7901/movie/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第6、7步正常，说明 springms-provider-user、springms-consumer-movie 两个服务目前正常

 8、新起网页页签，然后输入 http://localhost:8155/simple/springms-provider-user/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来，可见 prefix 属性添加前缀地址被改变生效了；
 9、新起网页页签，然后输入 http://localhost:8155/simple/springms-consumer-movie/4，正常情况下访问不通，理应访问不通的；

 总结二：之所以 springms-consumer-movie 访问不通，因为修改 prefix 只是这个修改的值正好和 springms-provider-user 的接口前缀恰好一致而已；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

欢迎关注，您的肯定是对我最大的支持!!!
```






























