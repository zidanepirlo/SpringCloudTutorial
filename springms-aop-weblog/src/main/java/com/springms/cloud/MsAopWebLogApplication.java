package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 使用AOP统一处理Web请求日志。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/19
 *
 */
@SpringBootApplication
public class MsAopWebLogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAopWebLogApplication.class, args);
		System.out.println("【【【【【【 AopWebLog微服务 】】】】】】已启动.");
	}
}




/****************************************************************************************
 实现AOP的切面主要有以下几个要素：

 使用@Aspect注解将一个java类定义为切面类
 使用@Pointcut定义一个切入点，可以是一个规则表达式，比如下例中某个package下的所有函数，也可以是一个注解等。
 根据需要在切入点不同位置的切入内容
 使用@Before在切入点开始处切入内容
 使用@After在切入点结尾处切入内容
 使用@AfterReturning在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
 使用@Around在切入点前后切入内容，并自己控制何时执行切入点自身的内容
 使用@AfterThrowing用来处理当切入内容部分抛出异常之后的处理逻辑
 使用@Order(i)注解来标识切面的优先级。i的值越小，优先级越高
 ****************************************************************************************/





/****************************************************************************************
 一、简单用户微服务类（实现AOP的切面打印日志）：

 1、启动 springms-aop-weblog 模块服务，启动1个端口；
 2、在浏览器输入地址 http://localhost:8350/simple/1 可以看到日志信息成功的被打印出来到控制台上。；

 执行日志顺序：

 2017-10-19 20:22:35.810  INFO 9014 --- [nio-8350-exec-1] WebLogHeadAspect    : (Order(1))************** (doBefore) URL : http://localhost:8350/simple/1
 2017-10-19 20:22:35.810  INFO 9014 --- [nio-8350-exec-1] WebLogHeadAspect    : (Order(1))************** (doBefore) HTTP_METHOD : GET
 2017-10-19 20:22:35.810  INFO 9014 --- [nio-8350-exec-1] WebLogHeadAspect    : (Order(1))************** (doBefore) IP : 127.0.0.1
 2017-10-19 20:22:35.811  INFO 9014 --- [nio-8350-exec-1] WebLogHeadAspect    : (Order(1))************** (doBefore) CLASS_METHOD : com.springms.cloud.controller.SimpleProviderUserAopWebLogController.findById
 2017-10-19 20:22:35.811  INFO 9014 --- [nio-8350-exec-1] WebLogHeadAspect    : (Order(1))************** (doBefore) ARGS : [1]
 2017-10-19 20:22:35.811  INFO 9014 --- [nio-8350-exec-1] com.springms.cloud.aop.WebLogFiveAspect        : (Order(5))============== (doBefore) URL : http://localhost:8350/simple/1
 2017-10-19 20:22:35.811  INFO 9014 --- [nio-8350-exec-1] com.springms.cloud.aop.WebLogFiveAspect        : (Order(5))============== (doBefore) HTTP_METHOD : GET
 2017-10-19 20:22:35.811  INFO 9014 --- [nio-8350-exec-1] com.springms.cloud.aop.WebLogFiveAspect        : (Order(5))============== (doBefore) IP : 127.0.0.1
 2017-10-19 20:22:35.811  INFO 9014 --- [nio-8350-exec-1] com.springms.cloud.aop.WebLogFiveAspect        : (Order(5))============== (doBefore) CLASS_METHOD : com.springms.cloud.controller.SimpleProviderUserAopWebLogController.findById
 2017-10-19 20:22:35.811  INFO 9014 --- [nio-8350-exec-1] com.springms.cloud.aop.WebLogFiveAspect        : (Order(5))============== (doBefore) ARGS : [1]
 Hibernate: select user0_.id as id1_0_0_, user0_.age as age2_0_0_, user0_.balance as balance3_0_0_, user0_.name as name4_0_0_, user0_.username as username5_0_0_ from user user0_ where user0_.id=?
 2017-10-19 20:22:35.853  INFO 9014 --- [nio-8350-exec-1] com.springms.cloud.aop.WebLogFiveAspect        : (Order(5))============== (doAfterReturning) RESPONSE : User@1249202
 2017-10-19 20:22:35.853  INFO 9014 --- [nio-8350-exec-1] com.springms.cloud.aop.WebLogFiveAspect        : (Order(5))============== (doAfterReturning) SPEND TIME : 42
 2017-10-19 20:22:35.853  INFO 9014 --- [nio-8350-exec-1] WebLogHeadAspect    : (Order(1))************** (doAfterReturning) RESPONSE : User@1249202
 2017-10-19 20:22:35.853  INFO 9014 --- [nio-8350-exec-1] WebLogHeadAspect    : (Order(1))************** (doAfterReturning) SPEND TIME : 43

 总结：doBefore 方法先从优先级到低优先级依次执行完，然后 doAfterReturning 方法从低优先级到高优先级依次执行完；也就是进入从高到低，出来从低到高；
 ****************************************************************************************/

