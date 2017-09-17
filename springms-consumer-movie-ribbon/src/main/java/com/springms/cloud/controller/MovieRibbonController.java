package com.springms.cloud.controller;

import com.springms.cloud.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 电影微服务Ribbon的Web控制层，使用采用 LoadBalanced 注解后的 restTemplate 进行负载均衡调度不同后端微服务；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@RestController
public class MovieRibbonController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        // http://localhost:7900/simple/
        // VIP：virtual IP
        // HAProxy Heartbeat

        return this.restTemplate.getForObject("http://springms-provider-user/simple/" + id, User.class);
    }

    /**
     * 添加给 springms-sidecar 微服务做测试用的代码。
     *
     * @return
     */
    @GetMapping("/sidecar")
    public String sidecar() {
        return this.restTemplate.getForObject("http://springms-sidecar/", String.class);
    }

    /**
     * 添加给 springms-sidecar 微服务做测试用的代码。
     *
     * @return
     */
    @GetMapping("/sidecar/health.json")
    public String sidecarHealth() {
        return this.restTemplate.getForObject("http://springms-sidecar/health.json", String.class);
    }
}
