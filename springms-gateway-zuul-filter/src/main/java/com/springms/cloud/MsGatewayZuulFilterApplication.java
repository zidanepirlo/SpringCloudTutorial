package com.springms.cloud;

import com.springms.cloud.filter.PreZuulFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * Zuul 的过滤器 ZuulFilter 的使用。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8150/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
 *
 * 想看更多关于过滤器的使用的话，请移步源码路径：spring-cloud-netflix-core-1.2.7.RELEASE.jar中的org.springframework.cloud.netflix.zuul.filters目录下；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/26
 *
 */
@SpringBootApplication
@EnableZuulProxy
public class MsGatewayZuulFilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulFilterApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulFilter微服务 】】】】】】已启动.");
    }

    /**
     * 即使其它配置都写好的话，那么不添加这个 Bean 的方法的话，还是不会执行任何过滤的方法；
     *
     * @return
     */
    @Bean
    public PreZuulFilter preZuulFilter() {
        return new PreZuulFilter();
    }
}


/****************************************************************************************
 一、Zuul 的过滤器 ZuulFilter 的使用（正常情况测试）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 2、修改 PreZuulFilter 的 shouldFilter 方法返回 true 即可，表明要使用过滤功能；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 5、启动 springms-gateway-zuul-filter 模块服务；

 6、新起网页页签，输入 http://localhost:8215/routes 正常情况下是能看到zuul需要代理的各个服务列表；

 7、新起网页页签，然后输入 http://localhost:8215/springms-provider-user/simple/1 正常情况下是能看到 ID != 0 一堆用户信息被打印出来，并且该zuul的微服务日志控制台会打印一堆 PreZuulFilter 打印的日志内容；
 8、然后会看到 PreZuulFilter.run 方法中的日志被打印出来，说名确实进入了过滤的方法里面，过滤起作用了；

 总结：过滤确实起了作用，那是因为过滤器的配置中 shouldFilter 设置的 true，需要过滤，所以当然会过滤啦，直接 run 方法中的打印信息即可；
 ****************************************************************************************/





/****************************************************************************************
 二、Zuul 的过滤器 ZuulFilter 的使用（使用过滤器，但是使得过滤的run方法失效）：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 2、修改 PreZuulFilter 的 shouldFilter 方法返回 false 即可，表明不需要使用过滤器；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口（application.yml 文件中的 appname 属性不去掉的话，测试一是无法测试通过的）；
 5、启动 springms-gateway-zuul-filter 模块服务；

 6、新起网页页签，输入 http://localhost:8215/routes 正常情况下是能看到zuul需要代理的各个服务列表；

 7、新起网页页签，然后输入 http://localhost:8215/springms-provider-user/simple/1 正常情况下是能看到 ID != 0 一堆用户信息被打印出来，但是该zuul的微服务日志控制台并不会打印一堆 PreZuulFilter 打印的日志内容；
 8、然后再看，PreZuulFilter.run 方法中的日志不见了，没有被打印出来，过滤的run方法失效了；


 总结：由此可见，PreZuulFilter 的 shouldFilter 设置为 false，过滤器就已经失去效果了；
 ****************************************************************************************/













