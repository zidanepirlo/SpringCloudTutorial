# SpringCloud（第 031 篇）配置客户端ConfigClient链接经过对称加解密的配置微服务
-

## 一、大致介绍

``` 
1、Git服务端的文件内容进行了加密处理，那么是不是配置客户端拿到内容之后需要解密呢？
2、答案显然不是的，因为这样解密的话，先不说实现起来的难易程度，单从表面上来讲，若是加解密频繁换的话，那客户端是不是每次都得升级解密算法呢？
3、而 SpringCloud 配置客户端不需要做什么加解密的配置，加解密的配置在服务端做就好了；

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

    <artifactId>springms-config-client-encrypt</artifactId>
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


### 2.2 添加应用配置文件（springms-config-client-encrypt/src/main/resources/application.yml）
``` 
server:
  port: 8260


```





### 2.3 添加 bootstrap.yml 应用配置文件（springms-config-client-encrypt/src/main/resources/bootstrap.yml）
``` 
#####################################################################################################
# 配置服务客户端Client应用入口（链接 ClientServer 测试）
spring:
  cloud:
    config:
      uri: http://localhost:8255  # 链接 springms-config-server-encrypt 微服务
      profile: prd  # 选择生产配置文件
      label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

  application:
    name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
#####################################################################################################

```


### 2.4 添加Web控制层类(springms-config-client-encrypt/src/main/java/com/springms/cloud/controller/ConfigClientEncryptController.java）
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
public class ConfigClientEncryptController {

    @Value("${profile}")
    private String profile;

    @GetMapping("/profile")
    public String getProfile(){
        return this.profile;
    }
}

```


### 2.5 添加应用启动类（springms-config-client-encrypt/src/main/java/com/springms/cloud/MsConfigClientEncryptApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 配置客户端ConfigClient链接经过对称加解密的配置微服务;<br/>
 *
 * （专门为测试经过对称加解密的配置微服务 springms-config-server-encrypt 微服务模块）。<br/>
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
public class MsConfigClientEncryptApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigClientEncryptApplication.class, args);
        System.out.println("【【【【【【 ConfigClientEncrypt微服务 】】】】】】已启动.");
    }
}
```

## 三、测试

``` 
/****************************************************************************************
 一、配置服务客户端Client应用入口（链接经过对称加解密的配置微服务）（专门为测试经过对称加解密的配置微服务 springms-config-server-encrypt 微服务模块）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 bootstrap.yml 文件，注意注释 profile 属性，然后添加相关客户端配置；
     spring:
         cloud:
             config:
                 uri: http://localhost:8255  # 链接 springms-config-server-encrypt 微服务
                 profile: prd  # 选择生产配置文件
                 label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master
    
         application:
            name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
 3、启动 springms-config-server-encrypt 模块服务，启动1个端口；
 4、启动 springms-config-client-encrypt 模块服务，启动1个端口；

 5、在浏览器输入地址 http://localhost:8260/profile 正常情况下会输出配置文件的内容（内容为：foobar-prd）；

 总结：正常打印，说明配置服务客户端不需要做什么加解密的配置，加解密的配置在服务端做就好了；
 ****************************************************************************************/
```


## 四、下载地址

<font color=#4183C4 size=4>[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)</font>

<font color=#4183C4 size=4>SpringCloudTutorial交流QQ群: 235322432</font>、<font color=#4183C4 size=4>[微信沟通交流群](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)</font>

<font color=red size=4>欢迎关注，您的肯定是对我最大的支持!!!</font>





























