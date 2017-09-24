package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 用户服务类（添加服务注册，将用户微服务注册到 EurekaServer 中）。
 *
 * 该服务和 springms-provider-user 功能一模一样，仅仅只是模块名称不一样 springms-provider-user-version 而已罢了，主要为 springms-gateway-zuul-reg-exp 模块做测试而已；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class MsProviderUserVersionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsProviderUserVersionApplication.class);
        System.out.println("【【【【【【 用户微Version服务 】】】】】】已启动.");
    }
}


/****************************************************************************************
 一、用户微服务接口测试：

 1、注解：EnableEurekaClient
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user-version 模块服务，启动1个端口；
 4、在浏览器输入地址http://localhost:7905/simple/1 可以看到信息成功的被打印出来，说明用户微服务正常；

 5、在浏览器输入地址 http://localhost:8761 并输入用户名密码 admin/admin 进入Eureka微服务显示在网页中，说明用户微服务确实注册到了 eureka 服务中；
 6、在浏览器输入地址 http://localhost:8761/eureka/apps/springms-provider-user 可以看到自定义的 <metadata>信息以及用户微服务的相关信息成功的被展示出来了；
 ****************************************************************************************/


