# SpringCloud（第 029 篇）配置客户端 ConfigClient 接入配置服务端
-

## 一、大致介绍

``` 
1、有配置服务端，那么势必就会有与之对应的客户端，SpringCloud 文档中集成也非常简单；
2、但是这里有点需要注意，就是 bootstrap 配置文件，官方建议我们在bootstrap中放置不更改的属性，我们同样也需要在这里做一些简单不易于改变的配置；

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

    <artifactId>springms-config-client</artifactId>
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


### 2.2 添加应用配置文件（springms-config-client\src\main\resources\application.yml）
``` 
server:
  port: 8225


#####################################################################################################
# 测试一：配置服务客户端Client应用入口（正常测试 ConfigClient ）
profile: profile-dev(local)
#####################################################################################################




#####################################################################################################
# 测试二：配置服务客户端Client应用入口（链接 ClientServer 测试）
#spring:
#  cloud:
#    config:
#      uri: http://localhost:8220
#      profile: dev
#      label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master
#
#  application:
#    name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
#####################################################################################################





#####################################################################################################
# 测试四：配置服务客户端Client应用入口（链接 ClientServer 测试，同时本地也有一份配置文件，那么该如何抉择呢？）
#profile: profile-local-dev
#####################################################################################################

```





### 2.3 添加 bootstrap.yml 应用配置文件（springms-config-client\src\main\resources\bootstrap.yml）
``` 

#####################################################################################################
# 测试三：配置服务客户端Client应用入口（链接 ClientServer 测试）
#spring:
#  cloud:
#    config:
#      uri: http://localhost:8220
#      profile: dev
#      label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master
#
#  application:
#    name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
#####################################################################################################

```


### 2.4 添加Web控制层类（springms-config-client\src\main\java\com\springms\cloud\controller\ConfigClientController.java）
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
 * @date 2017/10/15
 *
 */
@RestController
public class ConfigClientController {

    @Value("${profile}")
    private String profile;

    @GetMapping("/profile")
    public String getProfile(){
        return this.profile;
    }
}

```


### 2.4 添加应用启动类（springms-config-client\src\main\java\com\springms\cloud\MsConfigClientApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 配置客户端ConfigClient接入配置服务端。<br/>
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/15
 *
 */
@SpringBootApplication
public class MsConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigClientApplication.class, args);
        System.out.println("【【【【【【 ConfigClient微服务 】】】】】】已启动.");
    }
}
```

## 三、测试

``` 
/****************************************************************************************
 一、配置客户端ConfigClient接入配置服务端（正常测试 ConfigClient ）：

 1、注解：pom.xml 先删除 configclient 的引用模块，以便测试正常情况 ConfigClientController 接口是否畅通；
 2、编辑 application.yml 文件，注意添加 profile: profile-dev(local) 属性；
 3、启动 springms-config-client 模块服务，启动1个端口；

 4、在浏览器输入地址 http://localhost:8225/profile 正常情况下会输出配置文件的内容（内容为：profile-dev(local)）；

 注意：这里还暂时不需要 bootstrap.yml 配置文件，所以测试一是不需要添加 bootstrap.yml 文件的；
 ****************************************************************************************/





/****************************************************************************************
 二、配置客户端ConfigClient接入配置服务端（链接 ClientServer 测试遇到挫折）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 application.yml 文件，注意注释 profile 属性，然后添加相关客户端配置；
    spring:
        cloud:
            config:
                uri: http://localhost:8220
                profile: dev
                label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

        application:
            name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
 3、启动 springms-config-server 模块服务，启动1个端口；
 4、启动 springms-config-client 模块服务，启动1个端口；

 5、然后发现启动 springms-config-client 模块出现错误，报错信息为：Fetching config from server at: http://localhost:8888, Could not locate PropertySource: I/O error on GET request for "http://localhost:8888/foobar/dev/master": Connection refused；
 6、发现错误信息中，为什么链接的是远端的 8888 端口呢？百思不得其解，难道是默认加载的配置 8888 端口？？？
 7、SpringCloud里面有个“启动上下文”，主要是用于加载远端的配置，也就是加载ConfigServer里面的配置，默认加载顺序为：加载bootstrap.*里面的配置 --> 链接configserver，加载远程配置 --> 加载application.*里面的配置；

 总结：这里需要借助于“启动上下文”来处理加载远程配置，请看下面环节测试三。
 ****************************************************************************************/

/****************************************************************************************
 三、配置客户端ConfigClient接入配置服务端（链接 ClientServer 测试遇到挫折）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 application.yml 文件，注释'测试二'的属性配置；
 3、新建一个 bootstrap.yml 文件，将相关客户端配置挪到 bootstrap.yml 文件即可；
 4、启动 springms-config-server 模块服务，启动1个端口；
 5、启动 springms-config-client 模块服务，启动1个端口；
 6、在浏览器输入地址 http://localhost:8225/profile 正常情况下会输出配置文件的内容（内容为：profile-dev）；

 总结：这里成功获取了远端配置，并成功打印了属性值出来，说明添加 bootstrap.yml 配置文件对我们项目的顺利进行起到了有效的作用。
 ****************************************************************************************/

/****************************************************************************************
 四、配置客户端ConfigClient接入配置服务端（链接 ClientServer 测试，同时本地也有一份配置文件，那么该如何抉择呢？）：

 1、在测试三的基础上，咱们再做点其它配置测试；
 2、在 application.yml 文件，再次添加 profile 属性，看看加载的是本地配置还是远端配置？
 3、停止并重新启动 springms-config-client 模块服务，启动1个端口；
 4、在浏览器输入地址 http://localhost:8225/profile 正常情况下会输出远端服务的配置内容；

 总结：在ConfigServer服务启动的时候，bootstrap 拿到远端配置注入到 profile 的属性中的话，那么就不会再次覆盖这个属性了，所以只会选择远端配置的内容。
      那是不是会有人认为把ConfigServer再重启一下就行了呢？答案是不行的，因为首选的是远端配置内容；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

SpringCloudTutorial交流QQ群: 235322432

欢迎关注，您的肯定是对我最大的支持!!!
```






























