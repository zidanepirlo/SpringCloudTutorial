# SpringCloud（第 046 篇）注解式Schedule配置定时任务，不支持任务调度
-

## 一、大致介绍

``` 
1、很多时候我们需要隔一定的时间去执行某个任务，为了实现这样的需求通常最普通的方式就是利用多线程来实现；
2、但是有时候这个任务还真得去处理一些非常复杂非常耗时的动作，那么在SpringCloud生态圈中，Scheduled不失为一种好的解决方案；
3、不过我们这里介绍的Scheduled如果部署在多台服务的话，那么每台都会执行，不支持任务调度；
4、若要支持任务调度的话，请回头查看（第 010 篇）简单 Quartz-Cluster 微服务，支持集群任务调度；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-schedule</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
	
    <parent>
        <groupId>com.springms.cloud</groupId>
        <artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
	
	<dependencies>
        <!-- 访问数据库模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- web模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- MYSQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-schedule\src\main\resources\application.yml）
``` 
server:
  port: 8340
spring:
  application:
    name: springms-schedule  #全部小写

```


### 2.3 添加定时任务类（springms-schedule\src\main\java\com\springms\cloud\task\ScheduledTasks.java）
``` 
package com.springms.cloud.task;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@Component
public class ScheduledTasks {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 1000)
    public void reportCurrentTime() {
        System.out.println("当前时间: " + dateFormat.format(new Date()));
        Logger.info("打印当前时间: {}.", dateFormat.format(new Date()));
    }

    /**
     * 定时任务触发，操作多个DAO添加数据，事务中任一异常，都可以正常导致数据回滚。
     */
    @Scheduled(fixedRate = 5000)
    public void addMovieJob() {
        System.out.println("当前时间: " + dateFormat.format(new Date()));
        Logger.info("当前时间: {}.", dateFormat.format(new Date()));
    }
}

```


### 2.4 添加微服务启动类（springms-schedule\src\main\java\com\springms\cloud\MsScheduleApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 注解式Schedule配置定时任务，不支持任务调度。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@SpringBootApplication
@EnableScheduling
public class MsScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsScheduleApplication.class, args);
		System.out.println("【【【【【【 Schedule定时任务微服务 】】】】】】已启动.");
	}
}
```



## 三、测试

``` 
/****************************************************************************************
 一、用户微服务接口测试：

 1、注解：EnableEurekaClient
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口；
 4、在浏览器输入地址http://localhost:7900/simple/1 可以看到信息成功的被打印出来，说明用户微服务正常；

 5、在浏览器输入地址 http://localhost:8761 并输入用户名密码 admin/admin 进入Eureka微服务显示在网页中，说明用户微服务确实注册到了 eureka 服务中；
 6、在浏览器输入地址 http://localhost:8761/eureka/apps/springms-provider-user 可以看到自定义的 <metadata>信息以及用户微服务的相关信息成功的被展示出来了；
 ****************************************************************************************/
```




## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























