package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Zuul 路由后面的微服务挂了后，Zuul 提供了一种回退机制来应对熔断处理。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8150/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/27
 *
 */
@SpringBootApplication
@EnableZuulProxy
public class MsGatewayZuulFallbackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulFallbackApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulFallback微服务 】】】】】】已启动.");
    }
}


/****************************************************************************************
 一、Zuul 路由后面的微服务挂了后，Zuul 提供了一种回退机制来应对熔断处理：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 4、启动 springms-gateway-zuul-fallback 模块服务，启动1个端口；

 5、新起网页页签，输入 http://localhost:7900/simple/3 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；
 6、新起网页页签，然后输入 http://localhost:8200/springms-provider-user/simple/3，正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 7、这个时候，停止 springms-provider-user 模块服务；
 8、刷新 http://localhost:8200/springms-provider-user/simple/3 网页，正常情况下会提示 “fallback” 字样的字符串；

 ...... 等待大约两分钟左右 ......(微服务宕机默认好像是90秒再连不上eureka服务的话，就会被eureka服务剔除掉)

 9、待用户微服务被踢出后，刷新 http://localhost:8200/springms-provider-user/simple/3 网页，正当情况下会提示 404 错误页面，因为用户微服务由于宕机超过大约90秒后会自动被 eureka 服务器剔除掉，所以访问网页必然找不到服务路径；

 总结：首先 Zuul 作为路由转发微服务，其也提供了一种熔断机制，避免大量请求阻塞在路由分发处；
      其次当注册进入 eureka 服务治理发现框架后，一定时间后还没有连上eureka时，这个时候eureka就会将这个宕机的微服务移除服务治理框架；
 ****************************************************************************************/










