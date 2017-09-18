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
