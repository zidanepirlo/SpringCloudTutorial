# SpringCloud（第 036 篇）单点手动动态刷新ConfigClient配置
-

## 一、大致介绍

``` 
1、当ConfigServer启动后，假如我们新增配置内容的话，是不是要重新启动一下ConfigServer呢？
2、答案肯定是不需要重新启动的，因为 SpringCloud 给我们提供了一个刷新的触发机制，这样便可以在不重新的情况下重新加载最新配置文件内容；

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

    <artifactId>springms-config-client-refresh</artifactId>
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

        <!-- 监控和管理生产环境的模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-config-client-refresh/src/main/resources/application.yml）
``` 
server:
  port: 8295


#####################################################################################################
# 配置服务客户端Client应用入口（正常测试 ConfigClient ）
# profile: profile-dev
#####################################################################################################





#####################################################################################################
# 配置服务客户端Client应用入口（链接 ClientServer 测试，同时本地也有一份配置文件，那么该如何抉择呢？）
# profile: profile-local-dev
#####################################################################################################
```





### 2.3 添加 bootstrap.yml 应用配置文件（springms-config-client-refresh/src/main/resources/bootstrap.yml）
``` 

#####################################################################################################
# 配置服务客户端Client应用入口（链接 ClientServer 测试）
spring:
  cloud:
    config:
      uri: http://localhost:8220
      profile: refresh
      label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

  application:
    name: foobar  #取 foobar-refresh.yml 这个文件的 application 名字，即为 foobar 名称
#####################################################################################################

```


### 2.4 添加Web控制层类(springms-config-client-refresh/src/main/java/com/springms/cloud/controller/ConfigClientRefreshController.java）
``` 
package com.springms.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RefreshScope
public class ConfigClientRefreshController {

    @Value("${profile}")
    private String profile;

    @GetMapping("/profile")
    public String getProfile(){
        return this.profile;
    }
}

```


### 2.5 添加应用启动类（springms-config-client-refresh/src/main/java/com/springms/cloud/MsConfigClientRefreshApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 单点手动动态刷新ConfigClient配置。<br/>
 *
 * ConfigClient 配置客户端服务想要实现自动刷新配置的话，ConfigServer 一端是不要做任何处理，只需要在 ConfigClient 一端处理即可。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
public class MsConfigClientRefreshApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigClientRefreshApplication.class, args);
        System.out.println("【【【【【【 ConfigClientRefresh微服务 】】】】】】已启动.");
    }
}
```

## 三、测试

``` 
/****************************************************************************************
 application.yml 涉及到的链接文件内容展示如下：

 修改内容前：
 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/foobar-refresh.yml
 profile: profile-refresh

 修改内容后：
 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/foobar-refresh.yml
 profile: profile-refresh-refresh
 ****************************************************************************************/

/****************************************************************************************
 一、配置刷新服务客户端Client应用入口（单点手动动态刷新配置服务客户端配置）：

 1、添加注解 RefreshScope，然后添加引用模块 spring-boot-starter-actuator 监控和管理生产环境的模块；
 2、编辑 application.yml 文件，添加相关客户端配置；
     spring:
         cloud:
             config:
                 uri: http://localhost:8220
                 profile: refresh
                 label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master
    
         application:
            name: foobar  #取 foobar-refresh.yml 这个文件的 application 名字，即为 foobar 名称
 3、启动 springms-config-server 模块服务，启动1个端口；
 4、启动 springms-config-client-refresh 模块服务，启动1个端口；
 5、在浏览器输入地址 http://localhost:8295/profile 正常情况下会输出远端服务的配置内容（内容为：profile: profile-refresh）；

 6、修改 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/foobar-refresh.yml 内容，修改后为 profile: profile-refresh-refresh；
 7、打开windows命令窗口，执行命令： >curl.exe -X POST http://localhost:8295/refresh
 8、然后刷新 http://localhost:8295/profile 网页，正常情况下会输出远端服务的配置内容（内容为：profile: profile-refresh-refresh）；

 总结：这里通过执行刷新命令才得以将远端配置内容刷新到配置服务客户端。
 ****************************************************************************************/
```


## 四、下载地址

<font color=#4183C4 size=4>[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)</font>

<font color=#4183C4 size=4>SpringCloudTutorial交流QQ群: 235322432</font>、<font color=#4183C4 size=4>[微信沟通交流群](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)</font>

<font color=red size=4>欢迎关注，您的肯定是对我最大的支持!!!</font>





























