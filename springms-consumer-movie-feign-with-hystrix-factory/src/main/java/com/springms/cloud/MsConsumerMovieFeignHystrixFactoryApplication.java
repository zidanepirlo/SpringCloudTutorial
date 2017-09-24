package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 电影微服务接入Feign，添加 fallbackFactory 属性来触发请求进行容灾降级。
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
public class MsConsumerMovieFeignHystrixFactoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieFeignHystrixFactoryApplication.class, args);
		System.out.println("【【【【【【 电影Feign-HystrixFactory微服务 】】】】】】已启动.");
	}
}



/****************************************************************************************
 一、电影微服务接入Feign，添加 fallbackFactory 属性来触发请求进行容灾降级（测试正常接入功能）：

 1、注解：EnableFeignClients；
 2、编写类 HystrixClientFallbackFactory 回退处理机制类，并给该类加上注解 Component ；加入 FeignClient 注解
 	// @FeignClient(name = "springms-provider-user", fallback = HystrixClientFallback.class  )
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-feign-with-hystrix-factory 模块服务；
 6、在浏览器输入地址 http://localhost:8115/movie/1 可以看到具体的用户信息（即用户ID != 0 的用户）成功的被打印出来；
 ****************************************************************************************/






/****************************************************************************************
 二、电影FeignHystrix-HystrixFactory微服务接入 HystrixFactory 功能模块（测试断路器功能）：

 1、注解：EnableFeignClients；
 2、编写类 HystrixClientFallbackFactory 回退处理机制类，并给该类加上注解 Component，UserFeignHystrixFactoryClient 加上 fallbackFactory 属性；
 	// @FeignClient(name = "springms-provider-user", fallback = HystrixClientFallback.class, fallbackFactory = HystrixClientFallbackFactory.class )
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-feign-with-hystrix-factory 模块服务；
 6、在浏览器输入地址 http://localhost:8115/movie/1 可以看到具体的用户信息（即用户ID != 0 的用户）成功的被打印出来；

 7、停止 springms-provider-user 模块服务；
 8、在浏览器输入地址http://localhost:8115/movie/1 可以看到用户信息ID = 0 的用户成功的被打印出来，但随着问题也来了；
 9、HystrixClientFallbackFactory 截获的异常却没有被打印出来，本来用户微服务停止的话，请求链接就已经链接超时了，但是为啥异常没有打印出来呢？请看下面第三中测试方法。
 ****************************************************************************************/





/****************************************************************************************
 三、电影FeignHystrix-HystrixFactory微服务接入 HystrixFactory 功能模块（测试断路器功能）：

 1、注解：EnableFeignClients；
 2、编写类 HystrixClientFallbackFactory 回退处理机制类，并给该类加上注解 Component，UserFeignHystrixFactoryClient 去掉 fallback 属性，然后加上 fallbackfactory 属性；
 	// @FeignClient(name = "springms-provider-user", fallbackFactory = HystrixClientFallbackFactory.class )
 3、启动 springms-discovery-eureka 模块服务，启动1个端口；
 4、启动 springms-provider-user 模块服务，启动1个端口；
 5、启动 springms-consumer-movie-feign-with-hystrix-factory 模块服务；
 6、在浏览器输入地址 http://localhost:8115/movie/1 可以看到具体的用户信息（即用户ID != 0 的用户）成功的被打印出来；

 7、停止 springms-provider-user 模块服务；
 8、在浏览器输入地址http://localhost:8115/movie/1 可以看到用户信息ID = -1 的用户成功的被打印出来，而且异常信息日志也被打印出来了，这就正常了；

 注意：第2步骤：UserFeignHystrixFactoryClient 去掉 fallback 属性，然后加上 fallbackfactory 属性；
 	  所以这里目前暂时谨记，fallback 和 fallbackfactory 属性会有冲突，所以只要其一就行了；
 ****************************************************************************************/
