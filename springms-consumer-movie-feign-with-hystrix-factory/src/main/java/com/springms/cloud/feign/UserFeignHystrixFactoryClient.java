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



