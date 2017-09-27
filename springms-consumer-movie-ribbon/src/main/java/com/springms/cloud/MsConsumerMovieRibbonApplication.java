package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 电影微服务，使用 Ribbon 在客户端进行负载均衡。
 *
 * LoadBalanced：该负载均衡注解，已经整合了 Ribbon；
 *
 * 在浏览器输入http://localhost:8010/movie/3 地址后，注解 LoadBalanced 会进行负载均衡将请求分配到不同的【用户微服务】上面；
 *
 * Ribbon 的默认负载均衡的算法为：轮询；
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
public class MsConsumerMovieRibbonApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieRibbonApplication.class, args);
		System.out.println("【【【【【【 电影微服务-Ribbon 】】】】】】已启动.");
	}
}



/****************************************************************************************
 综上所述：
 Ribbon的负载均衡，主要通过LoadBalancerClient来实现的，而LoadBalancerClient具体交给了ILoadBalancer来处理，
 ILoadBalancer通过配置IRule、IPing等信息，并向EurekaClient获取注册列表的信息，并默认10秒一次向EurekaClient发送“ping”,
 进而检查是否更新服务列表，最后，得到注册列表后，ILoadBalancer根据IRule的策略进行负载均衡。

 而RestTemplate 被@LoadBalance注解后，能过用负载均衡，主要是维护了一个被@LoadBalance注解的RestTemplate列表，并给列表中的RestTemplate添加拦截器，进而交给负载均衡器去处理。
 ****************************************************************************************/





/****************************************************************************************
 一、电影微服务，使用 Ribbon 在客户端进行负载均衡：

 1、启动 springms-discovery-eureka 模块服务，启动1个端口；
 2、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 3、启动 springms-consumer-movie-ribbon 模块服务，启动1个端口；
 4、在浏览器输入地址 http://localhost:7900/simple/1 可以看到信息成功的被打印出来，说明用户微服务正常；
 5、在浏览器输入地址 http://localhost:8761 并输入用户名密码 admin/admin 进入Eureka微服务显示在网页中，验证三个用户微服务、1电影Ribbon微服务确实注册到了 eureka 服务中；

 6、在浏览器输入地址http://localhost:8010/movie/1 连续在浏览器回车6次；
 7、然后跑到用户微服务的三台服务器上查看 Hibernate 查询用户的 sql 日志，每台用户微服务的都被均匀的调用了 2 次用户信息；

 总结：由此可见，启动了3台用户微服务都注册到eureka服务中心，然后通过Ribbon对电影微服务进行客户端负载均衡调度，从而实现了客户端负载均衡算法，默认调度算法为轮询；
 ****************************************************************************************/


