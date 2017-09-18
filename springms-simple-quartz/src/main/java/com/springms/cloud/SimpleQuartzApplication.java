package com.springms.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.concurrent.TimeUnit;

/**
 * 简单 Quartz 微服务，不支持分布式。
 *
 * 其实若只是简单的实现任务调用而言的话，SpringBoot 的 Schedule 这个注解即可满足需求，但是注意该注解不支持分布式；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/18
 *
 */
@ComponentScan
@Configuration
@ImportResource("applicationContext.xml")
public class SimpleQuartzApplication {
	
	private static final Logger Logger = LoggerFactory.getLogger(SimpleQuartzApplication.class);
	
	@Value("${server.port}")
	private int port;
	@Value("${server.sessionTimeout}")
	private int sessionTimeout;	

	public static void main(String[] args) {
		Logger.info("简单Quartz微服务入口函数编码-" +System.getProperty("file.encoding"));
				
		SpringApplication.run(SimpleQuartzApplication.class, args);

		System.out.println("【【【【【【 简单Quartz微服务 】】】】】】已启动.");
	}
	
	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
	    TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
	    factory.setPort(port);
	    factory.setSessionTimeout(sessionTimeout, TimeUnit.SECONDS);
	    return factory;
	}
}





/****************************************************************************************
 一、简单Quartz微服务：

 1、添加 Quartz 相关配置文件；
 2、启动 springms-simple-quartz 模块服务，启动1个端口；
 3、然后查看日志， TestTask 类的日志不断被定时打印出来；

 总结：其实若只是简单的实现任务调用而言的话，SpringBoot 的 Schedule 这个注解即可满足需求，但是注意该注解不支持分布式；
 ****************************************************************************************/


