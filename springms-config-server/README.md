# SpringCloud（第 028 篇）ConfigServer 配置管理微服务
-

## 一、大致介绍

``` 
1、在讲到配置时，不得不说 SpringCloud 提供了一套解决分布式的配置管理方案，它既包含了服务端ConfigServer也包含了客户端ConfigClient；
2、SpringCloud 将配置文件当作源代码一样存储到 git 或者 svn 服务器上，虽然说这样没有什么管理界面配置啥的，既然能用 svn 上传上去，那也能做成管理界面，只是花的工作量多少而已了，而既然都说了是配置，那就是只要会稍微学些git或者svn的提交文件方式，基本上任何都极易掌握；
3、当我们把配置文件放在 git 上的时候，我们如果要做到更新的话，我们需要借助于 git 网页上的 push 操作来触发更新操作；
4、说的有点多了，那么我们今天就来小试牛刀，简单试试 ConfigServer 是如何和 Git 服务器相关联存储配置的，至于更新机制操作的话，请看后序章节讲解；

5、这里还顺便列举下配置路径的规则：
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

    <artifactId>springms-config-server</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.springms.cloud</groupId>
        <artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <dependencies>
        <!-- Config 服务端配置模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-config-server\src\main\resources\application.yml）
``` 
server:
  port: 8220

spring:
  application:
    name: springms-config-server
  cloud:
    config:
      server:
        git:
          uri: https://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar

```




### 2.3 添加sidecar微服务启动类（springms-config-server\src\main\java\com\springms\cloud\MsConfigServerApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * ConfigServer 配置管理微服务。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/28
 *
 */
@SpringBootApplication
@EnableConfigServer
public class MsConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigServerApplication.class, args);
        System.out.println("【【【【【【 ConfigServer微服务 】】】】】】已启动.");
    }
}
```



## 三、测试

``` 
/****************************************************************************************
 一、ConfigServer 配置管理微服务（正常测试）（添加一个配置文件 application.yml 来做测试）：

 1、注解：EnableConfigServer
 2、编辑 application.yml 文件，注意只填写 cloud.config.server.git.uri 属性；
 3、启动 springms-config-server 模块服务，启动1个端口；

 4、在浏览器输入地址 http://localhost:8220/abc-default.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 5、在浏览器输入地址 http://localhost:8220/abc-default.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 6、在浏览器输入地址 http://localhost:8220/abc-hehui.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 7、在浏览器输入地址 http://localhost:8220/aaa-bbb.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 8、在浏览器输入地址 http://localhost:8220/aaa-bbb.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；

 总结一：按照 /{application}-{profile}.yml 和 /{application}-{profile}.properties 这种规则来测试的，当找不到路径的话，会默认找到 application.yml 文件读取字段内容；

 9、在浏览器输入地址 http://localhost:8220/master/abc-default.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 10、在浏览器输入地址 http://localhost:8220/master/abc-default.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 11、在浏览器输入地址 http://localhost:8220/master/abc-hehui.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 12、在浏览器输入地址 http://localhost:8220/master/aaa-bbb.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 13、在浏览器输入地址 http://localhost:8220/master/aaa-bbb.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 14、在浏览器输入地址 http://localhost:8220/springms-config-server-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；

 总结二：按照 /{label}/{application}-{profile}.yml 和 /{label}/{application}-{profile}.properties 这种规则来测试，当找不到路径的话，会默认找到 application.yml 文件读取字段内容；
 总结三：所以不管怎么测试路径规则，找不到的话，反正也不会抛什么异常，反正一律会映射到 application.yml 文件上；
 ****************************************************************************************/

/****************************************************************************************
 二、ConfigServer 配置管理微服务（再添加一个配置文件 foobar-dev.yml 来做测试）：

 1、注解：EnableConfigServer
 2、编辑 application.yml 文件，注意只填写 cloud.config.server.git.uri 属性；
 3、启动 springms-config-server 模块服务，启动1个端口；

 4、在浏览器输入地址 http://localhost:8220/foobar-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 5、在浏览器输入地址 http://localhost:8220/foobar-dev.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 6、在浏览器输入地址 http://localhost:8220/master/foobar-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 7、在浏览器输入地址 http://localhost:8220/master/foobar-dev.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；

 总结一：按照 /{application}-{profile}.yml 和 /{application}-{profile}.properties
 /{label}/{application}-{profile}.yml 和 /{label}/{application}-{profile}.properties
 这种规则来测试的，会找到 foobar-dev.yml 文件，既然找到了 foobar-dev.yml 文件的话，那自然就没 application.yml 文件什么事情了；

 8、在浏览器输入地址 http://localhost:8220/foobar-aaa.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 9、在浏览器输入地址 http://localhost:8220/foobar-aaa.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 10、在浏览器输入地址 http://localhost:8220/master/foobar-aaa.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 11、在浏览器输入地址 http://localhost:8220/master/foobar-aaa.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；

 总结二：将dev改成aaa的话，按照配置服务的路径规则配置，当路径中的配置文件不再url的目录下的话，那么则会找到默认配置的文件 application.yml 来加载显示。
 ****************************************************************************************/
```


## 四、下载地址

<font color=#4183C4 size=4>[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)</font>

<font color=#4183C4 size=4>SpringCloudTutorial交流QQ群: 235322432</font>、<font color=#4183C4 size=4>[微信沟通交流群](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)</font>

<font color=red size=4>欢迎关注，您的肯定是对我最大的支持!!!</font>






























