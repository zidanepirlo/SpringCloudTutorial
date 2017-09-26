# SpringCloud（第 022 篇）Zuul 网关微服务的 regexmapper 属性测试, 类似测试 zuul 的自定义路径规则一样
-

## 一、大致介绍

``` 
1、本章节将 Zuul 的 regexmapper 属性单独拿出来，主要是这种配置规则，可以在一定程度上切分服务版本，根据版本信息请求服务；
2、在一些这样的场景中，后台每升级一个版本，需要不同环境测试，可以将 springms-provider-user-version 这样的名字命名为 springms-provider-user-test、springms-provider-user-prd 等名字，可以作为多套环境切换的一种方式而已，仅仅只是建议，不过这种多套环境的切换，后面会讲到用 config 来更简便配置。
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-gateway-zuul-reg-exp</artifactId>
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


### 2.2 添加应用配置文件（springms-gateway-zuul-reg-exp\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-gateway-zuul-reg-exp
server:
  port: 8185
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

```





### 2.3 添加zuul服务网关微服务启动类（springms-gateway-zuul-reg-exp\src\main\java\com\springms\cloud\MsGatewayZuulRegExpApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

/**
 * Zuul 网关微服务的 regexmapper 属性测试, 类似测试 zuul 的自定义路径规则一样。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8185/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/26
 *
 */
@SpringBootApplication
@EnableZuulProxy
public class MsGatewayZuulRegExpApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulRegExpApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulRegExp微服务 】】】】】】已启动.");
    }

    /**
     * 使用regexmapper提供serviceId和routes之间的绑定. 它使用正则表达式组来从serviceId提取变量, 然后注入到路由表达式中。
     *
     * 这个意思是说"springms-provider-user-version"将会匹配路由"/version/springms-provider-user/**". 任何正则表达式都可以, 但是所有组必须存在于servicePattern和routePattern之中.
     *
     * @return
     */
    @Bean
    public PatternServiceRouteMapper serviceRouteMapper() {
        return new PatternServiceRouteMapper("(?<name>^.+)-(?<version>v.+$)", "${version}/${name}");
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、Zuul 网关微服务的 regexmapper 属性测试, 类似测试 zuul 的自定义路径规则一样：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 2、在 MsGatewayZuulRegExpApplication 类中添加 serviceRouteMapper 方法；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user-version 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul-reg-exp 模块服务；

 6、新起网页页签，输入 http://localhost:8185/version/springms-provider-user/simple/4 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结：springms-provider-user-version 通过名字和版本被切割后，利用路径拼接规则，通过 http://localhost:8185/version/springms-provider-user/simple/4 也可以访问用户微服务；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

SpringCloudTutorial交流QQ群: 235322432

欢迎关注，您的肯定是对我最大的支持!!!
```






























