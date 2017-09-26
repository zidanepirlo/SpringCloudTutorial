# SpringCloud（第 024 篇）简单文件上传微服务，并加入 zuul 微服务后用 zuul 微服务地址采取curl或者页面点击实现文件上传
-

## 一、大致介绍

``` 
1、本章节主要将文件上传微服务加入到 zuul 服务中去，然后利用 zuul 微服务的地址上传文件；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-gateway-zuul-file-upload</artifactId>
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


### 2.2 添加应用配置文件（springms-gateway-zuul-file-upload\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-gateway-zuul-file-upload
server:
  port: 8195
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
    com.netflix: debug
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
# 第一种方式：将 hystrix 的超时时间设置成 60000 毫秒，因为文件上传需要的超时时间稍微长一点点
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 60000
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






### 2.3 添加zuul服务启动类（springms-gateway-zuul-file-upload\src\main\java\com\springms\cloud\MsGatewayZuulFileUploadApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 简单文件上传微服务，并加入 zuul 微服务后用 zuul 微服务地址采取curl或者页面点击实现文件上传。
 *
 * 提供给 springms-file-upload 文件上传微服务用的。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8195/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
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
public class MsGatewayZuulFileUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulFileUploadApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulFileUpload微服务 】】】】】】已启动.");
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、简单文件上传微服务，并加入 zuul 微服务后用 zuul 微服务地址采取curl或者页面点击实现文件上传（加入 zuul 微服务，页面上传文件）：

 1、启动 springms-discovery-eureka 模块服务，启动1个端口；
 2、启动 springms-file-upload 模块服务；
 3、启动 springms-gateway-zuul-file-upload 模块服务；

 4、新起网页页签，输入 http://localhost:8190/index.html 正常情况下是能看到选择文件上传的界面；
 5、新起网页页签，输入 http://localhost:8195/springms-file-upload/index.html 正常情况下是能看到选择文件上传的界面；
 6、选择文件，然后点击 upload 上传文件，上传成功后，直接返回上传成功文件所在的磁盘全路径；
 ****************************************************************************************/

/****************************************************************************************
 二、简单文件上传微服务，并加入 zuul 微服务后用 zuul 微服务地址采取curl或者页面点击实现文件上传（加入 zuul 微服务，然后采用命令上传文件）：

 1、启动 springms-discovery-eureka 模块服务，启动1个端口；
 2、启动 springms-file-upload 模块服务；
 3、启动 springms-gateway-zuul-file-upload 模块服务；

 4、进入 curl.exe 所在的目录，尝试 curl.exe www.baidu.com 看看是否正常，正常情况下会打印百度首页的一堆信息；
 5、执行命令：curl.exe -F "file=@文件名称" localhost:8195/springms-file-upload/upload
 6、正常情况下，第6步骤执行后，直接返回上传成功文件所在的磁盘全路径；
 ****************************************************************************************/

/****************************************************************************************
 三、简单文件上传微服务，并加入 zuul 微服务后用 zuul 微服务地址采取curl或者页面点击实现文件上传文件上传微服务（加入 zuul 微服务，然后采用命令上传文件，传送大文件，文件大小大于 max-file-size 这个配置的属性值）：

 1、启动 springms-discovery-eureka 模块服务，启动1个端口；
 2、启动 springms-file-upload 模块服务；
 3、启动 springms-gateway-zuul-file-upload 模块服务；

 4、进入 curl.exe 所在的目录，尝试 curl.exe www.baidu.com 看看是否正常，正常情况下会打印百度首页的一堆信息；
 5、执行命令：curl.exe -F "file=@文件名称大于50M" localhost:8195/springms-file-upload/upload
 6、结果第6步骤执行后，报错提示文件太大，报错信息为：the request was rejected because its size (36611267) exceeds the configured maximum (10485760)

 7、然而在zuul的集群中，可以饶过SpringMvc的检测，执行命令:curl.exe -F "file=@文件名称大于50M" localhost:8195/zuul/springms-file-upload/upload
 8、结果第7步骤执行后，报错提示timeout超时，报错信息为：{"timestamp":1503932607944,"status":500,"error":"Internal Server Error","exception":"com.netflix.zuul.exception.ZuulException","message":"GENERAL"}；

 9、发现采用了各种办法好像还是没有解决，难道是最近版本 SpringCloud 解决了这样的一个饶过SpringMvc请求访问的bug了么？？？
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

SpringCloudTutorial交流QQ群: 235322432

欢迎关注，您的肯定是对我最大的支持!!!
```






























