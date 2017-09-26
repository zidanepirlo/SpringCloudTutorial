# SpringCloud（第 023 篇）简单文件上传微服务采取curl或者页面点击实现文件上传
-

## 一、大致介绍

``` 
1、本章节主要搭建了一个简单的页面上传Web控制器，主要为后序工作加入 zuul 微服务而做的准备；
2、不过在本章节用命令上传文件的时候，在windows命令窗口有时候会出现中文乱码什么的，请注意看本文 FileUploadController 是如何解决这个乱码问题的；
3、至于使用 curl 命令需要下载什么安装包之类的，这个就请大家找找度娘怎么弄吧。
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-file-upload</artifactId>
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

        <!-- 客户端发现模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>

        <!-- 监控和管理生产环境的模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-file-upload\src\main\resources\application.yml）
``` 
server:
  port: 8190
eureka:
  client:
    serviceUrl:
      defaultZone: http://admin:admin@localhost:8761/eureka/
  instance:
    prefer-ip-address: true
spring:
  application:
    name: springms-file-upload
  http:
    multipart:
      max-file-size: 20Mb      # Max file size，默认1M
      max-request-size: 20Mb   # Max request size，默认10M



#####################################################################################################
# 打印日志
logging:
  level:
    root: INFO
    com.springms: DEBUG
    com.netflix: debug
#####################################################################################################
```




### 2.3 添加简单的上传文件页面（springms-file-upload\src\main\resources\static\index.html）
``` 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form method="POST" enctype="multipart/form-data" action="/upload">
		File to upload: 
		<input type="file" name="file">
		<input type="submit" value="Upload">
	</form>
</body>
</html>
```




### 2.4 添加上传文件Web控制器（springms-file-upload\src\main\java\com\springms\cloud\controller\FileUploadController.java）
``` 
package com.springms.cloud.controller;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 上传文件控制器。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/26
 *
 */
@RestController
public class FileUploadController {

    /**
     * 上传文件。
     *
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam(value = "file", required = true)MultipartFile file) throws IOException{
        byte[] bytes = file.getBytes();
        File fileToSave = new File(file.getOriginalFilename());
        FileCopyUtils.copy(bytes, fileToSave);
        return fileToSave.getAbsolutePath();
    }

//    解决 windows 的 curl 命令执行后返回乱码
//    chcp 65001 就是换成UTF-8代码页
//    chcp 936 可以换回默认的GBK
//    chcp 437 是美国英语
//    在命令行标题栏上点击右键，选择"属性"->"字体",将字体修改为True Type字体"Lucida Console",然后点击确定将属性应用到当前窗口。
}
```



### 2.5 添加文件服务启动类（springms-file-upload\src\main\java\com\springms\cloud\MsFileUploadApplication.java）
``` 
package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 简单文件上传微服务采取curl或者页面点击实现文件上传。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/26
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class MsFileUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsFileUploadApplication.class, args);
        System.out.println("【【【【【【 FileUpload微服务 】】】】】】已启动.");
    }
}

```



## 三、测试

``` 
/****************************************************************************************
 一、简单文件上传微服务，并加入 zuul 微服务后用 zuul 微服务地址采取curl或者页面点击实现文件上传（页面上传文件）：

 1、编写 FileUploadController 文件，添加应用程序的注解 EnableEurekaClient 配置；
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-file-upload 模块服务；

 4、新起网页页签，输入 http://localhost:8190/index.html 正常情况下是能看到选择文件上传的界面；
 5、选择文件，然后点击 upload 上传文件，然后可以在该项目所在的根目录可以看到刚刚上传的那个文件，而且网页也会将刚刚上传完后的磁盘路径呈现在页面上；
 ****************************************************************************************/





/****************************************************************************************
 二、简单文件上传微服务，并加入 zuul 微服务后用 zuul 微服务地址采取curl或者页面点击实现文件上传（命令上传文件）：

 1、编写 FileUploadController 文件，添加应用程序的注解 EnableEurekaClient 配置；
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-file-upload 模块服务；

 4、进入 curl.exe 所在的目录，尝试 curl.exe www.baidu.com 看看是否正常，正常情况下会打印百度首页的一堆信息；
 5、执行命令：curl.exe -F GBK "file=@文件名称" localhost:8190/upload
 6、正常情况下，第5步骤执行后，直接返回上传成功文件所在的磁盘全路径；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

SpringCloudTutorial交流QQ群: 235322432

欢迎关注，您的肯定是对我最大的支持!!!
```






























