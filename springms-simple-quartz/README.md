# SpringCloud（第 009 篇）简单 Quartz 微服务，不支持分布式
-

## 一、大致介绍

``` 
1、本章节仅仅只是为了测试 Quartz 在微服务中的使用情况；
2、其实若只是简单的实现任务调用而言的话，SpringBoot 的 Schedule 这个注解即可满足需求，但是注意该注解不支持分布式；

3、注意：配置文件中的 mysql 数据库链接配置大家就各自配置自己的哈；
```

## 二、实现步骤

### 2.1 添加 maven 引用包
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

	<artifactId>springms-simple-quartz</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
	
    <parent>
        <groupId>com.springms.cloud</groupId>
        <artifactId>springms-spring-cloud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
	
	<dependencies>
        <!-- 访问数据库模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- web模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- MYSQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
               
        <!-- quartz模块 -->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
            <version>2.3.0</version>
        </dependency>
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz-jobs</artifactId>
            <version>2.3.0</version>
        </dependency>
    	<dependency>
    		<groupId>org.springframework</groupId>
    		<artifactId>spring-context-support</artifactId>
		</dependency>
    </dependencies>

</project>

```


### 2.2 添加应用配置文件（springms-simple-quartz/src/main/resources/application.properties）
``` 
################################################################################
# mysql com.springms.cloud-test
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://ip:port/hmilyylimh
spring.datasource.username=username
spring.datasource.password=password
spring.datasource.jpa.hibernate.ddl-auto=update
spring.datasource.jpa.show-sql=true

################################################################################
# embedded servlet container
server.port=8390
# sessionTimeout in seconds
server.sessionTimeout=30000

```

### 2.3 添加Spring上下文配置文件（springms-simple-quartz/src/main/resources/applicationContext.xml）
``` 
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:task="http://www.springframework.org/schema/task"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-3.0.xsd">
	
	<bean id="properties"
		class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="locations">
			<list>
				<value>classpath:application.properties</value>
			</list>
		</property>
		<property name="ignoreUnresolvablePlaceholders" value="true" />
	</bean>
	
	<!-- 使用MethodInvokingJobDetailFactoryBean，任务类可以不实现Job接口，通过targetMethod指定调用方法-->
	<bean id="taskJob" class="com.springms.cloud.task.TestTask"/>
	<bean id="jobDetail" class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
    	<property name="group" value="job_work"/>
    	<property name="name" value="job_work_name"/>
    	<!--false表示等上一个任务执行完后再开启新的任务-->
    	<property name="concurrent" value="false"/>
    	<property name="targetObject">
        	<ref bean="taskJob"/>
   	 	</property>
    	<property name="targetMethod">
        	<value>run</value>
    	</property>
	</bean>

	<!--  调度触发器 -->
	<bean id="myTrigger"
      	class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
    	<property name="name" value="work_default_name"/>
    	<property name="group" value="work_default"/>
    	<property name="jobDetail">
        	<ref bean="jobDetail" />
    	</property>
    	<property name="cronExpression">
        	<value>0/10 * * * * ?</value>
    	</property>
	</bean>

	<!-- 调度工厂 -->
	<bean id="scheduler" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
    	<property name="triggers">
        	<list>
            	<ref bean="myTrigger"/>
        	</list>
    	</property>
	</bean>
	
	<bean id="springContextUtil" class="com.springms.cloud.util.SpringApplicationContextUtil"/>

</beans>

```



### 2.4 添加任务调度对象类（springms-simple-quartz/src/main/java/com/springms/cloud/domain/ScheduleJob.java）
``` 
package com.springms.cloud.domain;

/**
 * 任务调度对象。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/18
 *
 */
public class ScheduleJob {
	
	private String jobName;
	private String jobGroup;
	private String desc;
	private String jobStatus;
	private String cronExpression;
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
}

```




### 2.5 添加QuartzJobFactory（springms-simple-quartz/src/main/java/com/springms/cloud/service/QuartzJobFactory.java）
``` 
package com.springms.cloud.service;

import com.springms.cloud.domain.ScheduleJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class QuartzJobFactory implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("任务成功运行");
        ScheduleJob scheduleJob = (ScheduleJob)context.getMergedJobDataMap().get("scheduleJob");
        System.out.println("任务名称 = [" + scheduleJob.getJobName() + "]");
	}
}

```



### 2.6 添加定时任务服务（springms-simple-quartz/src/main/java/com/springms/cloud/service/ScheduleJobService.java）
``` 
package com.springms.cloud.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.springms.cloud.task.TestTask;
import com.springms.cloud.util.SpringApplicationContextUtil;
import com.springms.cloud.domain.ScheduleJob;
import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 定时任务服务。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/18
 *
 */
public class ScheduleJobService {
	
	private static final Logger Logger = LoggerFactory.getLogger(TestTask.class);
	
	public void getScheduleJob(){
		 
		try {
			SchedulerFactoryBean schedulerFactoryBean = SpringApplicationContextUtil.getBean("scheduler");
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
			Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);		
			List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
			for (JobKey jobKey : jobKeys) {
			    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			    for (Trigger trigger : triggers) {
			        ScheduleJob job = new ScheduleJob();
			        job.setJobName(jobKey.getName());
			        job.setJobGroup(jobKey.getGroup());
			        job.setDesc("触发器:" + trigger.getKey());
			        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			        job.setJobStatus(triggerState.name());
			        if (trigger instanceof CronTrigger) {
			            CronTrigger cronTrigger = (CronTrigger) trigger;
			            String cronExpression = cronTrigger.getCronExpression();
			            job.setCronExpression(cronExpression);
			        }
			        jobList.add(job);			        			       
			    }
			}
			
			for (ScheduleJob job : jobList) {
				Logger.info("计划列表,name:{},group:{},desc:{},status:{}",job.getJobName(),job.getJobGroup(),job.getDesc(),job.getJobStatus());
			}
			
		} catch (SchedulerException e) {
			Logger.error("SchedulerException", e);
		}
	}
}

```




### 2.7 添加测试任务类（springms-simple-quartz/src/main/java/com/springms/cloud/task/TestTask.java）
``` 
package com.springms.cloud.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试任务类（被任务调度后执行该任务类）。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/18
 *
 */
public class TestTask {
	
	/** 日志对象 */
    private static final Logger LOG = LoggerFactory.getLogger(TestTask.class);
    
    public void run() {
        if (LOG.isInfoEnabled()) {
            LOG.info("测试任务线程开始执行");
            
            //new ScheduleJobService().getScheduleJob();
        }
    }

}
```


### 2.8 添加 Spring 上下文工具类（springms-simple-quartz/src/main/java/com/springms/cloud/util/SpringApplicationContextUtil.java）
``` 
package com.springms.cloud.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/18
 *
 */
@Component
public class SpringApplicationContextUtil implements ApplicationContextAware{
	
	// 声明一个静态变量保存   
	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringApplicationContextUtil.applicationContext=applicationContext;
	}
	
	public static ApplicationContext getContext(){
		
		return applicationContext;   
	}  
	
	@SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
               return (T) applicationContext.getBean(name);
     }

}

```


### 2.9 添加 Quartz 启动类（springms-simple-quartz/src/main/java/com/springms/cloud/SimpleQuartzApplication.java）
``` 
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

```



## 三、测试

``` 
/****************************************************************************************
 一、简单Quartz微服务：

 1、添加 Quartz 相关配置文件；
 2、启动 springms-simple-quartz 模块服务，启动1个端口；
 3、然后查看日志， TestTask 类的日志不断被定时打印出来；

 总结：其实若只是简单的实现任务调用而言的话，SpringBoot 的 Schedule 这个注解即可满足需求，但是注意该注解不支持分布式；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

欢迎关注，您的肯定是对我最大的支持!!!
```






























