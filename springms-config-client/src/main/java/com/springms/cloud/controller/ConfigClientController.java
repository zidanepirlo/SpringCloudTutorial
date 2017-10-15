package com.springms.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置客户端Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/15
 *
 */
@RestController
public class ConfigClientController {

    @Value("${profile}")
    private String profile;

    @GetMapping("/profile")
    public String getProfile(){
        return this.profile;
    }
}
