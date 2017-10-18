# SpringCloud（第 032 篇）配置服务端ClientServer对配置文件内容进行RSA加解密
-

## 一、大致介绍

``` 
1、上章节我们讲解了对称加密配置文件内容，本章节我们讲解下非对称RSA加密配置文件；

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

    <artifactId>springms-config-server-encrypt-rsa</artifactId>
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


### 2.2 添加应用配置文件（springms-config-server-encrypt-rsa/src/main/resources/application.yml）
``` 
server:
  port: 8265

spring:
  application:
    name: springms-config-server-encrypt-rsa
  cloud:
    config:
      server:
        git:
          uri: https://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar
#          username:    # 自己设置，这里就不做演示了
#          password:    # 自己设置，这里就不做演示了


encrypt:
  keyStore:
    location: classpath:/server-rsa.jks
    password: paic1234
    alias: mytestkey
    secret: aaaaa888  # 私钥密码
  
```




### 2.3 添加应用启动类（springms-config-server-encrypt-rsa/src/main/java/com/springms/cloud/MsConfigServerEncryptRsaApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 配置服务端ClientServer对配置文件内容进行RSA加解密。<br/>
 *
 * 配置服务服务端Server应用入口（设置配置服务端文件 RSA 非对称加解密）。<br/>
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
public class MsConfigServerEncryptRsaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigServerEncryptRsaApplication.class, args);
        System.out.println("【【【【【【 ConfigServerEncryptRsa微服务 】】】】】】已启动.");
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

 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/foobar-stg1rsa.yml
 profile: '{cipher}AQAnkS1BpmB6Obu/Hg3qeXjDyvakHMIwFUVasKax0BIYHkc50ZRF7kcDLpG1o/iwhY8aAVyPGJXGnU7r
 1Su4NKAkQAHX6+yJq3hWd6N2GloQOIgMjjDc4cockGgxG+yoIFT1ggJ+kbzzMR6TDnPYZ3uDBLsngH0c
 9VkEaagpIcGW+2wCAu5KLq/Zh7m2oq65L4illCpPqOwbfvyiFwCpwU/0MH+QXC0+lPu/yXsxLILwRrh9
 7ZpWduQEDjMznMjSSpkbbeniilHjkUVWXsi4w36f194YN4abl3Lvv+pSzUMA72lGxIl7y50AbaeqyNM8
 ju8OKL0yDMmgmfTdxiVCK9DQIfaZHJeN9A5BEllzT5aOUATTsXtTTVSvL3+2RrcMIXw='

 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/foobar-stg2rsa.properties
 profile={cipher}AQBF1BU5+/8EvHkJdoXFvYmYt8K5QvuyTbBl7rwg0G49QSV4IPDDarPFr/10zzcepV8UHpbVHQ9vMJAV6WCefmzMh0YWAPwRsLOgJIgfpbkPacRoVSvwqYEhHshNNQHNOjWT84BDBXiKXcnPeOhnMNUOiB7M05VBZRVwdUuHBN/Zb/L9vxnQLTlwALS1TNfd3JUL7S61oz4JBf/c5FoQUPx/JawUz/uEwi337GCEkFmKacC8fF+cbjLOzsdtHkxrHZtz8QesDCwanwpZl8KbLTzeiU03uAj60qYBaoCYm+A19z+07SXHL0KKhoWp5TcABDv5HY5Bv1astZVp7r+YFAwh/xYnHBYeUwBvmbjTJMYCJEuFNuWr35RhJWJSrAuI1eE=
 ****************************************************************************************/

/****************************************************************************************
 一、配置服务服务端Server应用入口（设置配置服务端文件 RSA 非对称加解密）：

 1、注解：EnableConfigServer
 2、打开windows命令窗口，执行命令：
    >keytool -genkeypair -alias mytestkey -keyalg RSA -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=US" -keypass aaaaa888 -keystore server-rsa.jks -storepass paic1234

    执行完后，正常情况下在会执行命令的目录下生成 server-rsa.jks 文件；

 3、编辑 application.yml 文件，注意填写 encrypt.keyStore 属性字段值；
 4、启动 springms-config-server-encrypt-rsa 模块服务，启动1个端口；

 5、生成配置文件内容，打开windows命令窗口，执行命令：
     >curl.exe localhost:8265/encrypt -d foobar-stg1rsa
     AQAnkS1BpmB6Obu/Hg3qeXjDyvakHMIwFUVasKax0BIYHkc50ZRF7kcDLpG1o/iwhY8aAVyPGJXGnU7r
     1Su4NKAkQAHX6+yJq3hWd6N2GloQOIgMjjDc4cockGgxG+yoIFT1ggJ+kbzzMR6TDnPYZ3uDBLsngH0c
     9VkEaagpIcGW+2wCAu5KLq/Zh7m2oq65L4illCpPqOwbfvyiFwCpwU/0MH+QXC0+lPu/yXsxLILwRrh9
     7ZpWduQEDjMznMjSSpkbbeniilHjkUVWXsi4w36f194YN4abl3Lvv+pSzUMA72lGxIl7y50AbaeqyNM8
     ju8OKL0yDMmgmfTdxiVCK9DQIfaZHJeN9A5BEllzT5aOUATTsXtTTVSvL3+2RrcMIXw=

     >curl.exe localhost:8265/encrypt -d foobar-stg2rsa
     AQBF1BU5+/8EvHkJdoXFvYmYt8K5QvuyTbBl7rwg0G49QSV4IPDDarPFr/10zzcepV8UHpbVHQ9vMJAV
     6WCefmzMh0YWAPwRsLOgJIgfpbkPacRoVSvwqYEhHshNNQHNOjWT84BDBXiKXcnPeOhnMNUOiB7M05VB
     ZRVwdUuHBN/Zb/L9vxnQLTlwALS1TNfd3JUL7S61oz4JBf/c5FoQUPx/JawUz/uEwi337GCEkFmKacC8
     fF+cbjLOzsdtHkxrHZtz8QesDCwanwpZl8KbLTzeiU03uAj60qYBaoCYm+A19z+07SXHL0KKhoWp5TcA
     BDv5HY5Bv1astZVp7r+YFAwh/xYnHBYeUwBvmbjTJMYCJEuFNuWr35RhJWJSrAuI1eE=

     将这两个值进行保存到配置文件，也就是我们的Git仓库中的配置文件；

 6、在浏览器输入地址 http://localhost:8265/foobar-default.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 7、在浏览器输入地址 http://localhost:8265/foobar-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 8、在浏览器输入地址 http://localhost:8265/foobar-stg1rsa.yml 正常情况下会输出配置文件的内容（内容为：profile: foobar-stg1rsa）；
 9、在浏览器输入地址 http://localhost:8265/foobar-stg2rsa.yml 正常情况下会输出配置文件的内容（内容为：profile: foobar-stg2rsa）；
 10、在浏览器输入地址 http://localhost:8265/foobar-stg2rsa.properties 正常情况下会输出配置文件的内容（内容为：profile: foobar-stg2rsa）；

 总结一：一切都正常打印，说明 SpringCloud 的解密已经能正确的完成了，但是注意加密内容保存到 properties 文件的时候，要将回车换行符去掉保存，不然将获取不到正确值；

 11、修改 application.yml 文件，将 encrypt.keyStore 属性值随便改下，改成比如 encrypt.secret: aaaaaaaaaaa
 12、停止并重启 springms-config-server-encrypt-rsa 模块服务，启动1个端口；

 13、在浏览器输入地址 http://localhost:8265/foobar-default.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 14、在浏览器输入地址 http://localhost:8265/foobar-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 15、在浏览器输入地址 http://localhost:8265/foobar-stg1rsa.yml 不能正常获取配置文件内容（内容为：invalid: profile: <n/a> profile: profile-default）；
 16、在浏览器输入地址 http://localhost:8265/foobar-stg2rsa.yml 不能正常获取配置文件内容（内容为：invalid: profile: <n/a> profile: profile-default）；
 17、在浏览器输入地址 http://localhost:8265/foobar-stg2rsa.properties 不能正常获取配置文件内容（内容为：invalid: profile: <n/a> profile: profile-default）；

 总结二：由此可见 encrypt.keyStore 经过赋值生成配置文件内容后，就不能轻易改变，一旦改变的话，那么原本正常的内容值将获取不到了；
 ****************************************************************************************/
```


## 四、下载地址

<font color=#4183C4 size=4>[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)</font>

<font color=#4183C4 size=4>SpringCloudTutorial交流QQ群: 235322432</font>、<font color=#4183C4 size=4>[微信沟通交流群](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)</font>

<font color=red size=4>欢迎关注，您的肯定是对我最大的支持!!!</font>




























