package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 *
 * 电影微服务使用定制化 Feign 在客户端进行负载均衡调度并为 Feign 配置帐号密码登录认证 Eureka。
 *
 * Feign: Java HTTP 客户端开发的工具。
 *
 * 注解 EnableFeignClients 表示该电影微服务已经接入 Feign 模块。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/19
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MsConsumerMovieFeignCustomApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieFeignCustomApplication.class, args);
		System.out.println("【【【【【【 电影自定义Feign微服务 】】】】】】已启动.");
	}
}




/****************************************************************************************
 一、电影微服务使用定制化Feign在客户端进行负载均衡调度并为Feign配置帐号密码登录认证Eureka（测试接入 Feign 模块）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口；
 4、启动 springms-consumer-movie-feign-custom 模块服务；
 5、在浏览器输入地址http://localhost:8050/movie/1 可以看到信息成功的被打印出来；

 总结：说明接入 Feign 已经成功通过测试；
 ****************************************************************************************/






/****************************************************************************************
 二、电影微服务使用定制化Feign在客户端进行负载均衡调度并为Feign配置帐号密码登录认证Eureka（测试登录 Eureka 服务器需要认证配置）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动1个端口；
 4、启动 springms-consumer-movie-feign-custom 模块服务；
 5、在浏览器输入地址 http://localhost:8050/springms-provider-user 可以看到信息成功的被打印出来；

 总结：说明 TestEurekaAuthConfiguration 类中配置的帐号密码已经生效，可以正常访问 Eureka 服务；
 ****************************************************************************************/






/****************************************************************************************
 三、电影微服务使用定制化Feign在客户端进行负载均衡调度并为Feign配置帐号密码登录认证Eureka（测试接入 Feign 模块进行负载均衡）：

 1、注解：EnableFeignClients
 2、启动 springms-discovery-eureka 模块服务，启动1个端口；
 3、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 4、启动 springms-consumer-movie-feign-custom 模块服务；
 5、在浏览器输入地址http://localhost:8050/movie/1 连续刷新9次，正常情况可以看到 springms-provider-user 的3个端口轮询打印用户日志信息；

 总结：1、说明接入 Feign 已经成功的在客户端进行了负载均衡处理；
 2、之所以会在客户端进行轮询打印日志信息，是因为没有配置调度算法，而默认的调度算法就是轮询，所以会出现轮询打印日志信息；
 ****************************************************************************************/






/****************************************************************************************
 四、电影微服务使用定制化Feign在客户端进行负载均衡调度并为Feign配置帐号密码登录认证Eureka（配置日志级别）：

 1、application.yml 修改：logging.level.com.springms.cloud.feign.UserFeignCustomClient: DEBUG
 2、编写 TestFeignCustomConfiguration 新增日志级别的方法
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-feign-custom 模块服务；
 6、在浏览器输入地址http://localhost:8050/springms-provider-user 可以看到控制台中 DEBUG 日志级别的信息成功的被打印出来；
 ****************************************************************************************/














