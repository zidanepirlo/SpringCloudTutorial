# SpringCloud（第 010 篇）简单 Quartz-Cluster 微服务，支持集群分布式，并支持动态修改 Quartz 任务的 cronExpression 执行时间
-

## 一、大致介绍

``` 
1、根据上一章节的单台测试，本章节修改 Quartz 了支持分布式，因为这是更多的企业开发场景所需要的开发模式；
2、而且在定时任务执行的过程中，通过修改 Quartz 触发器表的 cronExpression 表达式值，从而达到动态修改定时任务的执行时间；

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

	<artifactId>springms-simple-quartz-cluster</artifactId>
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

        <!-- Jdbc 模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
               
        <!-- quartz 模块 -->
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

        <!-- druid 线程池模块 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.3</version>
        </dependency>
    </dependencies>

</project>

```


### 2.2 添加应用配置文件（springms-simple-quartz-cluster/src/main/resources/application.yml）
``` 
server:
  port: 8395
spring:
  application:
    name: springms-simple-quartz-cluster  #全部小写


#####################################################################################################
# mysql 属性配置
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://ip:port/hmilyylimh?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
    username: username
    password: password
  jpa:
    hibernate:
      #ddl-auto: create #ddl-auto:设为create表示每次都重新建表
      ddl-auto: update #ddl-auto:设为update表示每次都不会重新建表
    show-sql: true
#####################################################################################################




#####################################################################################################
#########mysql######### 注释先不用这些属性
#spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
#spring.datasource.driver-class-name=com.mysql.jdbc.Driver
#spring.datasource.url=jdbc:mysql://127.0.0.1:3306/test_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
#spring.datasource.username=test
#spring.datasource.password=test
#
#
#spring.datasource.initialSize=5
#spring.datasource.minIdle=5
#spring.datasource.maxActive=20
#spring.datasource.maxWait=60000
#
#
#spring.datasource.timeBetweenEvictionRunsMillis=3600000
#spring.datasource.minEvictableIdleTimeMillis=18000000
#
#
#spring.datasource.validationQuery=SELECT 1 FROM DUAL
#spring.datasource.testWhileIdle=true
#spring.datasource.testOnBorrow=true
#spring.datasource.testOnReturn=true
#spring.datasource.poolPreparedStatements=true
#spring.datasource.maxPoolPreparedStatementPerConnectionSize=20
#spring.datasource.filters=stat,wall,log4j
#spring.datasource.connectionProperties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
#####################################################################################################




#####################################################################################################
# 打印日志
logging:
  level:
    root: INFO
    org.hibernate: INFO
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    org.hibernate.type.descriptor.sql.BasicExtractor: TRACE
    com.springms: DEBUG
#####################################################################################################

```

### 2.3 添加 quartz 配置文件（springms-simple-quartz-cluster/src/main/resources/quartz.properties）
``` 
org.quartz.scheduler.instanceName = quartzScheduler  
org.quartz.scheduler.instanceId = AUTO  


org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX  
org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
org.quartz.jobStore.tablePrefix = QRTZ_  
org.quartz.jobStore.isClustered = true  
org.quartz.jobStore.useProperties = false
org.quartz.jobStore.clusterCheckinInterval = 20000    


org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool  
org.quartz.threadPool.threadCount = 10  
org.quartz.threadPool.threadPriority = 5  
org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread = true

```



### 2.4 添加 quartz 任务配置文件（springms-simple-quartz-cluster/src/main/resources/quartz.xml）
``` 
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
       xmlns:aop="http://www.springframework.org/schema/aop" xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
					http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd
          				http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
          				http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd">


    <bean id="testJobDetail" class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
        <!-- durability 表示任务完成之后是否依然保留到数据库，默认false -->
        <property name="durability" value="true" />
        <property name="requestsRecovery" value="true" />
        <property name="jobClass">
            <value>
                com.springms.cloud.job.DetailQuartzJobBean
            </value>
        </property>
        <property name="jobDataAsMap">
            <map>
                <entry key="targetObject" value="testScheduleTask" />
                <entry key="targetMethod" value="sayHello" />
                <!-- 是否允许任务并发执行。当值为false时，表示必须等到前一个线程处理完毕后才再启一个新的线程 -->
                <entry key="concurrent" value="false" />
            </map>
        </property>
    </bean>

    <bean id="testJobTrigger"
          class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
        <property name="jobDetail">
            <ref bean="testJobDetail" />
        </property>
        <property name="cronExpression">
            <value>0/10 * * * * ?</value><!--每10秒钟执行一次 -->
        </property>
    </bean>

    <bean id="startQuertz" lazy-init="false" autowire="no"
          class="org.springframework.scheduling.quartz.SchedulerFactoryBean"
          destroy-method="destroy">
        <!--QuartzScheduler 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了 -->
        <property name="overwriteExistingJobs" value="true" />
        <property name="startupDelay" value="2" />
        <property name="autoStartup" value="true" />
        <property name="triggers">
            <list>
                <ref bean="testJobTrigger" />
            </list>
        </property>
        <property name="dataSource" ref="dataSource" />
        <property name="applicationContextSchedulerContextKey" value="applicationContext" />
        <property name="configLocation" value="classpath:quartz.properties" />
    </bean>
</beans>

```


### 2.5 添加定时任务作业类（springms-simple-quartz-cluster/src/main/java/com/springms/cloud/job/DetailQuartzJobBean.java）
``` 
package com.springms.cloud.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务作业类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/18
 *
 */
public class DetailQuartzJobBean extends QuartzJobBean {

	private String targetObject;
	private String targetMethod;
	private ApplicationContext ctx;

	// 配置中设定了
	// ① targetMethod: 指定需要定时执行scheduleInfoAction中的simpleJobTest()方法
	// ② concurrent：对于相同的JobDetail，当指定多个Trigger时, 很可能第一个job完成之前，
	// 第二个job就开始了。指定concurrent设为false，多个job不会并发运行，第二个job将不会在第一个job完成之前开始。
	// ③ cronExpression：0/10 * * * * ?表示每10秒执行一次，具体可参考附表。
	// ④ triggers：通过再添加其他的ref元素可在list中放置多个触发器。 scheduleInfoAction中的simpleJobTest()方法
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			Object otargetObject = ctx.getBean(targetObject);
			Method m = null;

			System.out.println(targetObject + " - " + targetMethod + " - " + ((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date())));
			try {
				m = otargetObject.getClass().getMethod(targetMethod, new Class[] { JobExecutionContext.class });
				m.invoke(otargetObject, new Object[] { context });
			} catch (SecurityException e) {
				// Logger.error(e);
				System.out.println(e.getMessage());
			} catch (NoSuchMethodException e) {
				// Logger.error(e);
				System.out.println(e.getMessage());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new JobExecutionException(e);
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.ctx = applicationContext;
	}

	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}

	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}
}
```



### 2.6 添加调度服务接口（springms-simple-quartz-cluster/src/main/java/com/springms/cloud/service/ISchedulerService.java）
``` 
package com.springms.cloud.service;

import java.util.Date;
  
import org.quartz.CronExpression;  
  
/**
 * 调度服务接口。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/18
 *
 */
public interface ISchedulerService {
    
    /** 
     * 根据 Quartz Cron Expression 调试任务 
     *  
     * @param cronExpression 
     *            Quartz Cron 表达式，如 "0/10 * * ? * * *"等 
     */  
    void schedule(String cronExpression);  
  
    /** 
     * 根据 Quartz Cron Expression 调试任务 
     *  
     * @param name 
     *            Quartz CronTrigger名称 
     * @param cronExpression 
     *            Quartz Cron 表达式，如 "0/10 * * ? * * *"等 
     */  
    void schedule(String name, String cronExpression);  
  
    /** 
     * 根据 Quartz Cron Expression 调试任务 
     *  
     * @param name 
     *            Quartz CronTrigger名称 
     * @param group 
     *            Quartz CronTrigger组 
     * @param cronExpression 
     *            Quartz Cron 表达式，如 "0/10 * * ? * * *"等 
     */  
    void schedule(String name, String group, String cronExpression);  
  
    /** 
     * 根据 Quartz Cron Expression 调试任务 
     *  
     * @param cronExpression 
     *            Quartz CronExpression 
     */  
    void schedule(CronExpression cronExpression);  
  
    /** 
     * 根据 Quartz Cron Expression 调试任务 
     *  
     * @param name 
     *            Quartz CronTrigger名称 
     * @param cronExpression 
     *            Quartz CronExpression 
     */  
    void schedule(String name, CronExpression cronExpression);  
  
    /** 
     * 根据 Quartz Cron Expression 调试任务 
     *  
     * @param name 
     *            Quartz CronTrigger名称 
     * @param group 
     *            Quartz CronTrigger组 
     * @param cronExpression 
     *            Quartz CronExpression 
     */  
    void schedule(String name, String group, CronExpression cronExpression);  
  
    /** 
     * 在startTime时执行调试一次 
     *  
     * @param startTime 
     *            调度开始时间 
     */  
    void schedule(Date startTime);  
  
    void schedule(Date startTime, String group);  
  
    /** 
     * 在startTime时执行调试一次 
     *  
     * @param name 
     *            Quartz SimpleTrigger 名称 
     * @param startTime 
     *            调度开始时间 
     */  
    void schedule(String name, Date startTime);  
  
    void schedule(String name, Date startTime, String group);  
  
    /** 
     * 在startTime时执行调试，endTime结束执行调度 
     *  
     * @param startTime 
     *            调度开始时间 
     * @param endTime 
     *            调度结束时间 
     */  
    void schedule(Date startTime, Date endTime);  
  
    void schedule(Date startTime, Date endTime, String group);  
  
    /** 
     * 在startTime时执行调试，endTime结束执行调度 
     *  
     * @param name 
     *            Quartz SimpleTrigger 名称 
     * @param startTime 
     *            调度开始时间 
     * @param endTime 
     *            调度结束时间 
     */  
    void schedule(String name, Date startTime, Date endTime);  
  
    void schedule(String name, Date startTime, Date endTime, String group);  
  
    /** 
     * 在startTime时执行调试，endTime结束执行调度，重复执行repeatCount次 
     *  
     * @param startTime 
     *            调度开始时间 
     * @param repeatCount 
     *            重复执行次数 
     */  
    void schedule(Date startTime, int repeatCount);  
  
    /** 
     * 在startTime时执行调试，endTime结束执行调度，重复执行repeatCount次 
     *  
     * @param startTime 
     *            调度开始时间 
     * @param endTime 
     *            调度结束时间 
     * @param repeatCount 
     *            重复执行次数 
     */  
    void schedule(Date startTime, Date endTime, int repeatCount);  
  
    void schedule(Date startTime, Date endTime, int repeatCount, String group);  
  
    /** 
     * 在startTime时执行调试，endTime结束执行调度，重复执行repeatCount次 
     *  
     * @param name 
     *            Quartz SimpleTrigger 名称 
     * @param startTime 
     *            调度开始时间 
     * @param endTime 
     *            调度结束时间 
     * @param repeatCount 
     *            重复执行次数 
     */  
    void schedule(String name, Date startTime, Date endTime, int repeatCount);  
  
    void schedule(String name, Date startTime, Date endTime, int repeatCount, String group);  
  
    /** 
     * 在startTime时执行调试，endTime结束执行调度，重复执行repeatCount次，每隔repeatInterval秒执行一次 
     *  
     * @param startTime 
     *            调度开始时间 
     *  
     * @param repeatCount 
     *            重复执行次数 
     * @param repeatInterval 
     *            执行时间隔间 
     */  
    void schedule(Date startTime, int repeatCount, long repeatInterval);  
  
    /** 
     * 在startTime时执行调试，endTime结束执行调度，重复执行repeatCount次，每隔repeatInterval秒执行一次 
     *  
     * @param startTime 
     *            调度开始时间 
     * @param endTime 
     *            调度结束时间 
     * @param repeatCount 
     *            重复执行次数 
     * @param repeatInterval 
     *            执行时间隔间 
     */  
    void schedule(Date startTime, Date endTime, int repeatCount, long repeatInterval);  
  
    void schedule(Date startTime, Date endTime, int repeatCount, long repeatInterval, String group);  
  
    /** 
     * 在startTime时执行调试，endTime结束执行调度，重复执行repeatCount次，每隔repeatInterval秒执行一次 
     *  
     * @param name 
     *            Quartz SimpleTrigger 名称 
     * @param startTime 
     *            调度开始时间 
     * @param endTime 
     *            调度结束时间 
     * @param repeatCount 
     *            重复执行次数 
     * @param repeatInterval 
     *            执行时间隔间 
     */  
    void schedule(String name, Date startTime, Date endTime, int repeatCount, long repeatInterval);  
  
    void schedule(String name, Date startTime, Date endTime, int repeatCount, long repeatInterval, String group);  
  
    /** 
     * 暂停触发器 
     *  
     * @param triggerName 
     *            触发器名称 
     */  
    void pauseTrigger(String triggerName);  
  
    /** 
     * 暂停触发器 
     *  
     * @param triggerName 
     *            触发器名称 
     * @param group 
     *            触发器组 
     */  
    void pauseTrigger(String triggerName, String group);  
  
    /** 
     * 恢复触发器 
     *  
     * @param triggerName 
     *            触发器名称 
     */  
    void resumeTrigger(String triggerName);  
  
    /** 
     * 恢复触发器 
     *  
     * @param triggerName 
     *            触发器名称 
     * @param group 
     *            触发器组 
     */  
    void resumeTrigger(String triggerName, String group);  
  
    /** 
     * 删除触发器 
     *  
     * @param triggerName 
     *            触发器名称 
     * @return 
     */  
    boolean removeTrigdger(String triggerName);  
  
    /** 
     * 删除触发器 
     *  
     * @param triggerName 
     *            触发器名称 
     * @param group 
     *            触发器组 
     * @return 
     */  
    boolean removeTrigdger(String triggerName, String group);  
}  
```



### 2.7 添加调度服务实现类（springms-simple-quartz-cluster/src/main/java/com/springms/cloud/service/impl/SchedulerServiceImpl.java）
``` 
package com.springms.cloud.service.impl;

import java.text.ParseException;
import java.util.Date;  
import java.util.UUID;

import com.springms.cloud.service.ISchedulerService;
import org.quartz.CronExpression;
import org.quartz.JobDetail;  
import org.quartz.Scheduler;  
import org.quartz.SchedulerException;  
import org.quartz.TriggerKey;  
import org.quartz.impl.triggers.CronTriggerImpl;  
import org.quartz.impl.triggers.SimpleTriggerImpl;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Service;  

/**
 * 调度服务实现类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/18
 *
 */
@Service("schedulerService")  
public class SchedulerServiceImpl implements ISchedulerService {
  
    private static final String NULLSTRING = null;  
    private static final Date NULLDATE = null;  
  
    @Autowired  
    private Scheduler scheduler;  
    @Autowired  
    private JobDetail jobDetail;  
  
    @Override  
    public void schedule(String cronExpression) {  
        schedule(NULLSTRING, cronExpression);  
    }  
  
    @Override  
    public void schedule(String name, String cronExpression) {  
        schedule(name, NULLSTRING, cronExpression);  
    }  
  
    @Override  
    public void schedule(String name, String group, String cronExpression) {  
        try {  
            schedule(name, group, new CronExpression(cronExpression));  
        } catch (ParseException e) {  
            throw new IllegalArgumentException(e);  
        }  
    }  
  
    @Override  
    public void schedule(CronExpression cronExpression) {  
        schedule(NULLSTRING, cronExpression);  
    }  
  
    @Override  
    public void schedule(String name, CronExpression cronExpression) {  
        schedule(name, NULLSTRING, cronExpression);  
    }  
  
    @Override  
    public void schedule(String name, String group, CronExpression cronExpression) {  
  
        if (isValidExpression(cronExpression)) {  
  
            if (name == null || name.trim().equals("")) {  
                name = UUID.randomUUID().toString();  
            }  
  
            CronTriggerImpl trigger = new CronTriggerImpl();  
            trigger.setCronExpression(cronExpression);  
  
            TriggerKey triggerKey = new TriggerKey(name, group);  
  
            trigger.setJobName(jobDetail.getKey().getName());  
            trigger.setKey(triggerKey);  
  
            try {  
                scheduler.addJob(jobDetail, true);  
                if (scheduler.checkExists(triggerKey)) {  
                    scheduler.rescheduleJob(triggerKey, trigger);  
                } else {  
                    scheduler.scheduleJob(trigger);  
                }  
            } catch (SchedulerException e) {  
                throw new IllegalArgumentException(e);  
            }  
        }  
    }  
  
    @Override  
    public void schedule(Date startTime) {  
        schedule(startTime, NULLDATE);  
    }  
  
    @Override  
    public void schedule(Date startTime, String group) {  
        schedule(startTime, NULLDATE, group);  
    }  
  
    @Override  
    public void schedule(String name, Date startTime) {  
        schedule(name, startTime, NULLDATE);  
    }  
  
    @Override  
    public void schedule(String name, Date startTime, String group) {  
        schedule(name, startTime, NULLDATE, group);  
    }  
  
    @Override  
    public void schedule(Date startTime, Date endTime) {  
        schedule(startTime, endTime, 0);  
    }  
  
    @Override  
    public void schedule(Date startTime, Date endTime, String group) {  
        schedule(startTime, endTime, 0, group);  
    }  
  
    @Override  
    public void schedule(String name, Date startTime, Date endTime) {  
        schedule(name, startTime, endTime, 0);  
    }  
  
    @Override  
    public void schedule(String name, Date startTime, Date endTime, String group) {  
        schedule(name, startTime, endTime, 0, group);  
    }  
  
    @Override  
    public void schedule(Date startTime, int repeatCount) {  
        schedule(null, startTime, NULLDATE, 0);  
    }  
  
    @Override  
    public void schedule(Date startTime, Date endTime, int repeatCount) {  
        schedule(null, startTime, endTime, 0);  
    }  
  
    @Override  
    public void schedule(Date startTime, Date endTime, int repeatCount, String group) {  
        schedule(null, startTime, endTime, 0, group);  
    }  
  
    @Override  
    public void schedule(String name, Date startTime, Date endTime, int repeatCount) {  
        schedule(name, startTime, endTime, 0, 0L);  
    }  
  
    @Override  
    public void schedule(String name, Date startTime, Date endTime, int repeatCount, String group) {  
        schedule(name, startTime, endTime, 0, 0L, group);  
    }  
  
    @Override  
    public void schedule(Date startTime, int repeatCount, long repeatInterval) {  
        schedule(null, startTime, NULLDATE, repeatCount, repeatInterval);  
    }  
  
    @Override  
    public void schedule(Date startTime, Date endTime, int repeatCount, long repeatInterval) {  
        schedule(null, startTime, endTime, repeatCount, repeatInterval);  
    }  
  
    @Override  
    public void schedule(Date startTime, Date endTime, int repeatCount, long repeatInterval, String group) {  
        schedule(null, startTime, endTime, repeatCount, repeatInterval, group);  
    }  
  
    @Override  
    public void schedule(String name, Date startTime, Date endTime, int repeatCount, long repeatInterval) {  
        schedule(name, startTime, endTime, repeatCount, repeatInterval, NULLSTRING);  
    }  
  
    @Override  
    public void schedule(String name, Date startTime, Date endTime, int repeatCount, long repeatInterval, String group) {  
  
        if (this.isValidExpression(startTime)) {  
  
            if (name == null || name.trim().equals("")) {  
                name = UUID.randomUUID().toString();  
            }  
  
            TriggerKey triggerKey = new TriggerKey(name, group);  
  
            SimpleTriggerImpl trigger = new SimpleTriggerImpl();  
            trigger.setKey(triggerKey);  
            trigger.setJobName(jobDetail.getKey().getName());  
  
            trigger.setStartTime(startTime);  
            trigger.setEndTime(endTime);  
            trigger.setRepeatCount(repeatCount);  
            trigger.setRepeatInterval(repeatInterval);  
  
            try {  
                scheduler.addJob(jobDetail, true);  
                if (scheduler.checkExists(triggerKey)) {  
                    scheduler.rescheduleJob(triggerKey, trigger);  
                } else {  
                    scheduler.scheduleJob(trigger);  
                }  
            } catch (SchedulerException e) {  
                throw new IllegalArgumentException(e);  
            }  
        }  
    }  
  
    @Override  
    public void pauseTrigger(String triggerName) {  
        pauseTrigger(triggerName, NULLSTRING);  
    }  
  
    @Override  
    public void pauseTrigger(String triggerName, String group) {  
        try {  
            scheduler.pauseTrigger(new TriggerKey(triggerName, group));// 停止触发器  
        } catch (SchedulerException e) {  
            throw new RuntimeException(e);  
        }  
    }  
  
    @Override  
    public void resumeTrigger(String triggerName) {  
        resumeTrigger(triggerName, NULLSTRING);  
    }  
  
    @Override  
    public void resumeTrigger(String triggerName, String group) {  
        try {  
            scheduler.resumeTrigger(new TriggerKey(triggerName, group));// 重启触发器  
        } catch (SchedulerException e) {  
            throw new RuntimeException(e);  
        }  
    }  
  
    @Override  
    public boolean removeTrigdger(String triggerName) {  
        return removeTrigdger(triggerName, NULLSTRING);  
    }  
  
    @Override  
    public boolean removeTrigdger(String triggerName, String group) {  
        TriggerKey triggerKey = new TriggerKey(triggerName, group);  
        try {  
            scheduler.pauseTrigger(triggerKey);// 停止触发器  
            return scheduler.unscheduleJob(triggerKey);// 移除触发器  
        } catch (SchedulerException e) {  
            throw new RuntimeException(e);  
        }  
    }  
  
    private boolean isValidExpression(final CronExpression cronExpression) {  
  
        CronTriggerImpl trigger = new CronTriggerImpl();  
        trigger.setCronExpression(cronExpression);  
  
        Date date = trigger.computeFirstFireTime(null);  
  
        return date != null && date.after(new Date());  
    }  
  
    private boolean isValidExpression(final Date startTime) {  
  
        SimpleTriggerImpl trigger = new SimpleTriggerImpl();  
        trigger.setStartTime(startTime);  
  
        Date date = trigger.computeFirstFireTime(null);  
  
        return date != null && date.after(new Date());  
    }  
}  

```




### 2.8 添加调度的任务类（springms-simple-quartz-cluster/src/main/java/com/springms/cloud/task/ScheduleTask.java）
``` 
package com.springms.cloud.task;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;  
import org.springframework.stereotype.Component;    
    
/**
 * 调度的任务。
 *
 * testScheduleTask 字符串名称在 quartz.xml 中配置为属性 targetObject 的 value 值。</li>
 * sayHello 方法名称在 quartz.xml 中配置为属性 targetMethod 的 value 值。</li>
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/18
 *
 */
@Configuration    
@Component("testScheduleTask")
@EnableScheduling   
public class ScheduleTask {

    private static final Logger Logger = LoggerFactory.getLogger(ScheduleTask.class);

    public void sayHello(JobExecutionContext context){
        Logger.info("====    sayHello 123456789    ====");
        System.out.println("====    sayHello 123456789    ====");
    }    
}  
```


### 2.9 添加Web层Controller测试类（springms-simple-quartz-cluster/src/main/java/com/springms/cloud/controller/QuartzClusterController.java）
``` 
package com.springms.cloud.controller;

import com.springms.cloud.service.ISchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * QuartzCluster 分布式修改调度服务的Controller。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/18
 *
 */
@RestController
public class QuartzClusterController {

    @Autowired
    private ISchedulerService schedulerService;

    /**
     * 每隔多少秒调度一次。
     *
     * @param seconds
     * @return
     */
    @GetMapping("/modify/{seconds}")
    public String modifyStartQuartz(@PathVariable String seconds){
        // eg: 0/10 * * ? * * *
        try {
            schedulerService.schedule("testJobTrigger", "DEFAULT", "0/" + seconds + " * * ? * * *");
        } catch (Exception e) {
            return "Failed";
        }
        return "Successful";
    }
}

```


### 2.10 执行 Quartz 的 11 张表入数据库（springms-simple-quartz-cluster/quartz-tables.log）
``` 


DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;


CREATE TABLE QRTZ_JOB_DETAILS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    SCHED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);


commit;

```


### 2.11 添加 Quartz-Cluster 启动类（springms-simple-quartz-cluster/src/main/java/com/springms/cloud/SimpleQuartzClusterApplication.java）
``` 
package com.springms.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 简单 Quartz-Cluster 微服务，支持集群分布式，并支持动态修改 Quartz 任务的 cronExpression 执行时间。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/9/18
 *
 */
@SpringBootApplication
@ImportResource("quartz.xml")
public class SimpleQuartzClusterApplication {

	private static final Logger Logger = LoggerFactory.getLogger(SimpleQuartzClusterApplication.class);

	public static void main(String[] args) {
		Logger.info("简单Quartz-Cluster微服务入口函数编码-" + System.getProperty("file.encoding"));

		SpringApplication.run(SimpleQuartzClusterApplication.class, args);

		System.out.println("【【【【【【 简单Quartz-Cluster微服务 】】】】】】已启动.");
	}
}
```



## 三、测试

``` 
/****************************************************************************************
 一、简单 Quartz-Cluster 微服务，支持集群分布式，并支持动态修改 Quartz 任务的 cronExpression 执行时间：

 1、添加 Quartz 相关配置文件；
 2、启动 springms-simple-quartz-cluster 模块服务，启动1个端口（8395）；
 3、然后查看日志， ScheduleTask 类的 sayHello 方法被有规律的调用，并打印日志出来；

 4、启动 springms-simple-quartz-cluster 模块服务，再启动2个端口（8396、8397）；
 5、然后看到 3 台服务器只有 1 台服务器调用了 sayHello 方法，因此 Quartz 的集群分布式也算是部署成功了；
 ****************************************************************************************/

/****************************************************************************************
 二、简单 Quartz-Cluster 微服务，支持集群分布式，并支持动态修改 Quartz 任务的 cronExpression 执行时间（动态修改定时任务的 cronExpression 时间表达式）：

 1、添加 Quartz 相关配置文件；
 2、启动 springms-simple-quartz-cluster 模块服务，启动3个端口（8395、8396、8397）；
 3、然后看到 3 台服务器只有 1 台服务器调用了 sayHello 方法打印了日志，因此 Quartz 的集群分布式也算是部署成功了；
 4、然后新起网页输入 http://localhost:8395/modify/5 修改定时任务的触发时间；
 5、再等一会儿就看到 3 台服务器只有 1 台服务器每隔 5 秒调用一次 sayHello 方法，因此修改定时任务的克隆表达式也算是成功了；
 ****************************************************************************************/

/****************************************************************************************
 三、简单 Quartz-Cluster 微服务，支持集群分布式，并支持动态修改 Quartz 任务的 cronExpression 执行时间（动态删除其中 1 台活跃 Quartz 服务器，然后剩下的其中 1 台自动接替）：

 1、添加 Quartz 相关配置文件；
 2、启动 springms-simple-quartz-cluster 模块服务，启动3个端口（8395、8396、8397）；
 3、然后看到 3 台服务器只有 1 台服务器调用了 sayHello 方法打印了日志，因此 Quartz 的集群分布式也算是部署成功了；
 4、然后关闭 1 台活跃 Quartz 服务器；
 5、再等一会儿就看到 2 台服务器中的 1 台服务器每隔一定的时间调用一次 sayHello 方法；
 ****************************************************************************************/
```


## 四、下载地址

<font color=#4183C4 size=4>[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)</font>

<font color=#4183C4 size=4>SpringCloudTutorial交流QQ群: 235322432</font>、<font color=#4183C4 size=4>[微信沟通交流群](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)</font>

<font color=red size=4>欢迎关注，您的肯定是对我最大的支持!!!</font>






























