package com.springms.cloud.feign;

import com.springms.config.TestEurekaAuthConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户Http请求的客户端，FeignClient 注解地方采用了自定义的配置。
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
@FeignClient(name = "xxx", url = "http://localhost:8761/", configuration = TestEurekaAuthConfiguration.class, fallback = UserFeignCustomSecondClientFallback.class)
public interface UserFeignCustomSecondClient {

    @RequestMapping(value = "/eureka/apps/{serviceName}")
    public String findEurekaInfo(@PathVariable("serviceName") String serviceName);
}
