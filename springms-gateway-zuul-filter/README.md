# SpringCloud（第 021 篇）Zuul 的过滤器 ZuulFilter 的使用
-

## 一、大致介绍

``` 
1、我们在学 Spring 的时候，就有过滤器和拦截器的使用，而 Zuul 同样也有过滤器的使用，本章节我们指在如何简单使用 ZuulFilter。
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-gateway-zuul-filter</artifactId>
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


### 2.2 添加应用配置文件（springms-gateway-zuul-filter\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-gateway-zuul-filter
server:
  port: 8215
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
ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 60000
#####################################################################################################



```




### 2.3 添加zuul的过滤器类（springms-gateway-zuul-filter\src\main\java\com\springms\cloud\filter\PreZuulFilter.java）
``` 
package com.springms.cloud.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * zuul 的过滤器。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/26
 *
 */
public class PreZuulFilter extends ZuulFilter{

    private static final Logger Logger = LoggerFactory.getLogger(PreZuulFilter.class);

    /**
     * 前置过滤器。
     *
     * 但是在 zuul 中定义了四种不同生命周期的过滤器类型：
     *
     *      1、pre：可以在请求被路由之前调用；
     *
     *      2、route：在路由请求时候被调用；
     *
     *      3、post：在route和error过滤器之后被调用；
     *
     *      4、error：处理请求时发生错误时被调用；
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤的优先级，数字越大，优先级越低。
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 1;
    }

    /**
     * 是否执行该过滤器。
     *
     * true：说明需要过滤；
     *
     * false：说明不要过滤；
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return false;
    }

    /**
     * 过滤器的具体逻辑。
     *
     * @return
     */
    @Override
    public Object run() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String host = request.getRemoteHost();
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("                    请求的host:{}                          ", host);
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return null;
    }
}

```



### 2.4 添加zuul服务网关微服务启动类（springms-gateway-zuul-filter\src\main\java\com\springms\cloud\MsGatewayZuulFilterApplication.java）
``` 
package com.springms.cloud;

import com.springms.cloud.filter.PreZuulFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * Zuul 的过滤器 ZuulFilter 的使用。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8150/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
 *
 * 想看更多关于过滤器的使用的话，请移步源码路径：spring-cloud-netflix-core-1.2.7.RELEASE.jar中的org.springframework.cloud.netflix.zuul.filters目录下；
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
public class MsGatewayZuulFilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulFilterApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulFilter微服务 】】】】】】已启动.");
    }

    /**
     * 即使其它配置都写好的话，那么不添加这个 Bean 的方法的话，还是不会执行任何过滤的方法；
     *
     * @return
     */
    @Bean
    public PreZuulFilter preZuulFilter() {
        return new PreZuulFilter();
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、Zuul 的过滤器 ZuulFilter 的使用（正常情况测试）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 2、修改 PreZuulFilter 的 shouldFilter 方法返回 true 即可，表明要使用过滤功能；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 5、启动 springms-gateway-zuul-filter 模块服务；

 6、新起网页页签，输入 http://localhost:8215/routes 正常情况下是能看到zuul需要代理的各个服务列表；

 7、新起网页页签，然后输入 http://localhost:8215/springms-provider-user/simple/1 正常情况下是能看到 ID != 0 一堆用户信息被打印出来，并且该zuul的微服务日志控制台会打印一堆 PreZuulFilter 打印的日志内容；
 8、然后会看到 PreZuulFilter.run 方法中的日志被打印出来，说名确实进入了过滤的方法里面，过滤起作用了；

 总结：过滤确实起了作用，那是因为过滤器的配置中 shouldFilter 设置的 true，需要过滤，所以当然会过滤啦，直接 run 方法中的打印信息即可；
 ****************************************************************************************/





/****************************************************************************************
 二、Zuul 的过滤器 ZuulFilter 的使用（使用过滤器，但是使得过滤的run方法失效）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 2、修改 PreZuulFilter 的 shouldFilter 方法返回 false 即可，表明不需要使用过滤器；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 5、启动 springms-gateway-zuul-filter 模块服务；

 6、新起网页页签，输入 http://localhost:8215/routes 正常情况下是能看到zuul需要代理的各个服务列表；

 7、新起网页页签，然后输入 http://localhost:8215/springms-provider-user/simple/1 正常情况下是能看到 ID != 0 一堆用户信息被打印出来，但是该zuul的微服务日志控制台并不会打印一堆 PreZuulFilter 打印的日志内容；
 8、然后再看，PreZuulFilter.run 方法中的日志不见了，没有被打印出来，过滤的run方法失效了；


 总结：由此可见，PreZuulFilter 的 shouldFilter 设置为 false，过滤器就已经失去效果了；
 ****************************************************************************************/
```


## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!






























