package com.springms.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 认证配置，由于 UserFeignCustomSecondClient 访问 http://localhost:8761/ 需要密码登录，所以才有了此配置的出现。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/19
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
}
