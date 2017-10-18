# SpringCloud（第 034 篇）配置服务端ConfigServer设置安全认证
-

## 一、大致介绍

``` 
1、前面提到的加密内容，虽然说对内容进行了加密，但是为了更安全的安全隔离，服务与服务之间也需要设置简单的安全认证；
2、那么在本章节我们讲解下如何配置服务端之间的简单认证，Springcloud 的强大之处在于对认证这块仅仅配置一下即可；
3、然后后续我们还会讲解 SpringCloud 的 auth2 等认证机制，后续有待继续讲解；

4、这里还顺便列举下配置路径的规则：
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

    <artifactId>springms-config-server-authc</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.springms.cloud</groupId>
        <artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <dependencies>
        <!-- 服务端配置模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>

        <!-- 服务端登录验证模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-config-server-authc\src\main\resources\application.yml）
``` 
#配置登录密码
security:
  basic:
    enabled: true
  user:
    name: admin
    password: admin
server:
  port: 8275

spring:
  application:
    name: springms-config-server-authc
  cloud:
    config:
      server:
        git:
          uri: https://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar

```




### 2.3 添加微服务启动类（springms-config-server-authc/src/main/java/com/springms/cloud/MsConfigServerAuthcApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 配置服务端ConfigServer设置安全认证。<br/>
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
@EnableConfigServer
public class MsConfigServerAuthcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigServerAuthcApplication.class, args);
        System.out.println("【【【【【【 ConfigServerAuthc微服务 】】】】】】已启动.");
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、配置服务服务端Server应用入口（正常测试）：

 1、注解：EnableConfigServer
 2、编辑 application.yml 文件，配置登录密码；
 3、启动 springms-config-server-authc 模块服务，启动1个端口；

 4、在浏览器输入地址 http://localhost:8275/abc-default.properties 正常情况下会输出配置文件的内容；
 5、在浏览器输入地址 http://localhost:8275/abc-default.yml 正常情况下会输出配置文件的内容；
 6、在浏览器输入地址 http://localhost:8275/abc-hehui.yml 正常情况下会输出配置文件的内容；
 7、在浏览器输入地址 http://localhost:8275/aaa-bbb.yml 正常情况下会输出配置文件的内容；
 8、在浏览器输入地址 http://localhost:8275/aaa-bbb.properties 正常情况下会输出配置文件的内容；

 9、在浏览器输入地址 http://localhost:8275/master/abc-default.properties 正常情况下会输出配置文件的内容；
 10、在浏览器输入地址 http://localhost:8275/master/abc-default.yml 正常情况下会输出配置文件的内容；
 11、在浏览器输入地址 http://localhost:8275/master/abc-hehui.yml 正常情况下会输出配置文件的内容；
 12、在浏览器输入地址 http://localhost:8275/master/aaa-bbb.yml 正常情况下会输出配置文件的内容；
 13、在浏览器输入地址 http://localhost:8275/master/aaa-bbb.properties 正常情况下会输出配置文件的内容；
 14、在浏览器输入地址 http://localhost:8275/springms-config-server-dev.yml 正常情况下会输出配置文件的内容；

 总结：按照配置服务的路径规则配置，基本上都可以访问得到结果数据。
 ****************************************************************************************/
```


## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!






























