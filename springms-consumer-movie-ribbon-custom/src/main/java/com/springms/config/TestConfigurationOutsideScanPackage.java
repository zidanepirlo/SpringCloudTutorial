package com.springms.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试定制 Ribbon ,而且该定制的配置文件是不在应用扫描的目录里面，也就是说应用启动后该文件不会被扫描到。
 *
 * RibbonClient 中的 name 名称，一定要是 eureka 服务中注册的名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@Configuration
public class TestConfigurationOutsideScanPackage {

//    @Autowired
//    IClientConfig config;
//
//    /**
//     *
//     * 添加这个 Bean 的注解，主要是因为定义 config 的时候报错，也就是说明 config 没有被实例化。
//     *
//     */
//    @Bean
//    public IClientConfig config(){
//        return new DefaultClientConfigImpl();
//    }

    /**
     * 采用随机分配的策略。
     *
     * @return
     */
    @Bean
    public IRule ribbonRule(){
        return new RandomRule();
    }
}
