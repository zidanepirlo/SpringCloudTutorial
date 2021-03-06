package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Zuul 网关模块添加 listOfServers 属性，达到客户端负载均衡的能力。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8165/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@SpringBootApplication
@EnableZuulProxy
public class MsGatewayZuulClusterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulClusterApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulCluster微服务 】】】】】】已启动.");
    }
}


/****************************************************************************************
 一、Zuul 网关模块添加 listOfServers 属性，达到客户端负载均衡的能力：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
     # 测试一，API网关模块发现应用入口（添加 listOfServers 属性，测试 zuul 的负载均衡功能）
     zuul:
         routes:
             hmily:
                 path: /custom-path/**
                 serviceId: springms-provider-user

     # 注意，这里在运行的时候有个坑，如果以树形展开写法的话，那么就会出错了，所以这个配置还是避免用树形写法
     ribbon.eureka.enabled: false

     springms-provider-user: # 这里是 ribbon 要请求的微服务的 service-id 值
         ribbon:
            listOfServers: http://localhost:7900,http://localhost:7899,http://localhost:7898
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-gateway-zuul-cluster 模块服务；

 5、新起网页页签，输入 http://localhost:7900/simple/5 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结一：第5步正常，说明 springms-provider-user 服务目前正常；

 6、新起网页页签，然后输入 http://localhost:8165/springms-provider-user/simple/6，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结二：第6步也能正常打印用户信息，说明 API 网关已经生效了，可以通过API服务器地址链接各个微服务的 http://localhost:8165/serviceId/path 这样的路径来访问了；

 7、新起网页页签，然后输入 http://localhost:8165/custom-path/simple/1，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结三：path、serviceId 设置的反向代理路径也通了；

 8、清除 springms-provider-user 模块控制台的所有的日志，然后刷新 9 次该地址 http://localhost:8165/custom-path/simple/1 的网页，然后会发现 3 个用户服务都各打印了3次，再多刷新几次，会发现该负载均衡的调度的算法是轮论调，依次轮询调用每个用户服务微服务；

 总结四：listOfServers 属性也生效了，从而说明添加 listOfServers 也可以达到 zuul 负载均衡的能力；
 ****************************************************************************************/













