package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 简单用户微服务类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/17
 *
 */
@SpringBootApplication
public class MsSimpleProviderUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSimpleProviderUserApplication.class, args);
		System.out.println("【【【【【【 简单用户微服务 】】】】】】已启动.");
	}
}




/****************************************************************************************
 一、简单用户微服务接口测试：

 1、启动 springms-simple-provider-user 模块服务，启动1个端口；
 2、在浏览器输入地址 http://localhost:8000/simple/1 可以看到信息成功的被打印出来。；
 ****************************************************************************************/

