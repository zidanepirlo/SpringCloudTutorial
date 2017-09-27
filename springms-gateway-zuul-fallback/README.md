# SpringCloud（第 025 篇）Zuul 路由后面的微服务挂了后，Zuul 提供了一种回退机制来应对熔断处理
-

## 一、大致介绍

``` 
1、在一些不稳定因素导致路由后面的微服务宕机或者无响应时，zuul 就会累计大量的请求，久而久之基本上所有的请求都会超时，但是请求链接数却不断的在增加，不断的占用资源池不能结束知道超时消耗殆尽导致zuul微服务死机，整体挂机消亡；
2、而 zuul 在这种情况下，提供一种很好的回退机制，针对大量请求时提供了友好的熔断机制，确保在路由微服务修复前，尽量将过多的请求快速响应返回，减轻zuul的压力；
3、在本章节，我们对上面发生的这种普遍现象做了一种简单的回退处理，有效降低微服务的压力，还可以友好的提示给前端用户，或者调用方；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-gateway-zuul-fallback</artifactId>
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


### 2.2 添加应用配置文件（springms-gateway-zuul-fallback\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-gateway-zuul-fallback
server:
  port: 8200
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
zuul:
  ignoredServices: springms-consumer-movie-ribbon-with-hystrix
  routes:
    springms-provider-user: /user/**
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




### 2.3 添加zuul回退处理类（springms-gateway-zuul-fallback\src\main\java\com\springms\cloud\fallback\CustomZuulFallbackHandler.java）
``` 
package com.springms.cloud.fallback;


import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义Zuul回退机制处理器。
 *
 * Provides fallback when a failure occurs on a route 英文意思就是说提供一个回退机制当路由后面的服务发生故障时。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/27
 *
 */
@Component
public class CustomZuulFallbackHandler implements ZuulFallbackProvider {

    /**
     * 返回值表示需要针对此微服务做回退处理（该名称一定要是注册进入 eureka 微服务中的那个 serviceId 名称）；
     *
     * @return
     */
    @Override
    public String getRoute() {
        return "springms-provider-user";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.BAD_REQUEST.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.BAD_REQUEST.getReasonPhrase();
            }

            @Override
            public void close() {
            }

            /**
             * 当 springms-provider-user 微服务出现宕机后，客户端再请求时候就会返回 fallback 等字样的字符串提示；
             *
             * 但对于复杂一点的微服务，我们这里就得好好琢磨该怎么友好提示给用户了；
             *
             * @return
             * @throws IOException
             */
            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream((getRoute() + " fallback").getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
```



### 2.4 添加zuul服务网关微服务启动类（springms-gateway-zuul-fallback\src\main\java\com\springms\cloud\MsGatewayZuulFallbackApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Zuul 路由后面的微服务挂了后，Zuul 提供了一种回退机制来应对熔断处理。
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
 * @date 2017/9/27
 *
 */
@SpringBootApplication
@EnableZuulProxy
public class MsGatewayZuulFallbackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulFallbackApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulFallback微服务 】】】】】】已启动.");
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、Zuul 路由后面的微服务挂了后，Zuul 提供了一种回退机制来应对熔断处理：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-gateway-zuul-fallback 模块服务，启动1个端口；

 5、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 6、新起网页页签，然后输入 http://localhost:8200/springms-provider-user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 7、这个时候，停止 springms-provider-user 模块服务；
 8、刷新 http://localhost:8200/springms-provider-user/simple/3 网页，正常情况下会提示 “fallback” 字样的字符串；

 ...... 等待大约两分钟左右 ......(微服务宕机默认好像是90秒再连不上eureka服务的话，就会被eureka服务剔除掉)

 9、待用户微服务被踢出后，刷新 http://localhost:8200/springms-provider-user/simple/3 网页，正当情况下会提示 404 错误页面，因为用户微服务由于宕机超过大约90秒后会自动被 eureka 服务器剔除掉，所以访问网页必然找不到服务路径；

 总结：首先 Zuul 作为路由转发微服务，其也提供了一种熔断机制，避免大量请求阻塞在路由分发处；
      其次当注册进入 eureka 服务治理发现框架后，一定时间后还没有连上eureka时，这个时候eureka就会将这个宕机的微服务移除服务治理框架；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

SpringCloudTutorial交流QQ群: 235322432

欢迎关注，您的肯定是对我最大的支持!!!
```






























