package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 注解式Async配置异步任务；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@SpringBootApplication
@EnableAsync
public class MsAsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAsyncApplication.class, args);
		System.out.println("【【【【【【 Async异步任务微服务 】】】】】】已启动.");
	}
}



/****************************************************************************************
 一、简单用户链接Mysql数据库微服务（Async实现异步调用）：

 1、添加注解 EnableAsync、Async 以及任务类上注解 Component ；
 2、启动 springms-async 模块服务，启动1个端口；
 3、然后在浏览器输入地址 http://localhost:8345/task 然后等待大约10多秒后，成功打印所有信息，一切正常；

 总结：说明 Async 异步任务配置生效了；
 ****************************************************************************************/







