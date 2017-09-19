package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 电影微服务接入 Feign 进行客户端负载均衡，通过 FeignClient 调用远程 Http 微服务。
 *
 * Feign: Java HTTP 客户端开发的工具。
 *
 * 注解 EnableFeignClients 表示该电影微服务已经接入 Feign 模块。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/19
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MsConsumerMovieFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieFeignApplication.class, args);
		System.out.println("【【【【【【 电影Feign微服务 】】】】】】已启动.");
	}
}



/****************************************************************************************
 一、电影微服务接入 Feign 进行客户端负载均衡，通过 FeignClient 调用远程 Http 微服务（测试接入 Feign 模块）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口；
 4、启动 springms-consumer-movie-feign 模块服务；
 5、在浏览器输入地址http://localhost:7910/movie/1 或者 http://localhost:7910/movie/user 都可以看到信息成功的被打印出来；

 总结：说明接入 Feign 已经成功通过测试；
 ****************************************************************************************/






/****************************************************************************************
 一、电影微服务接入 Feign 进行客户端负载均衡，通过 FeignClient 调用远程 Http 微服务（测试接入 Feign 模块进行负载均衡）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 4、启动 springms-consumer-movie-feign 模块服务；
 5、在浏览器输入地址http://localhost:7910/movie/1 连续刷新9次，正常情况可以看到 springms-provider-user 的3个端口轮询打印用户日志信息；

 总结：1、说明接入 Feign 已经成功的在客户端进行了负载均衡处理；
      2、之所以会在客户端进行轮询打印日志信息，是因为没有配置调度算法，而默认的调度算法就是轮询，所以会出现轮询打印日志信息；
 ****************************************************************************************/








/****************************************************************************************
 总到来说，Feign的源码实现的过程如下：

 首先通过@EnableFeignCleints注解开启FeignCleint
 根据Feign的规则实现接口，并加@FeignCleint注解
 程序启动后，会进行包扫描，扫描所有的@ FeignCleint的注解的类，并将这些信息注入到ioc容器中。
 当接口的方法被调用，通过jdk的代理，来生成具体的RequesTemplate
 RequesTemplate在生成Request
 Request交给Client去处理，其中Client可以是HttpUrlConnection、HttpClient也可以是Okhttp
 最后Client被封装到LoadBalanceClient类，这个类结合类Ribbon做到了负载均衡。
 ****************************************************************************************/
