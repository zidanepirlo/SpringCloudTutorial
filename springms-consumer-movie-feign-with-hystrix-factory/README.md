# SpringCloud（第 017 篇）电影微服务接入Feign，添加 fallbackFactory 属性来触发请求进行容灾降级
-

## 一、大致介绍

``` 
1、在一些场景中，简单的触发在 FeignClient 加入 Fallback 属性即可，而另外有一些场景需要访问导致回退触发的原因，那么这个时候可以在 FeignClient 中加入 FallbackFactory 属性即可；
2、而在使用 Fallback 和 FallbackFactory 时候，我仅仅表述个人观点，发现二者混合一起用的话，会发生冲突情况，所以大家用的时候注重考虑一下场景，二者属性用其一即可。
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springms-consumer-movie-feign-with-hystrix-factory</artifactId>
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

        <!-- Java HTTP 客户端开发的工具的模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-feign</artifactId>
        </dependency>

        <!-- Hystrix 模块 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix</artifactId>
        </dependency>
    </dependencies>

</project>
```


### 2.2 添加应用配置文件（springms-consumer-movie-feign-with-hystrix-factory\src\main\resources\application.yml）
``` 
spring:
  application:
    name: springms-consumer-movie-feign-with-hystrix-factory
server:
  port: 8115
eureka:
  client:
#    healthcheck:
#      enabled: true
    serviceUrl:
      defaultZone: http://admin:admin@localhost:8761/eureka
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}


# 解决第一次请求报超时异常的方案，因为 hystrix 的默认超时时间是 1 秒，因此请求超过该时间后，就会出现页面超时显示 ：
#
# 这里就介绍大概三种方式来解决超时的问题，解决方案如下：
#
# 第一种方式：将 hystrix 的超时时间设置成 5000 毫秒
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 5000
#
# 或者：
# 第二种方式：将 hystrix 的超时时间直接禁用掉，这样就没有超时的一说了，因为永远也不会超时了
# hystrix.command.default.execution.timeout.enabled: false
#
# 或者：
# 第三种方式：索性禁用feign的hystrix支持
# feign.hystrix.enabled: false ## 索性禁用feign的hystrix支持

# 超时的issue：https://github.com/spring-cloud/spring-cloud-netflix/issues/768
# 超时的解决方案： http://stackoverflow.com/questions/27375557/hystrix-command-fails-with-timed-out-and-no-fallback-available
# hystrix配置： https://github.com/Netflix/Hystrix/wiki/Configuration#execution.isolation.thread.timeoutInMilliseconds
```

### 2.3 添加实体用户类User（springms-consumer-movie-feign-with-hystrix-factory\src\main\java\com\springms\cloud\entity\User.java）
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


### 2.4 添加访问远端用户微服务 Feign 客户端（springms-consumer-movie-feign-with-hystrix-factory\src\main\java\com\springms\cloud\feign\UserFeignHystrixFactoryClient.java）
``` 
package com.springms.cloud.feign;

import com.springms.cloud.entity.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Http请求的客户端。
 *
 * 注解FeignClient的传参：表示的是注册到 Eureka 服务上的模块名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@FeignClient(name = "springms-provider-user", /*fallback = HystrixClientFallback.class,*/ fallbackFactory = HystrixClientFallbackFactory.class)
public interface UserFeignHystrixFactoryClient {

    /**
     * 这里有两个坑需要注意：<br/>
     *
     * <li>这里需要设置请求的方式为 RequestMapping 注解，用 GetMapping 注解是运行不成功的，即 GetMapping 不支持。</li>
     * <li>注解 PathVariable 里面需要填充变量的名字，不然也是运行不成功的。</li>
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/simple/{id}", method = RequestMethod.GET)
    public User findById(@PathVariable("id") Long id);
}
```


### 2.5 添加访问远端用户微服务 Fallback 类（springms-consumer-movie-feign-with-hystrix-factory\src\main\java\com\springms\cloud\feign\HystrixClientFallback.java）
``` 
package com.springms.cloud.feign;

import com.springms.cloud.entity.User;
import org.springframework.stereotype.Component;

/**
 * Hystrix 客户端回退机制类。
 *
 * 这里加上注解 Component 的目的：就是因为没有这个注解，运行时候会报错，报错会说没有该类的这个实例，所以我们就想到要实例化这个类，因此加了这个注解。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@Component
public class HystrixClientFallback implements UserFeignHystrixFactoryClient {

    @Override
    public User findById(Long id) {

        System.out.println("======== findById Fallback " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());

        User tmpUser = new User();
        tmpUser.setId(0L);
        return tmpUser;
    }
}
```


### 2.6 添加访问远端用户微服务 FallbackFactory 类（springms-consumer-movie-feign-with-hystrix-factory\src\main\java\com\springms\cloud\feign\HystrixClientFallbackFactory.java）
``` 
package com.springms.cloud.feign;

import com.springms.cloud.entity.User;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Hystrix 客户端回退机制类。
 *
 * 这里加上注解 Component 的目的：就是因为没有这个注解，运行时候会报错，报错会说没有该类的这个实例，所以我们就想到要实例化这个类，因此加了这个注解。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@Component
public class HystrixClientFallbackFactory implements FallbackFactory<UserFeignHystrixFactoryClient> {

    private static final Logger Logger = LoggerFactory.getLogger(HystrixClientFallbackFactory.class);

    @Override
    public UserFeignHystrixFactoryClient create(Throwable e) {

        Logger.info("fallback; reason was: {}", e.getMessage());
        System.out.println("======== UserFeignHystrixFactoryClient.create " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());

        return new UserFeignWithFallBackFactoryClient(){

            @Override
            public User findById(Long id) {
                System.out.println("======== findById FallBackFactory " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());

                User tmpUser = new User();
                tmpUser.setId(-1L);
                return tmpUser;
            }
        };
    }
}

/****************************************************************************************
 @FeignClient(name = "hello", fallbackFactory = HystrixClientFallbackFactory.class)
 protected interface HystrixClient {
     @RequestMapping(method = RequestMethod.GET, value = "/hello")
     Hello iFailSometimes();
 }

 @Component
 static class HystrixClientFallbackFactory implements FallbackFactory<HystrixClient> {
     @Override
     public HystrixClient create(Throwable cause) {
        return new HystrixClientWithFallBackFactory() {
             @Override
             public Hello iFailSometimes() {
                return new Hello("fallback; reason was: " + cause.getMessage());
            }
        };
     }
 }
 ****************************************************************************************/
```

### 2.7 添加回退处理客户端类（springms-consumer-movie-feign-with-hystrix-factory\src\main\java\com\springms\cloud\feign\UserFeignWithFallBackFactoryClient.java）
``` 
package com.springms.cloud.feign;

/**
 * 回退处理客户端。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
public interface UserFeignWithFallBackFactoryClient extends UserFeignHystrixFactoryClient{
}
```




### 2.8 添加Web访问层Controller（springms-consumer-movie-feign-with-hystrix-factory\src\main\java\com\springms\cloud\controller\MovieFeignHystrixFactoryController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import com.springms.cloud.feign.UserFeignHystrixFactoryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieFeignHystrixFactoryController {

    @Autowired
    private UserFeignHystrixFactoryClient userFeignHystrixFactoryClient;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        System.out.println("======== findById Controller " + Thread.currentThread().getThreadGroup() + " - " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName());
        return userFeignHystrixFactoryClient.findById(id);
    }
}
```


### 2.9 添加电影微服务启动类（springms-consumer-movie-feign-custom-without-hystrix\src\main\java\com\springms\cloud\MsConsumerMovieFeignCustomWithoutHystrixApplication.java）
``` 
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
```


## 三、测试

``` 
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
```


## 四、下载地址

<font color=#4183C4 size=4>[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)</font>

<font color=#4183C4 size=4>SpringCloudTutorial交流QQ群: 235322432</font>、<font color=#4183C4 size=4>[微信沟通交流群](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)</font>

<font color=red size=4>欢迎关注，您的肯定是对我最大的支持!!!</font>






























