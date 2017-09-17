# SpringCloud（第 007 篇）电影微服务，使用定制化 Ribbon 在客户端进行负载均衡，使用 RibbonClient 不同服务不同配置策略
-

## 一、大致介绍

``` 
1、通过 RibbonClient 注解来设置随机调度算法方式；
2、通过 restTemplate.getForObject、loadBalancerClient.choose 两种代码调用方式来测试客户端负载均衡算法；

```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-consumer-movie-ribbon-custom</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
	
    <parent>
		<groupId>com.springms.cloud</groupId>
		<artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
	
	<dependencies>
        <!-- web模块 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

        <!-- 客户端发现模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>

        <!-- 监控和管理生产环境的模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

</project>


```


### 2.2 添加应用配置文件（springms-consumer-movie-ribbon-custom\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-consumer-movie-ribbon-custom
server:
  port: 8020
#做负载均衡的时候，不需要这个动态配置的地址
#user:
#  userServicePath: http://localhost:7900/simple/
eureka:
  client:
#    healthcheck:
#      enabled: true
    serviceUrl:
      defaultZone: http://admin:admin@localhost:8761/eureka
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}


```

### 2.3 添加实体用户类User（springms-consumer-movie-ribbon-custom\src\main\java\com\springms\cloud\entity\User.java）
``` 
package com.springms.cloud.entity;

import java.math.BigDecimal;

public class User {

    private Long id;

    private String username;

    private String name;

    private Short age;

    private BigDecimal balance;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getAge() {
        return this.age;
    }

    public void setAge(Short age) {
        this.age = age;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}

```


### 2.4 添加 RibbonClient 配置类01（springms-consumer-movie-ribbon-custom\src\main\java\com\springms\cloud\config\TestConfigurationInside2ScanPackage.java）
``` 
package com.springms.cloud.config;

import com.netflix.loadbalancer.RoundRobinRule;
import com.springms.cloud.ExcludeFromComponentScan;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试定制 Ribbon ,而且该定制的配置文件是在应用扫描的目录里面，也就是说应用启动后该文件会被扫描到。
 *
 * RibbonClient 中的 name 名称，一定要是 eureka 服务中注册的名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@Configuration
@ExcludeFromComponentScan
public class TestConfigurationInside2ScanPackage {

    /**
     * 采用随机分配的策略。
     *
     * @return
     */
    @Bean
    public IRule ribbonRule(){
        return new RandomRule();
        //return new RoundRobinRule();
    }
}


```

### 2.5 添加 RibbonClient 配置类02（springms-consumer-movie-ribbon-custom\src\main\java\com\springms\config\TestConfigurationOutsideScanPackage.java）
``` 
package com.springms.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试定制 Ribbon ,而且该定制的配置文件是不在应用扫描的目录里面，也就是说应用启动后该文件不会被扫描到。
 *
 * RibbonClient 中的 name 名称，一定要是 eureka 服务中注册的名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@Configuration
public class TestConfigurationOutsideScanPackage {

//    @Autowired
//    IClientConfig config;
//
//    /**
//     *
//     * 添加这个 Bean 的注解，主要是因为定义 config 的时候报错，也就是说明 config 没有被实例化。
//     *
//     */
//    @Bean
//    public IClientConfig config(){
//        return new DefaultClientConfigImpl();
//    }

    /**
     * 采用随机分配的策略。
     *
     * @return
     */
    @Bean
    public IRule ribbonRule(){
        return new RandomRule();
    }
}


```


### 2.6 添加 RibbonClient 配置类03（springms-consumer-movie-ribbon-custom\src\main\java\com\springms\cloud\TestConfigurationInsideScanPackage.java）
``` 
package com.springms.cloud;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试定制 Ribbon ,而且该定制的配置文件是在应用扫描的目录里面，也就是说应用启动后该文件会被扫描到。
 *
 * RibbonClient 中的 name 名称，一定要是 eureka 服务中注册的名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@Configuration
@ExcludeFromComponentScan
public class TestConfigurationInsideScanPackage {

    /**
     * 采用随机分配的策略。
     *
     * @return
     */
    @Bean
    public IRule ribbonRule(){
        // return new RoundRobinRule();
        return new RandomRule();
    }
}

```



### 2.7 添加注解，目的就是让应用启动后，含有该注解的文件不会被应用扫描到（springms-consumer-movie-ribbon-custom\src\main\java\com\springms\cloud\ExcludeFromComponentScan.java）
``` 
package com.springms.cloud;

/**
 * 添加该注解，目的就是让应用启动后，含有该注解的文件不会被应用扫描到。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
public @interface ExcludeFromComponentScan {

}


```


### 2.8 添加Web访问层Controller（springms-consumer-movie-ribbon-custom\src\main\java\com\springms\cloud\controller\MovieCustomRibbonController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MovieCustomRibbonController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        // http://localhost:7900/simple/
        // VIP：virtual IP
        // HAProxy Heartbeat

        return this.restTemplate.getForObject("http://springms-provider-user/simple/" + id, User.class);
    }

    @GetMapping("/choose")
    public String test() {
        ServiceInstance serviceInstance = this.loadBalancerClient.choose("springms-provider-user");
        System.out.println("00000" + ":" + serviceInstance.getServiceId() + ":" + serviceInstance.getHost() + ":" + serviceInstance.getPort());

        ServiceInstance serviceInstance2 = this.loadBalancerClient.choose("springms-provider-user2");
        System.out.println("222222222222222222" + ":" + serviceInstance2.getServiceId() + ":" + serviceInstance2.getHost() + ":" + serviceInstance2.getPort());

        return "choose successful";
    }
}


```


### 2.9 添加电影微服务启动类（springms-consumer-movie-ribbon-custom\src\main\java\com\springms\cloud\MsConsumerMovieCustomRibbonApplication.java）
``` 
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

```


## 三、测试

``` 
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
```





## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

欢迎关注，您的肯定是对我最大的支持!!!
```




























