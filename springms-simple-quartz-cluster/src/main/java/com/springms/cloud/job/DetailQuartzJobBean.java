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