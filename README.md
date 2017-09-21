# SpringCloud 教程
-

## 一、简介

``` 

```


## 二、各微服务占用端口列表
|章		| 微服务名称        														| 端口	| 功能描述		|
|:-----:	| :---------------------------------------------------------------------|:-----:|:------------	|
|001	| springms-simple-provider-user      									| 8000 	|简单用户微服务 	|
|002	| springms-simple-consumer-movie      									| 8005 	|简单电影微服务 	|
|003	| springms-discovery-eureka      										| 8761 	|服务发现服务端EurekaServer微服务 	|
|004	| springms-provider-user													| 7900 	|用户服务类，已注册 Eureka 	|
|005	| springms-consumer-movie      											| 8005 	|电影微服务，已注册 Eureka 	|
|006	| springms-consumer-movie-ribbon      									| 8010 	|电影微服务，使用 Ribbon 在客户端进行负载均衡  	|
|007	| springms-consumer-movie-ribbon-custom      							| 8020 	|电影微服务，定制 Ribbon 在客户端进行负载均衡 	|
|008	| springms-consumer-movie-ribbon-properties     							| 8030 	|电影微服务，配置 Ribbon 在客户端进行负载均衡 	|
|009	| springms-simple-quartz     									 		| 8390 	|简单Quartz微服务，不支持分布式 	|
|010	| springms-simple-quartz-cluster     									| 8395 	|简单Quartz分布式集群, 可动态修改任务执行时间 	|
|011	| springms-consumer-movie-ribbon-properties-without-eureka     			| 8040 	|电影Ribbon微服务，脱离Eureka使用 	|
|012	| springms-consumer-movie-feign     			                        	| 7910 	|电影 Feign 微服务，支持客户端负载均衡 	|
|013	| springms-consumer-movie-feign-custom     			                	| 8050 	|电影微服务，定制Feign可负载均衡并认证Eureka 	|
|014	| springms-consumer-movie-ribbon-with-hystrix		                	| 8070 	|电影Ribbon微服务，集成 Hytrix 断路器功能 	|





## 三、功能模块






































