package com.springms.config;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 认证配置，由于 UserFeignCustomSecondClient 访问 http://localhost:8761/ 需要密码登录，所以才有了此配置的出现。
 *
 * 不和 com.springms.cloud 在同级目录，因为文档有说明，不要被扫描到即可。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@Configuration
public class TestEurekaAuthConfiguration {

    /**
     * 此方法主要配置登录 Eureka 服务器的帐号与密码。
     *
     * @return
     */
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(){
        return new BasicAuthRequestInterceptor("admin", "admin");
    }

    /**
     * 在该配置中，加入这个方法的话，表明使用了该配置的地方，就会禁用该模块使用 Hystrix 容灾降级的功能；
     *
     * @return
     */
    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder(){
        return Feign.builder();
    }
}
