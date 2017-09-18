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