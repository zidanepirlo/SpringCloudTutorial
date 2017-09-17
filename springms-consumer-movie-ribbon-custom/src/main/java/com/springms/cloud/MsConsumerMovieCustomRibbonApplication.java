package com.springms.cloud;

import com.springms.cloud.config.TestConfigurationInside2ScanPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;

/**
 * 电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略。
 *
 * LoadBalanced：该负载均衡注解，已经整合了 Ribbon；
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
@RibbonClient(name = "springms-provider-user", configuration = TestConfigurationInsideScanPackage.class)
@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeFromComponentScan.class) })
public class MsConsumerMovieCustomRibbonApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(MsConsumerMovieCustomRibbonApplication.class, args);
		System.out.println("【【【【【【 电影微服务-定制Ribbon 】】】】】】已启动.");
	}
}



/****************************************************************************************
 一、电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略（测试轮询分配服务器地址 TestConfigurationOutsideScanPackage））：

 1、使用注解：@SpringBootApplication、@EnableEurekaClient；
 2、使用注解：@RibbonClient(name = "springms-provider-user", configuration = TestConfigurationOutsideScanPackage.class)
 3、TestConfigurationInsideScanPackage 类中采用 RoundRobinRule 轮询调度算法；
 4、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 5、启动 springms-consumer-movie-ribbon-custom 模块服务，启动1个端口；
 6、在浏览器输入地址http://localhost:8020/movie/2，然后看看 springms-provider-user 的三个端口的服务打印的信息是否均匀，正常情况下应该是轮询打印；

 总结：客户端之所以会轮询调用各个微服务，是因为在 TestConfigurationOutsideScanPackage 类中配置了负载均衡调度算法：轮询 RoundRobinRule 策略算法；
 ****************************************************************************************/






/****************************************************************************************
 二、电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略（测试轮询分配服务器地址 TestConfigurationInsideScanPackage）：

 1、使用注解：@SpringBootApplication、@EnableEurekaClient；
 2、使用注解：@RibbonClient(name = "springms-provider-user", configuration = TestConfigurationInsideScanPackage.class)
 3、使用注解：@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeFromComponentScan.class) })
 4、TestConfigurationInsideScanPackage 类中采用 RoundRobinRule 轮询调度算法；
 5、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 6、启动 springms-consumer-movie-ribbon-custom 模块服务，启动1个端口；
 7、在浏览器输入地址http://localhost:8020/movie/2，然后看看 springms-provider-user 的三个端口的服务打印的信息是否均匀，正常情况下应该是轮询打印；

 总结一：客户端之所以会轮询分配调用各个微服务，是因为在注解方面采用了注解 ComponentScan 使配置文件 TestConfigurationOutsideScanPackage 不被扫描到，然后再结合 TestConfigurationOutsideScanPackage 类中配置了负载均衡调度算法：轮询 RoundRobinRule 策略算法；
 总结二：可以发现规律，当使用 “restTemplate.getForObject("http://springms-provider-user/simple/" + id, User.class)” 这种方式负载均衡调用各个微服务跟配置文件 TestConfigurationOutsideScanPackage 在哪里没有关系；
 ****************************************************************************************/







/****************************************************************************************
 三、电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略（测试随机分配服务器地址 TestConfigurationOutsideScanPackage））：

 1、使用注解：@SpringBootApplication、@EnableEurekaClient；
 2、使用注解：@RibbonClient(name = "springms-provider-user", configuration = TestConfigurationOutsideScanPackage.class)
 3、TestConfigurationInsideScanPackage 类中采用 RandomRule 随机调度算法；
 4、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 5、启动 springms-consumer-movie-ribbon-custom 模块服务，启动1个端口；
 6、在浏览器输入地址http://localhost:8020/movie/2，然后看看 springms-provider-user 的三个端口的服务打印的信息是否均匀，正常情况下应该是随机打印；

 总结：客户端之所以会随机调用各个微服务，是因为在 TestConfigurationOutsideScanPackage 类中配置了负载均衡调度算法：随机 RandomRule 策略算法；
 ****************************************************************************************/







/****************************************************************************************
 四、电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略（测试随机分配服务器地址 TestConfigurationInsideScanPackage）：

 1、使用注解：@SpringBootApplication、@EnableEurekaClient；
 2、使用注解：@RibbonClient(name = "springms-provider-user", configuration = TestConfigurationInsideScanPackage.class)
 3、使用注解：@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeFromComponentScan.class) })
 4、TestConfigurationInsideScanPackage 类中采用 RandomRule 随机调度算法；
 5、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 6、启动 springms-consumer-movie-ribbon-custom 模块服务，启动1个端口；
 7、在浏览器输入地址http://localhost:8020/movie/2，然后看看 springms-provider-user 的三个端口的服务打印的信息是否均匀，正常情况下应该是随机打印；

 总结一：客户端之所以会轮询分配调用各个微服务，是因为在注解方面采用了注解 ComponentScan 使配置文件 TestConfigurationOutsideScanPackage 不被扫描到，然后再结合 TestConfigurationOutsideScanPackage 类中配置了负载均衡调度算法：随机 RandomRule 策略算法；
 总结二：可以发现规律，当使用 “restTemplate.getForObject("http://springms-provider-user/simple/" + id, User.class)” 这种方式负载均衡调用各个微服务跟配置文件 TestConfigurationOutsideScanPackage 在哪里没有关系；

 总结三：由（测试一、测试二）和（测试三、测试四）对比可知，当使用 “restTemplate.getForObject("http://springms-provider-user/simple/" + id, User.class)” 这种方式负载均衡调用各个微服务不需要考虑配置文件的放在哪个包下面。
 ****************************************************************************************/






/****************************************************************************************
 五、电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略（测试自定义分配服务器地址 TestConfigurationInside2ScanPackage 调度方式）：

 1、使用注解：@SpringBootApplication、@EnableEurekaClient；
 2、使用注解：@RibbonClient(name = "springms-provider-user", configuration = TestConfigurationInside2ScanPackage.class)
 3、使用注解：@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeFromComponentScan.class) })
 3、TestConfigurationInside2ScanPackage 类中采用 RoundRobinRule 轮询调度算法；
 4、在 MovieCustomRibbonController 里面添加 test 方法来做测试；
 5、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 6、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；
 7、启动 springms-consumer-movie-ribbon-custom 模块服务；

 8、在浏览器输入地址http://localhost:8020/choose，然后看看 springms-provider-user、springms-provider-user2 的各个对应的端口的服务打印的信息是否均匀，正常情况下应该是轮询分配打印的；

 总结：springms-provider-user（之所以轮询是因为使用了 RibbonClient 配置采用 RoundRobinRule 轮询调度算法）、springms-provider-user2（之所以轮询是因为没有任何配置，默认调度算法就是轮询算法）；
 ****************************************************************************************/







/****************************************************************************************
 六、电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略（测试自定义分配服务器地址 TestConfigurationInside2ScanPackage 调度方式）：

 1、使用注解：@SpringBootApplication、@EnableEurekaClient；
 2、使用注解：@RibbonClient(name = "springms-provider-user", configuration = TestConfigurationInside2ScanPackage.class)
 3、使用注解：@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeFromComponentScan.class) })
 3、TestConfigurationInside2ScanPackage 类中采用 RandomRule 随机调度算法；
 4、在 MovieCustomRibbonController 里面添加 test 方法来做测试；
 5、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 6、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；
 7、启动 springms-consumer-movie-ribbon-custom 模块服务；

 8、在浏览器输入地址http://localhost:8020/choose，然后看看 springms-provider-user、springms-provider-user2 的各个对应的服务打印的信息是否均匀，正常情况下应该是 springms-provider-user 随机分配，springms-provider-user2 轮询分配；

 总结：springms-provider-user（之所以随机是因为使用了 RibbonClient 配置采用 RandomRule 随机调度算法）、springms-provider-user2（之所以轮询是因为没有任何配置，默认调度算法就是轮询算法）；
 ****************************************************************************************/







/****************************************************************************************
 七、电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略（测试自定义分配服务器地址 TestConfigurationInsideScanPackage 调度方式）：

 1、注解：@RibbonClient(name = "springms-provider-user", configuration = TestConfigurationInsideScanPackage.class)
 2、注解：@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeFromComponentScan.class) })
 3、TestConfigurationInsideScanPackage 类中采用 RoundRobinRule 轮询调度算法；
 4、在 MovieCustomRibbonController 里面添加 test 方法来做测试；
 5、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 6、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；
 7、启动 springms-consumer-movie-ribbon-custom 模块服务；
 8、在浏览器输入地址http://localhost:8020/choose，然后看看 springms-provider-user、springms-provider-user2 的两个端口的服务打印的信息是否均匀，正常情况下都是轮询打印；

 总结：springms-provider-user（之所以随机是因为使用了 RibbonClient 配置采用 RoundRobinRule 轮询调度算法）、springms-provider-user2（之所以轮询是因为没有任何配置，默认调度算法就是轮询算法）；
 ****************************************************************************************/







/****************************************************************************************
 八、电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略（测试自定义分配服务器地址 TestConfigurationInsideScanPackage 调度方式）：

 1、注解：@RibbonClient(name = "springms-provider-user", configuration = TestConfigurationInsideScanPackage.class)
 2、注解：@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeFromComponentScan.class) })
 3、TestConfigurationInsideScanPackage 类中采用 RandomRule 随机调度算法；
 4、在 MovieCustomRibbonController 里面添加 test 方法来做测试；
 5、启动 springms-provider-user 模块服务，启动3个端口（7900、7899、7898）；
 6、启动 springms-provider-user2 模块服务，启动2个端口（7997、7996）（直接将用户微服务 spring.application.name 改了个名字为 springms-provider-user2 再启动而已）；
 7、启动 springms-consumer-movie-ribbon-custom 模块服务；
 8、在浏览器输入地址http://localhost:8020/choose，然后看看 springms-provider-user、springms-provider-user2 的两个端口的服务打印的信息是否均匀，正常情况下应该是 springms-provider-user 随机分配，springms-provider-user2 轮询分配；

 总结：springms-provider-user（之所以随机是因为使用了 RibbonClient 配置采用 RandomRule 随机调度算法）、springms-provider-user2（之所以轮询是因为没有任何配置，默认调度算法就是轮询算法）；
 ****************************************************************************************/

























