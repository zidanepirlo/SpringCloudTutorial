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
