# SpringCloud（第 051 篇）EurekaServer集群高可用注册中心以及简单的安全认证
-

## 一、大致介绍

``` 

1、前面章节分析了一下 Eureka 的源码，我们是不是在里面注意到了 Peer 节点的复制，为什么要复制节点同步信息呢，其实就是为了同一个集群之间的EurekaServer一致性方案的一个实现；
2、于是我们在本章节就真正的来通过代码来实现一下EurekaServer之间的高可用注册中心。
3、至于所谓的安全认证，就是在我们的客户端配置defaultZone属性环节，要带上用户名密码才可以注册到高可用注册中心去；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-discovery-eureka-ha</artifactId>
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


### 2.2 添加应用默认配置文件（springms-discovery-eureka-ha\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-discovery-eureka-ha
  profiles:
    # 主要激活哪个配置文件，如果有不清楚的话，可以回头看看 Config 配置相关的知识，比如 springms-config-client、springms-config-server
    active: peer1
```


### 2.3 添加应用peer1节点默认配置文件（springms-discovery-eureka-ha\src\main\resources\application-peer1.yml）
``` 
# 配置 eureka 登录密码，输入地址 http://localhost:8761 时候就需要输入这个用户名密码登录进去
security:
  basic:
    enabled: true
  user:
    name: admin
    password: admin

server:
  port: 8401
 
eureka:
  datacenter: SpringCloud   # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Data center 显示信息
  environment: Test         # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Environment 显示信息
  instance:
    hostname: peer1
    instance-id: ${spring.application.name}:${server.port}
  client:
#    register-with-eureka: false
#    fetch-registry: false
    service-url:
      defaultZone: http://admin:admin@peer2:8402/eureka,http://admin:admin@peer3:8403/eureka

```


### 2.4 添加应用peer2节点默认配置文件（springms-discovery-eureka-ha\src\main\resources\application-peer2.yml）
``` 
# 配置 eureka 登录密码，输入地址 http://localhost:8761 时候就需要输入这个用户名密码登录进去
security:
  basic:
    enabled: true
  user:
    name: admin
    password: admin

server:
  port: 8402
 
eureka:
  datacenter: SpringCloud   # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Data center 显示信息
  environment: Test         # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Environment 显示信息
  instance:
    hostname: peer2
    instance-id: ${spring.application.name}:${server.port}
  client:
#    register-with-eureka: false
#    fetch-registry: false
    service-url:
      defaultZone: http://admin:admin@peer1:8401/eureka,http://admin:admin@peer3:8403/eureka
```


### 2.5 添加应用peer3节点默认配置文件（springms-discovery-eureka-ha\src\main\resources\application-peer3.yml）
``` 
# 配置 eureka 登录密码，输入地址 http://localhost:8761 时候就需要输入这个用户名密码登录进去
security:
  basic:
    enabled: true
  user:
    name: admin
    password: admin

server:
  port: 8403
 
eureka:
  datacenter: SpringCloud   # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Data center 显示信息
  environment: Test         # 修改 http://localhost:8761 地址 Eureka 首页上面 System Status 的 Environment 显示信息
  instance:
    hostname: peer3
    instance-id: ${spring.application.name}:${server.port}
  client:
#    register-with-eureka: false
#    fetch-registry: false
    service-url:
      defaultZone: http://admin:admin@peer1:8401/eureka,http://admin:admin@peer2:8402/eureka
```



### 2.6 添加 EurekaServer 微服务启动类（springms-discovery-eureka-ha\src\main\java\com\springms\cloud\EurekaServerHaApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 服务发现服务端EurekaServer微服务高可用。<br/>
 *
 * Eureka默认端口是8761
 * http://localhost:8761/eureka/apps 可以查看注册到该服务器上的一堆微服务实例的信息。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/25
 *
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerHaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerHaApplication.class, args);
        System.out.println("【【【【【【 EurekaHa微服务 】】】】】】已启动.");
    }
}

```


## 三、测试

``` 
测试之前，我们得配置一下Run Configuration，如何快速启动运行三个EurekaServer微服务；
在 Run/Debug Configuration(新建一个Spring Boot) -> Spring Boot -> EurekaServerHaApplication_8401_peer1(给界面顶部 Name 字段属性命名) -> Configuration -> Spring Boot Settings -> Active Profiles: Peer1
在 Run/Debug Configuration(新建一个Spring Boot) -> Spring Boot -> EurekaServerHaApplication_8402_peer2(给界面顶部 Name 字段属性命名) -> Configuration -> Spring Boot Settings -> Active Profiles: Peer2
在 Run/Debug Configuration(新建一个Spring Boot) -> Spring Boot -> EurekaServerHaApplication_8403_peer3(给界面顶部 Name 字段属性命名) -> Configuration -> Spring Boot Settings -> Active Profiles: Peer3

/****************************************************************************************
 测试一：EurekaServer集群高可用注册中心以及简单的安全认证（正常测试）：

 1、注解：EnableEurekaClient
 2、按照上面依次运行启动 EurekaServerHaApplication_8401_peer1、EurekaServerHaApplication_8402_peer2、EurekaServerHaApplication_8403_peer3 模块服务，启动3个端口；
 3、在浏览器输入地址 http://localhost:8401 并输入用户名密码 admin/admin 进入 8401 端口这台服务正常启动；
 4、在浏览器输入地址 http://localhost:8402 并输入用户名密码 admin/admin 进入 8402 端口这台服务正常启动；
 5、在浏览器输入地址 http://localhost:8403 并输入用户名密码 admin/admin 进入 8403 端口这台服务正常启动；
 6、注意一下，当前已经注册的实例信息里面，都有三台微服务，说明小小的高可用集群已经呈现在大家的眼前了；
 ****************************************************************************************/
 
/****************************************************************************************
 测试二：用户微服务接口测试（采用 springms-provider-user 给 springms-discovery-eureka-ha 模块做测试，测试EurekaClient客户端注册进EurekaServer高可用集群中）：

 1、注解：EnableEurekaClient
 2、修改 defaultZone 的接入地址值如下：
 ###################################################################################
 # 测试二：测试EurekaClient客户端注册进EurekaServer高可用集群中
 defaultZone: http://admin:admin@peer1:8401/eureka,,http://admin:admin@peer2:8402/eureka,,http://admin:admin@peer3:8403/eureka
 ###################################################################################
 3、启动 springms-discovery-eureka-ha 模块服务，启动3个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、在浏览器输入地址http://localhost:7900/simple/1 可以看到信息成功的被打印出来，说明用户微服务正常；

 6、在浏览器输入地址 http://localhost:8401 并输入用户名密码 admin/admin 进入Eureka微服务显示在网页中，说明用户微服务确实注册到了 eureka 服务中；
 7、在浏览器输入地址 http://localhost:8401/eureka/apps/springms-provider-user 可以看到自定义的 <metadata>信息以及用户微服务的相关信息成功的被展示出来了；
 8、在浏览器输入地址 http://localhost:8402/eureka/apps/springms-provider-user 可以看到自定义的 <metadata>信息以及用户微服务的相关信息成功的被展示出来了；
 9、在浏览器输入地址 http://localhost:8403/eureka/apps/springms-provider-user 可以看到自定义的 <metadata>信息以及用户微服务的相关信息成功的被展示出来了；
 
 注意：这里我们要回到 springms-provider-user 项目代码中稍微修改，也写了一个对应的《测试二》测试步骤，即可实现我们这个高可用的《测试二》样例，；
 ****************************************************************************************/
```




## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!



























