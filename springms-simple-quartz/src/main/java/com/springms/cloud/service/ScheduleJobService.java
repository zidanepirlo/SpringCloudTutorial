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
