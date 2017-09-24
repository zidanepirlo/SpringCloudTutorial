package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 *
 * 电影微服务，定制Feign，一个Feign功能禁用Hystrix，另一个Feign功能启用Hystrix。
 *
 * Feign: Java HTTP 客户端开发的工具。
 *
 * 注解 EnableFeignClients 表示该电影微服务已经接入 Feign 模块。
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
@EnableFeignClients
public class MsConsumerMovieFeignCustomWithoutHystrixApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieFeignCustomWithoutHystrixApplication.class, args);
		System.out.println("【【【【【【 电影自定义FeignWithoutHystrix微服务 】】】】】】已启动.");
	}
}

/****************************************************************************************
 一、电影微服务，定制Feign，一个Feign功能禁用Hystrix，另一个Feign功能启用Hystrix（正常功能测试）：

 1、编写：UserFeignCustomClientFallback、UserFeignCustomSecondClientFallback 断路器回退客户端类；
 2、编写 TestEurekaAuthConfiguration 配置，加入禁用 Hystrix 模块功能的代码，表示禁用该配置的客户端模块；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-feign-custom-without-hystrix 模块服务；
 6、在浏览器输入地址 http://localhost:8110/movie/1 可以看到信息成功的被打印出来，表明正常情况下一切正常；
 7、在浏览器输入地址 http://localhost:8110/springms-provider-user 可以看到信息成功的被打印出来，表明正常情况下一切正常；

 注意：如果第6步如果一开始就输入ID = 0 的用户信息，不要灰心，耐心等等，说不定服务之间还没通信链接成功呢。。。
      如果第6步的地址输入错误，请回头看看 springms-provider-user 该微服务 appname 的名字，再次输入http://localhost:8110/ + appname 即可试试；

 ****************************************************************************************/




/****************************************************************************************
 二、电影微服务，定制Feign，一个Feign功能禁用Hystrix，另一个Feign功能启用Hystrix（禁用其中一个 Feign 的断路器功能）：

 1、编写：UserFeignCustomClientFallback、UserFeignCustomSecondClientFallback 断路器回退客户端类；
 2、编写 TestEurekaAuthConfiguration 配置，加入禁用 Hystrix 模块功能的代码，表示禁用该配置的客户端模块；
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-feign-custom-without-hystrix 模块服务；
 6、在浏览器输入地址 http://localhost:8110/movie/1 可以看到信息成功的被打印出来，表明正常情况下一切正常；
 7、在浏览器输入地址 http://localhost:8110/springms-provider-user 可以看到信息成功的被打印出来，表明正常情况下一切正常；

 8、关闭 springms-provider-user 模块服务；
 9、在浏览器输入地址 http://localhost:8110/movie/1 可以看到用户ID = 0 的用户信息被打印出来，表明该模块的Hystrix断路器模块起作用了；

 10、再关闭 springms-discovery-eureka 模块服务；
 11、再在浏览器输入地址 http://localhost:8110/appname-springms-provider-user 可以看到网页已经打印出链接不上服务的错误页面了，表明该模块禁用Hystrix断路器模块也起作用了；
 ****************************************************************************************/

