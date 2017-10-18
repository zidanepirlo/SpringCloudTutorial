# SpringCloud（第 030 篇）配置服务端ClientServer对配置文件内容进行对称加解密
-

## 一、大致介绍

``` 
1、前面我们也简单讲解了如何搭建配置服务端微服务，也搭建了配置客户端微服务，但是呢，我们存储在Git上面的内容为明文，在生产环境的话，也不利于传输，特别一些重要的信息容易被泄露；
2、所以此章节，我们讲解一下如何对文件的内容进行加密、解密，有利于内容在网络中的安全传输；

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

    <artifactId>springms-config-server-encrypt</artifactId>
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
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-config-server-encrypt/src/main/resources/application.yml）
``` 
server:
  port: 8255

spring:
  application:
    name: springms-config-server-encrypt
  cloud:
    config:
      server:
        git:
          uri: https://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar
#          username:    # 自己设置，这里就不做演示了
#          password:    # 自己设置，这里就不做演示了



encrypt:
  key: hehui  # 给配置文件的内容进行加密用的
  
```




### 2.3 添加应用启动类（springms-config-server-encrypt/src/main/java/com/springms/cloud/MsConfigServerEncryptApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 配置服务端ClientServer对配置文件内容进行对称加解密。<br/>
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
public class MsConfigServerEncryptApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigServerEncryptApplication.class, args);
        System.out.println("【【【【【【 ConfigServerEncrypt微服务 】】】】】】已启动.");
    }
}
```

## 三、测试

``` 
/****************************************************************************************
 application.yml 涉及到的链接文件内容展示如下：

 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/application.yml
 profile: profile-default

 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/foobar-dev.yml
 profile: profile-dev

 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/foobar-prd.yml
 profile: '{cipher}91e9d2e319f18d32de4821a5e932c759f93e0007dc66e69e0b9587e595e0f241'

 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/foobar-test.properties
 profile: {cipher}3bf7b3e4adf9228d9fc70ecc168a33ff5269bc6efc66eaac8dde5c8e655303a0
 ****************************************************************************************/
 
/****************************************************************************************
 一、配置服务端ClientServer对配置文件内容进行对称加解密（设置配置服务端文件对称加解密）：

 1、注解：EnableConfigServer
 2、编辑 application.yml 文件，注意填写 encrypt.key 属性字段值，该值的作用在于给配置文件的内容进行加密用的；

 3、启动 springms-config-server-encrypt 模块服务，启动1个端口；

 4、下载 Java 8 JCE 文件，下载下来后文件名为 jce_policy-8.zip，然后将解压后的文件直接覆盖到 jdk 中，将 Java\jdk1.8.0_92\jre\lib\security 路径下的文件覆盖即可；

 5、打开windows命令窗口，执行命令：
    >curl.exe localhost:8255/encrypt -d foobar-prd
    91e9d2e319f18d32de4821a5e932c759f93e0007dc66e69e0b9587e595e0f241

    >curl.exe localhost:8255/encrypt -d foobar-test
    3bf7b3e4adf9228d9fc70ecc168a33ff5269bc6efc66eaac8dde5c8e655303a0

    将这两个值进行保存到配置文件，也就是我们的Git仓库中的配置文件；
 6、在浏览器输入地址 http://localhost:8255/foobar-default.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 7、在浏览器输入地址 http://localhost:8255/foobar-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 8、在浏览器输入地址 http://localhost:8255/foobar-prd.yml 正常情况下会输出配置文件的内容（内容为：profile: foobar-prd）；
 9、在浏览器输入地址 http://localhost:8255/foobar-test.yml 正常情况下会输出配置文件的内容（内容为：profile: foobar-test）；
 10、在浏览器输入地址 http://localhost:8255/foobar-test.properties 正常情况下会输出配置文件的内容（内容为：profile: foobar-test）；

 总结一：一切都正常打印，说明 SpringCloud 的解密已经能正确的完成了；

 11、修改 application.yml 文件，将 encrypt.key 属性值随便改下，改成比如 encrypt.key: aaaaaaaaaaa
 12、停止并重启 springms-config-server-encrypt 模块服务，启动1个端口；

 13、在浏览器输入地址 http://localhost:8255/foobar-default.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 14、在浏览器输入地址 http://localhost:8255/foobar-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 15、在浏览器输入地址 http://localhost:8255/foobar-prd.yml 不能正常获取配置文件内容（内容为：invalid: profile: <n/a> profile: profile-default）；
 16、在浏览器输入地址 http://localhost:8255/foobar-test.yml 不能正常获取配置文件内容（内容为：invalid: profile: <n/a> profile: profile-default）；
 17、在浏览器输入地址 http://localhost:8255/foobar-test.properties 不能正常获取配置文件内容（内容为：invalid: profile: <n/a> profile: profile-default）；

 总结二：由此可见 encrypt.key 经过赋值生成配置文件内容后，就不能轻易改变，一旦改变的话，那么原本正常的内容值将获取不到了；
 ****************************************************************************************/
```


## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!




























