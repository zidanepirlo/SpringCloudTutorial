package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 服务发现服务端EurekaServer微服务。<br/>
 *
 * Eureka默认端口是8761
 * http://localhost:8761/eureka/apps 可以查看注册到该服务器上的一堆微服务实例的信息。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        System.out.println("【【【【【【 Eureka微服务 】】】】】】已启动.");
    }
}


/****************************************************************************************
 一、服务发现服务端EurekaServer微服务：

 1、启动 springms-discovery-eureka 模块服务，启动1个端口；
 2、在浏览器输入地址 http://localhost:8761 后，并且输入用户名密码即可登录服务发现服务端；
 ****************************************************************************************/