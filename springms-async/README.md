# SpringCloud（第 047 篇）注解式Async配置异步任务
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

	<artifactId>springms-async</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
	
    <parent>
        <groupId>com.springms.cloud</groupId>
        <artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
	
	<dependencies>
        <!-- web模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

</project>

```


### 2.2 添加应用配置文件（springms-async\src\main\resources\application.yml）
``` 
server:
  port: 8345
spring:
  application:
    name: springms-async  #全部小写


```


### 2.3 添加异步任务类（springms-async\src\main\java\com\springms\cloud\task\AsyncTasks.java）
``` 
package com.springms.cloud.task;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.concurrent.Future;

/**
 * Async实现异步调用。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@Component
public class AsyncTasks {

    public static Random random = new Random();

    @Async
    public Future<String> doTaskOne() throws Exception {
        System.out.println("Async, taskOne, Start...");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        System.out.println("Async, taskOne, End, 耗时: " + (end - start) + "毫秒");
        return new AsyncResult<>("AsyncTaskOne Finished");
    }

    @Async
    public Future<String> doTaskTwo() throws Exception {
        System.out.println("Async, taskTwo, Start");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        System.out.println("Async, taskTwo, End, 耗时: " + (end - start) + "毫秒");
        return new AsyncResult<>("AsyncTaskTwo Finished");
    }

    @Async
    public Future<String> doTaskThree() throws Exception {
        System.out.println("Async, taskThree, Start");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("Async, taskThree, End, 耗时: " + (end - start) + "毫秒");
        return new AsyncResult<>("AsyncTaskThree Finished");
    }
}
```

### 2.4 添加异步任务Web控制器（springms-async\src\main\java\com\springms\cloud\controller\AsyncTaskController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.task.AsyncTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

/**
 * 测试异步任务Web控制器。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@RestController
public class AsyncTaskController {

    @Autowired
    AsyncTasks asyncTasks;

    /**
     * 测试异步任务。
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/task")
    public String task() throws Exception {
        long start = System.currentTimeMillis();

        Future<String> task1 = asyncTasks.doTaskOne();
        Future<String> task2 = asyncTasks.doTaskTwo();
        Future<String> task3 = asyncTasks.doTaskThree();

        while(true) {
            if(task1.isDone() && task2.isDone() && task3.isDone()) {
                // 三个任务都调用完成，退出循环等待
                break;
            }
            Thread.sleep(1000);
        }

        long end = System.currentTimeMillis();

        String result = "任务全部完成，总耗时：" + (end - start) + "毫秒";
        return result;
    }
}
``` 

### 2.5 添加微服务启动类（springms-schedule\src\main\java\com\springms\cloud\MsScheduleApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 注解式Async配置异步任务；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@SpringBootApplication
@EnableAsync
public class MsAsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAsyncApplication.class, args);
		System.out.println("【【【【【【 Async异步任务微服务 】】】】】】已启动.");
	}
}
```



## 三、测试

``` 
/****************************************************************************************
 一、简单用户链接Mysql数据库微服务（Async实现异步调用）：

 1、添加注解 EnableAsync、Async 以及任务类上注解 Component ；
 2、启动 springms-async 模块服务，启动1个端口；
 3、然后在浏览器输入地址 http://localhost:8345/task 然后等待大约10多秒后，成功打印所有信息，一切正常；

 总结：说明 Async 异步任务配置生效了；
 ****************************************************************************************/
```




## 四、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























