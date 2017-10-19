package com.springms.cloud.task;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@Component
public class ScheduledTasks {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 1000)
    public void reportCurrentTime() {
        System.out.println("当前时间: " + dateFormat.format(new Date()));
        Logger.info("打印当前时间: {}.", dateFormat.format(new Date()));
    }

    /**
     * 定时任务触发，操作多个DAO添加数据，事务中任一异常，都可以正常导致数据回滚。
     */
    @Scheduled(fixedRate = 5000)
    public void addMovieJob() {
        System.out.println("当前时间: " + dateFormat.format(new Date()));
        Logger.info("当前时间: {}.", dateFormat.format(new Date()));
    }
}
