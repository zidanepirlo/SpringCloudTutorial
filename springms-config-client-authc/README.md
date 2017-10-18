# SpringCloud（第 035 篇）配置服务客户端ConfigClient链接经过认证的配置服务端
-

## 一、大致介绍

``` 
1、前面一章节讲解了服务端配置安全认证，那么本章节就讲解如何链接上服务端的认证；

2、这里还顺便列举下配置路径的规则：
/****************************************************************************************
 * 配置服务的路劲规则：
 *
 * /{application}/{profile}[/{label}]
 * /{application}-{profile}.yml
 * /{label}/{application}-{profile}.yml
 * /{application}-{profile}.properties
 * /{label}/{application}-{profile}.properties
 ****************************************************************************************/
```


## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-config-client-authc</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.springms.cloud</groupId>
        <artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <dependencies>
        <!-- 客户端配置模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>

        <!-- web模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-config-client-authc\src\main\resources\application.yml）
``` 
server:
  port: 8280
  
```





### 2.3 添加 bootstrap.yml 应用配置文件（springms-config-client-authc\src\main\resources\bootstrap.yml）
``` 
#####################################################################################################
# 测试二：配置服务客户端Client应用入口（链接 ClientServer 测试，username、password 属性字段的优先级高于 uri 的优先级）
spring:
  cloud:
    config:
      uri: http://localhost:8275  # 链接 springms-config-server-authc 微服务
      username: admin
      password: admin
      profile: dev  # 选择 dev 配置文件
      label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

  application:
    name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
#####################################################################################################
```



### 2.4 添加Web控制层类（springms-config-client-authc/src/main/java/com/springms/cloud/controller/ConfigClientAuthcController.java）
``` 
package com.springms.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置客户端Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@RestController
public class ConfigClientAuthcController {

    @Value("${profile}")
    private String profile;

    @GetMapping("/profile")
    public String getProfile(){
        return this.profile;
    }
}

```


### 2.4 添加应用启动类（springms-config-client-authc/src/main/java/com/springms/cloud/MsConfigClientAuthcApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 配置服务客户端ConfigClient链接经过认证的配置服务端。<br/>
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
public class MsConfigClientAuthcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigClientAuthcApplication.class, args);
        System.out.println("【【【【【【 ConfigClientAuthc微服务 】】】】】】已启动.");
    }
}

```

## 三、测试

``` 
/****************************************************************************************
 一、配置服务客户端Client应用入口（链接经过认证的配置服务端）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 bootstrap.yml 文件，注意注释 profile 属性，然后添加相关客户端配置；
        spring:
            cloud:
                config:
                    uri: http://admin:admin@localhost:8275  # 链接 springms-config-server-authc 微服务
                    profile: dev  # 选择 dev 配置文件
                    label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

            application:
                name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
 3、启动 springms-config-server-authc 模块服务，启动1个端口；
 4、启动 springms-config-client-authc 模块服务，启动1个端口；

 5、在浏览器输入地址 http://localhost:8280/profile 正常情况下会输出配置文件的内容（内容为：foobar-dev）；

 总结：正常打印，说明配置服务客户端已经通过帐号、密码登录了远程的配置服务端成功了；
 ****************************************************************************************/

/****************************************************************************************
 二、配置服务客户端Client应用入口（链接经过认证的配置服务端, username、password 属性字段的优先级高于 uri 的优先级）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 bootstrap.yml 文件，注意注释 profile 属性，然后添加相关客户端配置；
        spring:
            cloud:
                config:
                    uri: http://localhost:8275  # 链接 springms-config-server-authc 微服务
                    username: admin
                    password: admin
                    profile: dev  # 选择 dev 配置文件
                    label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

            application:
                name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
 3、启动 springms-config-server-authc 模块服务，启动1个端口；
 4、启动 springms-config-client-authc 模块服务，启动1个端口；

 5、在浏览器输入地址 http://localhost:8280/profile 正常情况下会输出配置文件的内容（内容为：foobar-dev）；

 总结：正常打印，说明配置服务客户端已经通过帐号、密码登录了远程的配置服务端成功了；
 ****************************************************************************************/
```


## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























