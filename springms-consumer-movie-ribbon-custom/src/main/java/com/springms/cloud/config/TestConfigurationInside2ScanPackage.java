package com.springms.cloud.config;

import com.netflix.loadbalancer.RoundRobinRule;
import com.springms.cloud.ExcludeFromComponentScan;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试定制 Ribbon ,而且该定制的配置文件是在应用扫描的目录里面，也就是说应用启动后该文件会被扫描到。
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
@ExcludeFromComponentScan
public class TestConfigurationInside2ScanPackage {

    /**
     * 采用随机分配的策略。
     *
     * @return
     */
    @Bean
    public IRule ribbonRule(){
        return new RandomRule();
        //return new RoundRobinRule();
    }
}
