# SpringCloud（第 006 篇）电影微服务，使用 Ribbon 在客户端进行负载均衡
-

## 一、大致介绍

``` 
1、Ribbon 是 Netflix 发布的云中间层服务开源项目，主要功能是提供客户端负载均衡算法。
2、Ribbon 客户端组件提供一系列完善的配置项，如，连接超时，重试等。简单的说，Ribbon是一个客户端负载均衡器，我们可以在配置文件中列出load Balancer后面所有的机器，Ribbon会自动的帮助你基于某种规则(如简单轮询，随机连接等)去连接这些机器，我们也很容易使用Ribbon实现自定义的负载均衡算法。
3、Ribbon 实现软负载均衡，核心有三点：
   * 服务发现，发现依赖服务的列表
   * 服务选择规则，在多个服务中如何选择一个有效服务
   * 服务监听，检测失效的服务，高效剔除失效服务

4、本章节仅仅只是简单使用 Ribbon 来达到客户端负载均衡选择后端微服务列表而已，深入了解 Ribbon 使用的话请继续期待后序有关 Ribbon 章节的更新。
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-consumer-movie-ribbon</artifactId>
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


### 2.2 添加应用配置文件（springms-consumer-movie-ribbon\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-consumer-movie-ribbon
server:
  port: 8010
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

### 2.3 添加实体用户类User（springms-consumer-movie-ribbon\src\main\java\com\springms\cloud\entity\User.java）
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

### 2.4 添加电影Web访问层Controller（springms-consumer-movie-ribbon\src\main\java\com\springms\cloud\controller\MovieRibbonController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 电影微服务Ribbon的Web控制层，使用采用 LoadBalanced 注解后的 restTemplate 进行负载均衡调度不同后端微服务；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@RestController
public class MovieRibbonController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        // http://localhost:7900/simple/
        // VIP：virtual IP
        // HAProxy Heartbeat

        return this.restTemplate.getForObject("http://springms-provider-user/simple/" + id, User.class);
    }

    /**
     * 添加给 springms-sidecar 微服务做测试用的代码。
     *
     * @return
     */
    @GetMapping("/sidecar")
    public String sidecar() {
        return this.restTemplate.getForObject("http://springms-sidecar/", String.class);
    }

    /**
     * 添加给 springms-sidecar 微服务做测试用的代码。
     *
     * @return
     */
    @GetMapping("/sidecar/health.json")
    public String sidecarHealth() {
        return this.restTemplate.getForObject("http://springms-sidecar/health.json", String.class);
    }
}

```


### 2.5 添加电影微服务启动类（springms-consumer-movie-ribbon\src\main\java\com\springms\cloud\MsConsumerMovieRibbonApplication.java）
``` 
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

```


## 三、Ribbon 简述流程分析

``` 
 Ribbon的负载均衡，主要通过LoadBalancerClient来实现的，而LoadBalancerClient具体交给了ILoadBalancer来处理，
 ILoadBalancer通过配置IRule、IPing等信息，并向EurekaClient获取注册列表的信息，并默认10秒一次向EurekaClient发送“ping”,
 进而检查是否更新服务列表，最后，得到注册列表后，ILoadBalancer根据IRule的策略进行负载均衡。

 而RestTemplate 被@LoadBalance注解后，能过用负载均衡，主要是维护了一个被@LoadBalance注解的RestTemplate列表，并给列表中的RestTemplate添加拦截器，进而交给负载均衡器去处理。
```


## 四、测试

``` 
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
```





## 五、下载地址

<font color=#4183C4 size=4>[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)</font>

<font color=#4183C4 size=4>SpringCloudTutorial交流QQ群: 235322432</font>、<font color=#4183C4 size=4>[微信沟通交流群](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)</font>

<font color=red size=4>欢迎关注，您的肯定是对我最大的支持!!!</font>




























