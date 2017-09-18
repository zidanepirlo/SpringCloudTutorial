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