package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 注解式Schedule配置定时任务，不支持任务调度。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@SpringBootApplication
@EnableScheduling
public class MsScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsScheduleApplication.class, args);
		System.out.println("【【【【【【 Schedule定时任务微服务 】】】】】】已启动.");
	}
}



/****************************************************************************************
 一、注解式Schedule配置定时任务，不支持任务调度（正常测试）：

 1、添加注解 EnableScheduling、Scheduled 以及任务类上注解 Component ；
 2、启动 springms-schedule 模块服务，启动1个端口；
 3、然后会看到“打印当前时间”等字样的内容被打印出来，说明定时任务生效了；
 ****************************************************************************************/






/****************************************************************************************
 二、注解式Schedule配置定时任务，不支持任务调度（测试是否可以调度？）：

 1、添加注解 EnableScheduling、Scheduled 以及任务类上注解 Component ；
 2、启动 springms-schedule 模块服务，启动3个端口（8340、8341、8342）；
 3、然后会看到3台服务都会“打印当前时间”等字样的内容被打印出来，说明定时任务生效了；

 总结：同一时刻3台服务都会打印日志，说明不支持任务调度的；
 ****************************************************************************************/







