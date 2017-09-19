package com.springms.cloud.feign;

import com.springms.cloud.entity.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Http请求的客户端。
 *
 * 注解FeignClient的传参：表示的是注册到 Eureka 服务上的模块名称，也就是需要访问的微服务名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/19
 *
 */
@FeignClient("springms-provider-user")
public interface UserFeignClient {

    /**
     * 这里有两个坑需要注意：
     *
     * 1、这里需要设置请求的方式为 RequestMapping 注解，用 GetMapping 注解是运行不成功的，即 GetMapping 不支持。
     *
     * 2、注解 PathVariable 里面需要填充变量的名字，不然也是运行不成功的。
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/simple/{id}", method = RequestMethod.GET)
    public User findById(@PathVariable("id") Long id);

    /**
     * 这里也有一个坑需要注意：
     *
     * 如果入参是一个对象的话，那么这个方法在 feign 里面默认为 POST 方法，就算你写成 GET 方式也无济于事。
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User postUser(@RequestBody User user);
}
