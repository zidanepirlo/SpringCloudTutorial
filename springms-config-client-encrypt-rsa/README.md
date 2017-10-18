# SpringCloud（第 033 篇）配置客户端ConfigClient链接经过对称加解密的配置微服务
-

## 一、大致介绍

``` 
1、在（第 031 篇）讲解了如何链接对称加密的配置服务端，而链接对称非对称加密的配置微服务也是同样的；
2、配置客户端不需要做什么加解密的配置，加解密的配置在服务端做就好了；

3、这里还顺便列举下配置路径的规则：
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

    <artifactId>springms-config-client-encrypt-rsa</artifactId>
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


### 2.2 添加应用配置文件（springms-config-client-encrypt-rsa/src/main/resources/application.yml）
``` 
server:
  port: 8270


```





### 2.3 添加 bootstrap.yml 应用配置文件（springms-config-client-encrypt-rsa/src/main/resources/bootstrap.yml）
``` 
#####################################################################################################
# 配置服务客户端Client应用入口（链接 ClientServer 测试）
spring:
  cloud:
    config:
      uri: http://localhost:8265  # 链接 springms-config-client-encrypt-rsa 微服务
      profile: stg1rsa  # 选择 stg1rsa 配置文件
      label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

  application:
    name: foobar  #取 foobar-stg1rsa.yml 这个文件的 application 名字，即为 foobar 名称
#####################################################################################################

```


### 2.4 添加Web控制层类(springms-config-client-encrypt-rsa/src/main/java/com/springms/cloud/controller/ConfigClientEncryptRsaController.java）
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
public class ConfigClientEncryptRsaController {

    @Value("${profile}")
    private String profile;

    @GetMapping("/profile")
    public String getProfile(){
        return this.profile;
    }
}

```


### 2.5 添加应用启动类（springms-config-client-encrypt-rsa/src/main/java/com/springms/cloud/MsConfigClientEncryptRsaApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 配置客户端ConfigClient链接经过RSA加解密的配置微服务。
 *
 * 配置服务客户端Client应用入口（链接经过 RSA 非对称加解密的配置微服务）（专门为测试经过 RSA 非加解密的配置微服务 springms-config-server-encrypt-rsa 微服务模块）。<br/>
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
public class MsConfigClientEncryptRsaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigClientEncryptRsaApplication.class, args);
        System.out.println("【【【【【【 ConfigClientEncryptRsa微服务 】】】】】】已启动.");
    }
}
```

## 三、测试

``` 
/****************************************************************************************
 一、配置服务客户端Client应用入口（链接经过 RSA 非加解密的配置微服务）（专门为测试经过 RSA 非加解密的配置微服务 springms-config-server-encrypt-rsa 微服务模块）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 bootstrap.yml 文件，注意注释 profile 属性，然后添加相关客户端配置；
     spring:
         cloud:
             config:
                 uri: http://localhost:8265  # 链接 springms-config-client-encrypt-rsa 微服务
                 profile: stg1rsa  # 选择stg1rsa配置文件
                 label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master
        
         application:
            name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
 3、启动 springms-config-server-encrypt-rsa 模块服务，启动1个端口；
 4、启动 springms-config-client-encrypt-rsa 模块服务，启动1个端口；

 5、在浏览器输入地址 http://localhost:8270/profile 正常情况下会输出配置文件的内容（内容为：foobar-stg2rsa）；

 总结：正常打印，说明配置服务客户端不需要做什么加解密的配置，加解密的配置在服务端做就好了；
 ****************************************************************************************/
```


## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!




























