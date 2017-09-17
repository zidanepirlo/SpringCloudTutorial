package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 电影微服务，使用 application.yml 配置文件配置 Ribbon 在客户端进行负载均衡调度算法。
 *
 * LoadBalanced：该负载均衡注解，已经整合了 Ribbon；
 *
 * Ribbon 的默认负载均衡的算法为：轮询；
 *
 * 配置文件优先级最高，Java代码设置的配置其次，默认的配置优先级最低；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class MsConsumerMovieRibbonPropertiesApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MsConsumerMovieRibbonPropertiesApplication.class, args);
        System.out.println("【【【【【【 电影微服务-Properties定制Ribbon 】】】】】】已启动.");
    }
}




/****************************************************************************************
 一、电影微服务，使用 application.yml 配置文件配置 Ribbon 在客户端进行负载均衡调度算法（测试轮询分配服务器地址）：

 1、application.yml配置轮询算法：springms-provider-user.ribbon.NFLoadBalancerRuleClassName=com.netflix.loadbalancer.RoundRobinRule；
 2、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 3、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；
 4、启动 springms-consumer-movie-ribbon-properties 模块服务，启动1个端口；
 5、在浏览器输入地址http://localhost:8030/choose，然后看看 springms-provider-user、springms-provider-user2 的各个对应的端口的服务打印的信息是否均匀，正常情况下应该是轮询分配打印的；

 总结：springms-provider-user（之所以轮询是因为配置文件采用 RoundRobinRule 轮询调度算法）、springms-provider-user2（之所以轮询是因为没有任何配置，默认调度算法就是轮询算法）
 ****************************************************************************************/






/****************************************************************************************
 二、电影微服务，使用 application.yml 配置文件配置 Ribbon 在客户端进行负载均衡调度算法（测试随机分配服务器地址）：

 1、application.yml配置随机算法：springms-provider-user.ribbon.NFLoadBalancerRuleClassName=com.netflix.loadbalancer.RandomRule；
 2、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 3、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；
 4、启动 springms-consumer-movie-ribbon-properties 模块服务，启动1个端口；
 5、在浏览器输入地址http://localhost:8030/choose，然后看看 springms-provider-user、springms-provider-user2 的各个对应的端口的服务打印的信息是否均匀，正常情况下应该是轮询分配打印的；

 总结：springms-provider-user（之所以随机是因为配置文件采用 RandomRule 随机调度算法）、springms-provider-user2（之所以轮询是因为没有任何配置，默认调度算法就是轮询算法）
 ****************************************************************************************/






