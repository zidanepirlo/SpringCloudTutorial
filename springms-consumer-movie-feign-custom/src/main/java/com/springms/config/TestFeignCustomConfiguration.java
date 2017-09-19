package com.springms.config;

import feign.Contract;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义配置。
 *
 * 不和 com.springms.cloud 在同级目录，因为文档有说明，该配置文件不需要被扫描到。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/19
 *
 */
@Configuration
public class TestFeignCustomConfiguration {

    @Bean
    public Contract feignContract(){
        return new feign.Contract.Default();
    }

    /**
     * 日志级别配置
     *
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
