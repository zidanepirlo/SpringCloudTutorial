package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 电影Ribbon微服务，脱离Eureka使用配置listOfServers进行客户端负载均衡调度。
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
 * @date 2017/9/18
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class MsConsumerMoviePropertiesWithoutEurekaApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MsConsumerMoviePropertiesWithoutEurekaApplication.class, args);
        System.out.println("【【【【【【 电影微服务-PropertiesWithoutEureka定制Ribbon 】】】】】】已启动.");
    }
}



/****************************************************************************************
 一、电影Ribbon微服务，脱离Eureka使用配置listOfServers进行客户端负载均衡调度（正常使用服务测试）：

 1、application.yml 配置：ribbon.eureka.enabled: false
 2、application.yml 配置：springms-provider-user.ribbon.listOfServers: localhost:7900
 3、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 4、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；

 5、启动 springms-consumer-movie-ribbon-properties-without-eureka 模块服务；
 6、在浏览器输入地址 http://localhost:8040/movie/1，连续刷新9次，然后看看 springms-provider-user、springms-provider-user2 的这几个端口的服务打印日志情况，正常情况下只会有7999该端口才会有9条用户信息日志打印出来；

 总结：之所以只有用户微服务7999端口打印日志，首先禁用了eureka的使用，如果没禁用的话，3个端口按道理都会打印日志；其次配置 listOfServers 仅仅只选择了7999一个端口的微服务；
 ****************************************************************************************/







/****************************************************************************************
 二、电影Ribbon微服务，脱离Eureka使用配置listOfServers进行客户端负载均衡调度（负载均衡调度）：

 1、application.yml 配置：ribbon.eureka.enabled: false
 2、application.yml 配置：springms-provider-user.ribbon.listOfServers: localhost:7898,localhost:7899,localhost:7900
 3、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 4、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；

 5、启动 springms-consumer-movie-ribbon-properties-without-eureka 模块服务；
 6、在浏览器输入地址 http://localhost:8040/movie/1，连续刷新9次，然后看看 springms-provider-user、springms-provider-user2 的这几个端口的服务打印日志情况，正常情况下springms-provider-user的3个端口都会打印日志，而且是轮询打印；

 总结：之所以7900、7899、7898三个端口轮询打印，是因为没有配置任何调度算法，默认的调度算法是轮询，所以3个端口当然会轮询打印用户信息；
 ****************************************************************************************/























