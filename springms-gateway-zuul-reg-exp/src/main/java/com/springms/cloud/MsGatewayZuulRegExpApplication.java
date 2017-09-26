package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

/**
 * Zuul 网关微服务的 regexmapper 属性测试, 类似测试 zuul 的自定义路径规则一样。
 *
 * 注意 EnableZuulProxy 注解能注册到 eureka 服务上，是因为该注解包含了 eureka 客户端的注解，该 EnableZuulProxy 是一个复合注解。
 *
 * @EnableZuulProxy --> { @EnableCircuitBreaker、@EnableDiscoveryClient } 包含了 eureka 客户端注解，同时也包含了 Hystrix 断路器模块注解。
 *
 * http://localhost:8185/routes 地址可以查看该zuul微服务网关代理了多少微服务的serviceId。
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
public class MsGatewayZuulRegExpApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGatewayZuulRegExpApplication.class, args);
        System.out.println("【【【【【【 GatewayZuulRegExp微服务 】】】】】】已启动.");
    }

    /**
     * 使用regexmapper提供serviceId和routes之间的绑定. 它使用正则表达式组来从serviceId提取变量, 然后注入到路由表达式中。
     *
     * 这个意思是说"springms-provider-user-version"将会匹配路由"/version/springms-provider-user/**". 任何正则表达式都可以, 但是所有组必须存在于servicePattern和routePattern之中.
     *
     * @return
     */
    @Bean
    public PatternServiceRouteMapper serviceRouteMapper() {
        return new PatternServiceRouteMapper("(?<name>^.+)-(?<version>v.+$)", "${version}/${name}");
    }
}


/****************************************************************************************
 一、Zuul 网关微服务的 regexmapper 属性测试, 类似测试 zuul 的自定义路径规则一样：

 1、编写 application.yml 文件，添加应用程序的注解 EnableZuulProxy 配置；
 2、在 MsGatewayZuulRegExpApplication 类中添加 serviceRouteMapper 方法；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user-version 模块服务，启动1个端口；
 5、启动 springms-gateway-zuul-reg-exp 模块服务；

 6、新起网页页签，输入 http://localhost:8185/version/springms-provider-user/simple/4 正常情况下是能看到 ID != 0 一堆用户信息被打印出来；

 总结：springms-provider-user-version 通过名字和版本被切割后，利用路径拼接规则，通过 http://localhost:8185/version/springms-provider-user/simple/4 也可以访问用户微服务；
 ****************************************************************************************/













