package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 简单电影微服务类（消费方，而提供方为用户微服务）。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@SpringBootApplication
public class MsSimpleConsumerMovieApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MsSimpleConsumerMovieApplication.class, args);
      System.out.println("【【【【【【 简单电影微服务 】】】】】】已启动.");
    }
}


/****************************************************************************************
 一、简单电影微服务类（消费方，而提供方为用户微服务）：

 1、启动 springms-simple-provider-user 模块服务，启动1个端口；
 2、启动 springms-simple-consumer-movie 模块服务，启动1个端口；
 3、在浏览器输入地址 http://localhost:8005/movie/1 可以看到信息成功的被打印出来；
 ****************************************************************************************/

