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
