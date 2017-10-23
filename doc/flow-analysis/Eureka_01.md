# SpringCloud（第 049 篇）Netflix Eureka 源码深入剖析（上）
-

## 一、大致介绍

``` 
1、鉴于一些朋友的提问并提议讲解下eureka的源码分析，由此应运而产生的本章节的内容；
2、所以我站在自我的理解角度试着整理了这篇Eureka源码的分析，希望对大家有所帮助；
3、由于篇幅太长不能在一篇里面发布出来，所以拆分了上下篇；
```


## 二、基本原理
``` 
1、Eureka Server 提供服务注册服务，各个节点启动后，会在Eureka Server中进行注册，这样Eureka Server中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。
2、Eureka Client 是一个Java 客户端，用于简化与Eureka Server的交互，客户端同时也具备一个内置的、使用轮询负载算法的负载均衡器。
3、在应用启动后，将会向Eureka Server发送心跳(默认周期为30秒)，如果Eureka Server在多个心跳周期没有收到某个节点的心跳，Eureka Server 将会从服务注册表中把这个服务节点移除(默认90秒)。
4、Eureka Server之间将会通过复制的方式完成数据的同步；
5、Eureka Client具有缓存的机制，即使所有的Eureka Server 都挂掉的话，客户端依然可以利用缓存中的信息消费其它服务的API；
```


## 三、EurekaServer 启动流程分析

### 3.1 跑一下 springms-discovery-eureka 代码，不难发现，我们会看到一些有关 EurekaServer 启动的流程日志；
``` 
2017-10-22 18:14:17.635  INFO 5288 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Located managed bean 'environmentManager': registering with JMX server as MBean [org.springframework.cloud.context.environment:name=environmentManager,type=EnvironmentManager]
2017-10-22 18:14:17.650  INFO 5288 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Located managed bean 'restartEndpoint': registering with JMX server as MBean [org.springframework.cloud.context.restart:name=restartEndpoint,type=RestartEndpoint]
2017-10-22 18:14:17.661  INFO 5288 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Located managed bean 'refreshScope': registering with JMX server as MBean [org.springframework.cloud.context.scope.refresh:name=refreshScope,type=RefreshScope]
2017-10-22 18:14:17.674  INFO 5288 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Located managed bean 'configurationPropertiesRebinder': registering with JMX server as MBean [org.springframework.cloud.context.properties:name=configurationPropertiesRebinder,context=335b5620,type=ConfigurationPropertiesRebinder]
2017-10-22 18:14:17.683  INFO 5288 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Located managed bean 'refreshEndpoint': registering with JMX server as MBean [org.springframework.cloud.endpoint:name=refreshEndpoint,type=RefreshEndpoint]
2017-10-22 18:14:17.926  INFO 5288 --- [           main] o.s.c.support.DefaultLifecycleProcessor  : Starting beans in phase 0
2017-10-22 18:14:17.927  INFO 5288 --- [           main] c.n.e.EurekaDiscoveryClientConfiguration : Registering application unknown with eureka with status UP
2017-10-22 18:14:17.927  INFO 5288 --- [      Thread-10] o.s.c.n.e.server.EurekaServerBootstrap   : Setting the eureka configuration..
2017-10-22 18:14:17.948  INFO 5288 --- [      Thread-10] o.s.c.n.e.server.EurekaServerBootstrap   : isAws returned false
2017-10-22 18:14:17.949  INFO 5288 --- [      Thread-10] o.s.c.n.e.server.EurekaServerBootstrap   : Initialized server context
2017-10-22 18:14:17.949  INFO 5288 --- [      Thread-10] c.n.e.r.PeerAwareInstanceRegistryImpl    : Got 1 instances from neighboring DS node
2017-10-22 18:14:17.949  INFO 5288 --- [      Thread-10] c.n.e.r.PeerAwareInstanceRegistryImpl    : Renew threshold is: 1
2017-10-22 18:14:17.949  INFO 5288 --- [      Thread-10] c.n.e.r.PeerAwareInstanceRegistryImpl    : Changing status to UP
2017-10-22 18:14:17.958  INFO 5288 --- [      Thread-10] e.s.EurekaServerInitializerConfiguration : Started Eureka Server
2017-10-22 18:14:18.019  INFO 5288 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8761 (http)
2017-10-22 18:14:18.020  INFO 5288 --- [           main] c.n.e.EurekaDiscoveryClientConfiguration : Updating port to 8761
2017-10-22 18:14:18.023  INFO 5288 --- [           main] c.s.cloud.EurekaServerApplication        : Started EurekaServerApplication in 8.299 seconds (JVM running for 8.886)
【【【【【【 Eureka微服务 】】】】】】已启动.

【分析】：发现有这么一句日志打印“Setting the eureka configuration..”，eureka 开始进行配置，说不定也许就是Eureka Server 流程启动的开
始呢？我们抱着怀疑的心态进入这行日志打印的EurekaServerBootstrap类去看看。
```

### 3.2 进入 EurekaServerBootstrap 类看看，看这个类的名字，见名知意，应该就是 EurekaServer 的启动类了；
``` 
protected void initEurekaEnvironment() throws Exception {
	log.info("Setting the eureka configuration..");
	。。。
｝

【分析一】：我们看到日志在 initEurekaEnvironment 方法中被打印出来，然后我顺着这个方法寻找该方法被调用的地方；

public void contextInitialized(ServletContext context) {
	try {
		initEurekaEnvironment();
		initEurekaServerContext();

		context.setAttribute(EurekaServerContext.class.getName(), this.serverContext);
	}
	catch (Throwable e) {
		log.error("Cannot bootstrap eureka server :", e);
		throw new RuntimeException("Cannot bootstrap eureka server :", e);
	}
}

【分析二】：接着发现 contextInitialized 这个方法里面调用了 initEurekaEnvironment 方法，接着我们再往上层寻找被调用的地方；

【分析三】：接着我们看到 EurekaServerInitializerConfiguration 类中有个 start 方法，该方法创建了一个线程来后台执行 EurekaServer 的初始化流程；
```
### 3.3 进入 EurekaServerInitializerConfiguration 方法，看看这个所谓的 EurekaServer 初始化配置做了哪些事情？
``` 
@Override
public void start() { // 打上断点
	new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				//TODO: is this class even needed now?
				eurekaServerBootstrap.contextInitialized(EurekaServerInitializerConfiguration.this.servletContext);
				log.info("Started Eureka Server");

				publish(new EurekaRegistryAvailableEvent(getEurekaServerConfig()));
				EurekaServerInitializerConfiguration.this.running = true;
				publish(new EurekaServerStartedEvent(getEurekaServerConfig()));
			}
			catch (Exception ex) {
				// Help!
				log.error("Could not initialize Eureka servlet context", ex);
			}
		}
	}).start();
}

【分析一】：看到 log.info("Started Eureka Server"); 这行代码，相信大家已经释然了，这里就是所谓的启动了 EurekaServer 了，其实也就是 
eurekaServerBootstrap.contextInitialized(EurekaServerInitializerConfiguration.this.servletContext) 初始化了一些我们未知的东西；

【分析二】：当打印完启动Eureka Server日志后，调用了两次 publish 方法，该方法最终调用的是 this.applicationContext.publishEvent
(event) 方法，目的是利用Spring中ApplicationContext对事件传递性质，事件发布者(applicationContext)来发布事件(event)，但是缺少的是监听
者，其实你仔细搜索下代码，发现好像没有地方对 EurekaServerStartedEvent、EurekaRegistryAvailableEvent 进行监听，奇了怪了，这是咋了呢？

【分析三】：然后找到 EurekaServerStartedEvent 所在的目录下，EurekaInstanceCanceledEvent、EurekaInstanceRegisteredEvent、
EurekaInstanceRenewedEvent、EurekaRegistryAvailableEvent、EurekaServerStartedEvent 有这么几个事件的类，服务下线事件、服务注册事
件、服务续约事件、注册中心启动事件、Eureka Server启动事件，这么几个事件都没有被监听，那么我们是不是给添加上监听，是不是就可以了呢？像这样
 @EventListener  public void listen(EurekaInstanceCanceledEvent event) { 。。。处下线逻辑 ｝，添加 EventListener 监听注解，就可
以在我们自己的代码逻辑中收到这个事件的回调了，所以想想SpringCloud还是挺机制的，提供回调接口让我们自己实现自己的业务逻辑，真心不错；

【分析四】：那么反过来想想，为啥会无缘无故 start 方法就被调用了呢？那么反向继续向上找调用 start 方法的地方，结果找到了 
DefaultLifecycleProcessor类的doStart方法调用了 bean.start(); 这么一段代码；
```

### 3.4 进入 DefaultLifecycleProcessor 类看看，这个 EurekaServerInitializerConfiguration.start 方法是如何被触发的？
``` 
private void doStart(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, boolean autoStartupOnly) {
	// 打上断点
	Lifecycle bean = lifecycleBeans.remove(beanName);
	if (bean != null && !this.equals(bean)) {
		String[] dependenciesForBean = this.beanFactory.getDependenciesForBean(beanName);
		for (String dependency : dependenciesForBean) {
			doStart(lifecycleBeans, dependency, autoStartupOnly);
		}
		if (!bean.isRunning() &&
				(!autoStartupOnly || !(bean instanceof SmartLifecycle) || ((SmartLifecycle) bean).isAutoStartup())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting bean '" + beanName + "' of type [" + bean.getClass() + "]");
			}
			try {
				bean.start();
			}
			catch (Throwable ex) {
				throw new ApplicationContextException("Failed to start bean '" + beanName + "'", ex);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Successfully started bean '" + beanName + "'");
			}
		}
	}
}

【分析一】：看到在 bean.isRunning 等一系列状态的判断下才去调用 bean.start() 方法的，我们再往上寻找被调用地方；

public void start() {
	// 打上断点
	if (this.members.isEmpty()) {
		return;
	}
	if (logger.isInfoEnabled()) {
		logger.info("Starting beans in phase " + this.phase);
	}
	Collections.sort(this.members);
	for (LifecycleGroupMember member : this.members) {
		if (this.lifecycleBeans.containsKey(member.name)) {
			doStart(this.lifecycleBeans, member.name, this.autoStartupOnly);
		}
	}
}

【分析二】：该类是DefaultLifecycleProcessor中内部类LifecycleGroup的一个方法，再往上寻找调用方；

private void startBeans(boolean autoStartupOnly) {
	Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
	Map<Integer, LifecycleGroup> phases = new HashMap<Integer, LifecycleGroup>();
	for (Map.Entry<String, ? extends Lifecycle> entry : lifecycleBeans.entrySet()) {
		Lifecycle bean = entry.getValue();
		if (!autoStartupOnly || (bean instanceof SmartLifecycle && ((SmartLifecycle) bean).isAutoStartup())) {
			int phase = getPhase(bean);
			LifecycleGroup group = phases.get(phase);
			if (group == null) {
				group = new LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly);
				phases.put(phase, group);
			}
			group.add(entry.getKey(), bean);
		}
	}
	if (phases.size() > 0) {
		List<Integer> keys = new ArrayList<Integer>(phases.keySet());
		Collections.sort(keys);
		for (Integer key : keys) {
			phases.get(key).start();
		}
	}
}

【分析三】：startBeans 属于 DefaultLifecycleProcessor 类的一个私有方法，startBeans 方法第一行就是获取 getLifecycleBeans() 生命周期
Bean对象，由此可见似乎 Eureka Server 之所以会被启动，是不是实现了某个接口或者重写了某个方法，才会导致由于容易在初始化的过程中因调用某些特
殊方法或者某些类才启动的，因此我们回头去看看 EurekaServerInitializerConfiguration 这个类；

【分析四】：结果发现 EurekaServerInitializerConfiguration 这个类实现了 SmartLifecycle 这么样的一个接口，而 SmartLifecycle 接口又继
承了 Lifecycle 生命周期接口类，所以真想已经重见天日了，原来是实现了 Lifecycle 这样的一个接口，然后实现了 start 方法，因此 Eureka 
Server 就这么稀里糊涂的就被莫名其妙的启动起来了？
```

### 3.5 到这里难道就真的完了么？难道Eureka Server启动就干这么点点事情？不可能吧？
``` 
【分析一】：我们之前仅仅只是通过了日志来逆向分析，但是我们是不是忘了我们本应该标志是Eureka Server的这个注解了呢？没错，我们在分析的过程中
已经将 @EnableEurekaServer 这个注解遗忘了，那么我们现在先回到这个注解类来看看；
```

### 3.6 进入 EnableEurekaServer 类，看看究竟干了啥？
``` 
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EurekaServerConfiguration.class)
public @interface EnableEurekaServer {

}

【分析一】：我们不难发现 EnableEurekaServer 类上有个 @Import 注解，引用了一个 class 文件，由此我们进入观察；
```

### 3.7 进入 EurekaServerConfiguration 类看看，看名称的话，理解的意思大概就是 Eureka Server 配置类；
``` 
【分析一】：果不其然，这个类有很多 @Bean、@Configuration 注解过的方法，那是不是我们可以认为刚才 3.1~3.4 的推论是不是就是由于被实例化了这么一个 Bean，然后就慢慢的调用到了 start 方法了呢？

【分析二】：搜索 “Bootstrap” 字样，还真发现了有这么一个方法；

@Bean
public EurekaServerBootstrap eurekaServerBootstrap(PeerAwareInstanceRegistry registry,
		EurekaServerContext serverContext) {
	return new EurekaServerBootstrap(this.applicationInfoManager,
			this.eurekaClientConfig, this.eurekaServerConfig, registry,
			serverContext);
}

【分析三】：既然有这么一个 Bean，那么是不是和刚开始顺着日志逆向分析也是有一定道理的，没有这么一个Bean的存在，那么 DefaultLifecycleProcessor.startBeans 方法中 getLifecycleBeans 的这个也就没那么顺畅被找到了呢？不过我的猜想是这样的，本人没有将源码下载下来，将 eurekaServerBootstrap 方法中的 @Bean 注解注释掉试试，不过推理起来也八九不离十，这个疑问悬念就留给大家尝试尝试吧；

【分析四】：既然找到了一个 @Bean 注解过的方法，那我们再找找其他的一些被注解过的方法，比如一些通用全局用的类似词眼，比如 Context，Bean，Init、Server 之类的；

@Bean
public EurekaServerContext eurekaServerContext(ServerCodecs serverCodecs,
		PeerAwareInstanceRegistry registry, PeerEurekaNodes peerEurekaNodes) {
	return new DefaultEurekaServerContext(this.eurekaServerConfig, serverCodecs,
			registry, peerEurekaNodes, this.applicationInfoManager);
}

@Bean
public PeerEurekaNodes peerEurekaNodes(PeerAwareInstanceRegistry registry,
		ServerCodecs serverCodecs) {
	return new PeerEurekaNodes(registry, this.eurekaServerConfig,
			this.eurekaClientConfig, serverCodecs, this.applicationInfoManager);
}

@Bean
public PeerAwareInstanceRegistry peerAwareInstanceRegistry(
		ServerCodecs serverCodecs) {
	this.eurekaClient.getApplications(); // force initialization
	return new InstanceRegistry(this.eurekaServerConfig, this.eurekaClientConfig,
			serverCodecs, this.eurekaClient,
			this.instanceRegistryProperties.getExpectedNumberOfRenewsPerMin(),
			this.instanceRegistryProperties.getDefaultOpenForTrafficCount());
}

@Bean
@ConditionalOnProperty(prefix = "eureka.dashboard", name = "enabled", matchIfMissing = true)
public EurekaController eurekaController() {
	return new EurekaController(this.applicationInfoManager);
}

【分析五】：DefaultEurekaServerContext.initialize 初始化了一些东西，现在还不知道干啥用的，先放这里，打上断点；

【分析六】：PeerEurekaNodes.start 方法，又是一个 start 方法，但是该类没有实现任何类，姑且先放这里，打上断点；

【分析七】：InstanceRegistry.register 方法，而且还有几个呢，可能是客户端注册用的，也先放这里，都打上断点，或者将 这个类的所有方法都断点上，断点打完后发现有注册的，有续约的，有注销的；

【分析八】：打完这些断点后，感觉没有思路了，索性就断点跑一把，看看有什么新的发现点；
```

### 3.8 停止服务，Debug 跑一下 springms-discovery-eureka 代码；
``` 
【分析一】：DefaultEurekaServerContext.initialize 方法被调用了，证实了刚才想法，EurekaServerConfiguration 不是白写的，还是有它的作用的；

@PostConstruct
@Override
public void initialize() throws Exception {
    logger.info("Initializing ...");
    peerEurekaNodes.start();
    registry.init(peerEurekaNodes);
    logger.info("Initialized");
}

【分析二】：进入 initialize 方法中 peerEurekaNodes.start();

public void start() {
    taskExecutor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "Eureka-PeerNodesUpdater");
                    thread.setDaemon(true);
                    return thread;
                }
            }
    );
    try {
        updatePeerEurekaNodes(resolvePeerUrls());
        Runnable peersUpdateTask = new Runnable() {
            @Override
            public void run() {
                try {
                    updatePeerEurekaNodes(resolvePeerUrls());
                } catch (Throwable e) {
                    logger.error("Cannot update the replica Nodes", e);
                }

            }
        };
		// 注释：间隔 600000 毫秒，即 10分钟 间隔执行一次服务集群数据同步；
        taskExecutor.scheduleWithFixedDelay(
                peersUpdateTask,
                serverConfig.getPeerEurekaNodesUpdateIntervalMs(),
                serverConfig.getPeerEurekaNodesUpdateIntervalMs(),
                TimeUnit.MILLISECONDS
        );
    } catch (Exception e) {
        throw new IllegalStateException(e);
    }
    for (PeerEurekaNode node : peerEurekaNodes) {
        logger.info("Replica node URL:  " + node.getServiceUrl());
    }
}

【分析三】： start 方法中会看到一个定时调度的任务，updatePeerEurekaNodes(resolvePeerUrls()); 间隔 600000 毫秒，即 10分钟 间隔执行一次服务集群数据同步；

【分析四】： 然后断点放走放下走，进入 initialize 方法中 registry.init(peerEurekaNodes);

@Override
public void init(PeerEurekaNodes peerEurekaNodes) throws Exception {
    this.numberOfReplicationsLastMin.start();
    this.peerEurekaNodes = peerEurekaNodes;
	// 注释：初始化 Eureka Server 响应缓存，默认缓存时间为30s
    initializedResponseCache();
	// 注释：定时任务，多久重置一下心跳阈值，900000 毫秒，即 15分钟 的间隔时间，会重置心跳阈值
    scheduleRenewalThresholdUpdateTask();
	// 注释：初始化远端注册
    initRemoteRegionRegistry();

    try {
        Monitors.registerObject(this);
    } catch (Throwable e) {
        logger.warn("Cannot register the JMX monitor for the InstanceRegistry :", e);
    }
}

【分析五】： 缓存也配置好了，定时任务也配置好了，似乎应该没啥了，那么我们把断点放开，看看下一步会走到哪里？
```

### 3.9 EurekaServerInitializerConfiguration.start 也进断点了。
``` 
【分析一】：先是 DefaultLifecycleProcessor.doStart 方法进断点，然后才是 EurekaServerInitializerConfiguration.start 方法进断点；

【分析二】：再一次证明刚刚的逆向分析仅仅只是缺了个从头EnableEurekaServer分析罢了，但是最终方法论分析思路还是对的，由于开始分析过这里，然而我们就跳过，继续放开断点向后继续看看；

```


### 3.10 InstanceRegistry.openForTraffic 也进断点了。
``` 
【分析一】：这不就是我们刚才在 “步骤3.7之分析七” 打的断点么？看下堆栈信息，正是 “步骤3.2之分析一” 中 initEurekaServerContext 方法中有
这么一句 this.registry.openForTraffic(this.applicationInfoManager, registryCount); 调用到了，因果轮回，代码千变万化，打上断点还有有好处的，结果还是回到了开始日志逆向分析的地方。

【分析二】：进入 super.openForTraffic 方法；

@Override
public void openForTraffic(ApplicationInfoManager applicationInfoManager, int count) {
    // Renewals happen every 30 seconds and for a minute it should be a factor of 2.
	// 注释：每30秒续约一次，那么每分钟续约就是2次，所以才是 count * 2 的结果；
    this.expectedNumberOfRenewsPerMin = count * 2;
    this.numberOfRenewsPerMinThreshold =
            (int) (this.expectedNumberOfRenewsPerMin * serverConfig.getRenewalPercentThreshold());
    logger.info("Got " + count + " instances from neighboring DS node");
    logger.info("Renew threshold is: " + numberOfRenewsPerMinThreshold);
    this.startupTime = System.currentTimeMillis();
    if (count > 0) {
        this.peerInstancesTransferEmptyOnStartup = false;
    }
    DataCenterInfo.Name selfName = applicationInfoManager.getInfo().getDataCenterInfo().getName();
    boolean isAws = Name.Amazon == selfName;
    if (isAws && serverConfig.shouldPrimeAwsReplicaConnections()) {
        logger.info("Priming AWS connections for all replicas..");
        primeAwsReplicas(applicationInfoManager);
    }
    logger.info("Changing status to UP");
	// 注释：修改 Eureka Server 为上电状态，就是说设置 Eureka Server 已经处于活跃状态了，那就是意味着 EurekaServer 基本上说可以正常使用了；
    applicationInfoManager.setInstanceStatus(InstanceStatus.UP);
	// 注释：定时任务，60000 毫秒，即 1分钟 的间隔时间，Eureke Server定期进行失效节点的清理
    super.postInit();
}

【分析三】：这里主要设置了服务状态，以及开启了定时清理失效节点的定时任务，每分钟扫描一次；
```
### 3.11 继续放开断点，来到了日志打印  “main] c.n.e.EurekaDiscoveryClientConfiguration : Updating port to 8761” 的EurekaDiscoveryClientConfiguration 类中 onApplicationEvent 方法。
``` 
@EventListener(EmbeddedServletContainerInitializedEvent.class)
public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
	// TODO: take SSL into account when Spring Boot 1.2 is available
	int localPort = event.getEmbeddedServletContainer().getPort();
	if (this.port.get() == 0) {
		log.info("Updating port to " + localPort);
		this.port.compareAndSet(0, localPort);
		start();
	}
}

【分析一】：设置端口，当看到 Updating port to 8761 这样的日志打印出来的话，说明 Eureka Server 整个启动也就差不多Over了。现在回头看看，
发现分析了不少的方法和流程，有种感觉被掏空的感觉了。
```

### 3.12 总结 EurekaServer 启动时候大概干了哪些事情？
``` 
1、初始化Eureka环境，Eureka上下文；
2、初始化EurekaServer的缓存
3、启动了一些定时任务，比如充值心跳阈值定时任务，清理失效节点定时任务；
4、更新EurekaServer上电状态，更新EurekaServer端口；

虽然我从列举的流程里面大概总结了这么几点，但是还是有些是我没关注到的，如果大家有关注到的，可以和我共同讨论分析分析。
```

## 四、EurekaServer 处理服务注册、集群数据复制

### 4.1 EurekaClient 是如何注册到 EurekaServer 的？
``` 
【分析一】：由于我们刚才在 org.springframework.cloud.netflix.eureka.server.InstanceRegistry 的每个方法都打了一个断点，而且现在 
EurekaServer 已经处于 Debug 运行状态，那么我们就随便找一个被 @EnableEurekaClient 的微服务启动试试，要么就拿 springms-provider-user
微服务来试试吧，直接 Run。

【分析二】：猜测，如果如我们分析所想，当 springms-provider-user 启动后，就一定会调用注册register方法，那么就接着往下看，拭目以待；
```

### 4.2 InstanceRegistry.register(final InstanceInfo info, final boolean isReplication) 方法进断点了。
``` 
【分析一】：由于 InstanceRegistry.register 是我们刚刚打断点的地方，那么我们顺着堆栈信息往上看，原来是 ApplicationResource.addInstance 方法被调用了，那么我们就看看 addInstance 这个方法，并在 addInstance 这里打上断点；接着我们重新杀死 springms-provider-user 服务，然后再重启 springms-provider-user 服务；
```

### 4.2 断点再次来到了 ApplicationResource 类，这个类呢，主要是处理接收 Http 的服务请求。
``` 
@POST
@Consumes({"application/json", "application/xml"})
public Response addInstance(InstanceInfo info,
                            @HeaderParam(PeerEurekaNode.HEADER_REPLICATION) String isReplication) {
    logger.debug("Registering instance {} (replication={})", info.getId(), isReplication);
    // validate that the instanceinfo contains all the necessary required fields
    if (isBlank(info.getId())) {
        return Response.status(400).entity("Missing instanceId").build();
    } else if (isBlank(info.getHostName())) {
        return Response.status(400).entity("Missing hostname").build();
    } else if (isBlank(info.getAppName())) {
        return Response.status(400).entity("Missing appName").build();
    } else if (!appName.equals(info.getAppName())) {
        return Response.status(400).entity("Mismatched appName, expecting " + appName + " but was " + info.getAppName()).build();
    } else if (info.getDataCenterInfo() == null) {
        return Response.status(400).entity("Missing dataCenterInfo").build();
    } else if (info.getDataCenterInfo().getName() == null) {
        return Response.status(400).entity("Missing dataCenterInfo Name").build();
    }

    // handle cases where clients may be registering with bad DataCenterInfo with missing data
    DataCenterInfo dataCenterInfo = info.getDataCenterInfo();
    if (dataCenterInfo instanceof UniqueIdentifier) {
        String dataCenterInfoId = ((UniqueIdentifier) dataCenterInfo).getId();
        if (isBlank(dataCenterInfoId)) {
            boolean experimental = "true".equalsIgnoreCase(serverConfig.getExperimental("registration.validation.dataCenterInfoId"));
            if (experimental) {
                String entity = "DataCenterInfo of type " + dataCenterInfo.getClass() + " must contain a valid id";
                return Response.status(400).entity(entity).build();
            } else if (dataCenterInfo instanceof AmazonInfo) {
                AmazonInfo amazonInfo = (AmazonInfo) dataCenterInfo;
                String effectiveId = amazonInfo.get(AmazonInfo.MetaDataKey.instanceId);
                if (effectiveId == null) {
                    amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.instanceId.getName(), info.getId());
                }
            } else {
                logger.warn("Registering DataCenterInfo of type {} without an appropriate id", dataCenterInfo.getClass());
            }
        }
    }

    registry.register(info, "true".equals(isReplication));
    return Response.status(204).build();  // 204 to be backwards compatible
}

【分析一】：这里的写法貌似看起来和我们之前 Controller 的 RESTFUL 写法有点不一样，仔细一看，原来是Jersey RESTful 框架，是一个产品级的 
RESTful service 和 client 框架。与Struts类似，它同样可以和hibernate,spring框架整合。

【分析二】：紧接着，我们看到 registry.register(info, "true".equals(isReplication)); 这么一段代码，注册啊，原来EurekaClient客户端启
动后会调用会通过Http(s)请求，直接调到 ApplicationResource.addInstance 方法，那么总算明白了，只要是和注册有关的，都会调用这个方法。

【分析三】：接着我们深入 registry.register(info, "true".equals(isReplication)) 查看；

@Override
public void register(final InstanceInfo info, final boolean isReplication) {
	handleRegistration(info, resolveInstanceLeaseDuration(info), isReplication);
	super.register(info, isReplication);
}

【分析四】：看看 handleRegistration(info, resolveInstanceLeaseDuration(info), isReplication) 方法；

private void handleRegistration(InstanceInfo info, int leaseDuration,
		boolean isReplication) {
	log("register " + info.getAppName() + ", vip " + info.getVIPAddress()
			+ ", leaseDuration " + leaseDuration + ", isReplication "
			+ isReplication);
	publishEvent(new EurekaInstanceRegisteredEvent(this, info, leaseDuration,
			isReplication));
}

【分析五】：该方法仅仅只是打了一个日志，然后通过 ApplicationContext 发布了一个事件 EurekaInstanceRegisteredEvent 服务注册事件，正如
“步骤3.3之分析三” 所提到的，用户可以给 EurekaInstanceRegisteredEvent 添加监听事件，那么用户就可以在此刻实现自己想要的一些业务逻辑。
然后我们再来看看 super.register(info, isReplication) 方法，该方法是 InstanceRegistry 的父类 PeerAwareInstanceRegistryImpl 的方法。
```

### 4.3 进入 PeerAwareInstanceRegistryImpl 类的 register(final InstanceInfo info, final boolean isReplication) 方法；
``` 
@Override
public void register(final InstanceInfo info, final boolean isReplication) {
	// 注释：续约时间，默认时间是常量值 90 秒
    int leaseDuration = Lease.DEFAULT_DURATION_IN_SECS;
	// 注释：续约时间，当然也可以从配置文件中取出来，所以说续约时间值也是可以让我们自己自定义配置的
    if (info.getLeaseInfo() != null && info.getLeaseInfo().getDurationInSecs() > 0) {
        leaseDuration = info.getLeaseInfo().getDurationInSecs();
    }
	// 注释：将注册方的信息写入 EurekaServer 的注册表，父类为　AbstractInstanceRegistry
    super.register(info, leaseDuration, isReplication);
	// 注释：EurekaServer 节点之间的数据同步，复制到其他Peer
    replicateToPeers(Action.Register, info.getAppName(), info.getId(), info, null, isReplication);
}

【分析一】：进入　super.register(info, leaseDuration, isReplication)　看看是如何写入 EurekaServer 的注册表的，即进入　AbstractInstanceRegistry.register(InstanceInfo registrant, int leaseDuration, boolean isReplication) 方法。

public void register(InstanceInfo registrant, int leaseDuration, boolean isReplication) {
    try {
        read.lock();
		// 注释：registry　这个变量，就是我们所谓的注册表，注册表是保存在内存中的；
        Map<String, Lease<InstanceInfo>> gMap = registry.get(registrant.getAppName());
        REGISTER.increment(isReplication);
        if (gMap == null) {
            final ConcurrentHashMap<String, Lease<InstanceInfo>> gNewMap = new ConcurrentHashMap<String, Lease<InstanceInfo>>();
            gMap = registry.putIfAbsent(registrant.getAppName(), gNewMap);
            if (gMap == null) {
                gMap = gNewMap;
            }
        }
        Lease<InstanceInfo> existingLease = gMap.get(registrant.getId());
        // Retain the last dirty timestamp without overwriting it, if there is already a lease
        if (existingLease != null && (existingLease.getHolder() != null)) {
            Long existingLastDirtyTimestamp = existingLease.getHolder().getLastDirtyTimestamp();
            Long registrationLastDirtyTimestamp = registrant.getLastDirtyTimestamp();
            logger.debug("Existing lease found (existing={}, provided={}", existingLastDirtyTimestamp, registrationLastDirtyTimestamp);
            if (existingLastDirtyTimestamp > registrationLastDirtyTimestamp) {
                logger.warn("There is an existing lease and the existing lease's dirty timestamp {} is greater" +
                        " than the one that is being registered {}", existingLastDirtyTimestamp, registrationLastDirtyTimestamp);
                logger.warn("Using the existing instanceInfo instead of the new instanceInfo as the registrant");
                registrant = existingLease.getHolder();
            }
        } else {
            // The lease does not exist and hence it is a new registration
            synchronized (lock) {
                if (this.expectedNumberOfRenewsPerMin > 0) {
                    // Since the client wants to cancel it, reduce the threshold
                    // (1
                    // for 30 seconds, 2 for a minute)
                    this.expectedNumberOfRenewsPerMin = this.expectedNumberOfRenewsPerMin + 2;
                    this.numberOfRenewsPerMinThreshold =
                            (int) (this.expectedNumberOfRenewsPerMin * serverConfig.getRenewalPercentThreshold());
                }
            }
            logger.debug("No previous lease information found; it is new registration");
        }
        Lease<InstanceInfo> lease = new Lease<InstanceInfo>(registrant, leaseDuration);
        if (existingLease != null) {
            lease.setServiceUpTimestamp(existingLease.getServiceUpTimestamp());
        }
        gMap.put(registrant.getId(), lease);
        synchronized (recentRegisteredQueue) {
            recentRegisteredQueue.add(new Pair<Long, String>(
                    System.currentTimeMillis(),
                    registrant.getAppName() + "(" + registrant.getId() + ")"));
        }
        // This is where the initial state transfer of overridden status happens
        if (!InstanceStatus.UNKNOWN.equals(registrant.getOverriddenStatus())) {
            logger.debug("Found overridden status {} for instance {}. Checking to see if needs to be add to the "
                            + "overrides", registrant.getOverriddenStatus(), registrant.getId());
            if (!overriddenInstanceStatusMap.containsKey(registrant.getId())) {
                logger.info("Not found overridden id {} and hence adding it", registrant.getId());
                overriddenInstanceStatusMap.put(registrant.getId(), registrant.getOverriddenStatus());
            }
        }
        InstanceStatus overriddenStatusFromMap = overriddenInstanceStatusMap.get(registrant.getId());
        if (overriddenStatusFromMap != null) {
            logger.info("Storing overridden status {} from map", overriddenStatusFromMap);
            registrant.setOverriddenStatus(overriddenStatusFromMap);
        }

        // Set the status based on the overridden status rules
        InstanceStatus overriddenInstanceStatus = getOverriddenInstanceStatus(registrant, existingLease, isReplication);
        registrant.setStatusWithoutDirty(overriddenInstanceStatus);

        // If the lease is registered with UP status, set lease service up timestamp
        if (InstanceStatus.UP.equals(registrant.getStatus())) {
            lease.serviceUp();
        }
        registrant.setActionType(ActionType.ADDED);
        recentlyChangedQueue.add(new RecentlyChangedItem(lease));
        registrant.setLastUpdatedTimestamp();
        invalidateCache(registrant.getAppName(), registrant.getVIPAddress(), registrant.getSecureVipAddress());
        logger.info("Registered instance {}/{} with status {} (replication={})",
                registrant.getAppName(), registrant.getId(), registrant.getStatus(), isReplication);
    } finally {
        read.unlock();
    }
}

【分析二】：发现这个方法有点长，大致阅读，主要更新了注册表的时间之外，还更新了缓存等其它东西，大家有兴趣的可以深究阅读该方法；
```

### 4.４ 跳出来我们接着看上面的　replicateToPeers(Action.Register, info.getAppName(), info.getId(), info, null, isReplication)　的这个方法。
``` 
private void replicateToPeers(Action action, String appName, String id,
                              InstanceInfo info /* optional */,
                              InstanceStatus newStatus /* optional */, boolean isReplication) {
    Stopwatch tracer = action.getTimer().start();
    try {
        if (isReplication) {
            numberOfReplicationsLastMin.increment();
        }
        // If it is a replication already, do not replicate again as this will create a poison replication
		// 注释：如果已经复制过，就不再复制  
        if (peerEurekaNodes == Collections.EMPTY_LIST || isReplication) {
            return;
        }

		// 遍历Eureka Server集群中的所有节点，进行复制操作 
        for (final PeerEurekaNode node : peerEurekaNodes.getPeerEurekaNodes()) {
            // If the url represents this host, do not replicate to yourself.
            if (peerEurekaNodes.isThisMyUrl(node.getServiceUrl())) {
                continue;
            }
			// 没有复制过，遍历Eureka Server集群中的node节点，依次操作，包括取消、注册、心跳、状态更新等。
            replicateInstanceActionsToPeers(action, appName, id, info, newStatus, node);
        }
    } finally {
        tracer.stop();
    }
}

【分析一】：走到这里，我不难理解，每当有注册请求，首先更新 EurekaServer 的注册表，然后再将信息同步到其它EurekaServer的节点上去；

【分析二】：接下来我们看看 node 节点是如何进行复制操作的，进入 replicateInstanceActionsToPeers 方法。

private void replicateInstanceActionsToPeers(Action action, String appName,
                                             String id, InstanceInfo info, InstanceStatus newStatus,
                                             PeerEurekaNode node) {
    try {
        InstanceInfo infoFromRegistry = null;
        CurrentRequestVersion.set(Version.V2);
        switch (action) {
            case Cancel:
                node.cancel(appName, id);
                break;
            case Heartbeat:
                InstanceStatus overriddenStatus = overriddenInstanceStatusMap.get(id);
                infoFromRegistry = getInstanceByAppAndId(appName, id, false);
                node.heartbeat(appName, id, infoFromRegistry, overriddenStatus, false);
                break;
            case Register:
                node.register(info);
                break;
            case StatusUpdate:
                infoFromRegistry = getInstanceByAppAndId(appName, id, false);
                node.statusUpdate(appName, id, newStatus, infoFromRegistry);
                break;
            case DeleteStatusOverride:
                infoFromRegistry = getInstanceByAppAndId(appName, id, false);
                node.deleteStatusOverride(appName, id, infoFromRegistry);
                break;
        }
    } catch (Throwable t) {
        logger.error("Cannot replicate information to {} for action {}", node.getServiceUrl(), action.name(), t);
    }
}

【分析三】：节点之间的复制状态操作，都在这里体现的淋漓尽致，那么我们就拿 Register 类型 node.register(info) 来看，我们来看看 node 究竟是
如何做到同步信息的，进入 node.register(info) 方法看看；
```

### 4.5 进入 PeerEurekaNode.register(final InstanceInfo info) 方法，一窥究竟如何同步数据。
``` 
public void register(final InstanceInfo info) throws Exception {
	// 注释：任务过期时间给任务分发器处理，默认时间偏移当前时间 30秒
    long expiryTime = System.currentTimeMillis() + getLeaseRenewalOf(info);
    batchingDispatcher.process(
            taskId("register", info),
            new InstanceReplicationTask(targetHost, Action.Register, info, null, true) {
                public EurekaHttpResponse<Void> execute() {
                    return replicationClient.register(info);
                }
            },
            expiryTime
    );
}

【分析一】：这里涉及到了 Eureka 的任务批处理，通常情况下Peer之间的同步需要调用多次，如果EurekaServer一多的话，那么将会有很多http请求，所
以自然而然的孕育出了任务批处理，但是也在一定程度上导致了注册和下线的一些延迟，突出优势的同时也势必会造成一些劣势，但是这些延迟情况还是能符合
常理在容忍范围之内的。

【分析二】：在 expiryTime 超时时间之内，批次处理要做的事情就是合并任务为一个List，然后发送请求的时候，将这个批次List直接打包发送请求出去，这样的话，在这个批次的List里面，可能包含取消、注册、心跳、状态等一系列状态的集合List。

【分析三】：我们再接着看源码，batchingDispatcher.process 这么一调用，然后我们就直接看这个 TaskDispatchers.createBatchingTaskDispatcher 方法。

public static <ID, T> TaskDispatcher<ID, T> createBatchingTaskDispatcher(String id,
                                                                             int maxBufferSize,
                                                                             int workloadSize,
                                                                             int workerCount,
                                                                             long maxBatchingDelay,
                                                                             long congestionRetryDelayMs,
                                                                             long networkFailureRetryMs,
                                                                             TaskProcessor<T> taskProcessor) {
        final AcceptorExecutor<ID, T> acceptorExecutor = new AcceptorExecutor<>(
                id, maxBufferSize, workloadSize, maxBatchingDelay, congestionRetryDelayMs, networkFailureRetryMs
        );
        final TaskExecutors<ID, T> taskExecutor = TaskExecutors.batchExecutors(id, workerCount, taskProcessor, acceptorExecutor);
        return new TaskDispatcher<ID, T>() {
            @Override
            public void process(ID id, T task, long expiryTime) {
                acceptorExecutor.process(id, task, expiryTime);
            }

            @Override
            public void shutdown() {
                acceptorExecutor.shutdown();
                taskExecutor.shutdown();
            }
        };
    }

【分析四】：这里的 process 方法会将任务添加到队列中，有入队列自然有出队列，具体怎么取任务，我就不一一给大家讲解了，我就讲讲最后是怎么触发任务的。进入 final TaskExecutors<ID, T> taskExecutor = TaskExecutors.batchExecutors(id, workerCount, taskProcessor, acceptorExecutor) 这句代码的 TaskExecutors.batchExecutors 方法。

static <ID, T> TaskExecutors<ID, T> batchExecutors(final String name,
                                                   int workerCount,
                                                   final TaskProcessor<T> processor,
                                                   final AcceptorExecutor<ID, T> acceptorExecutor) {
    final AtomicBoolean isShutdown = new AtomicBoolean();
    final TaskExecutorMetrics metrics = new TaskExecutorMetrics(name);
    return new TaskExecutors<>(new WorkerRunnableFactory<ID, T>() {
        @Override
        public WorkerRunnable<ID, T> create(int idx) {
            return new BatchWorkerRunnable<>("TaskBatchingWorker-" +name + '-' + idx, isShutdown, metrics, processor, acceptorExecutor);
        }
    }, workerCount, isShutdown);
}

【分析五】：我们发现 TaskExecutors 类中的 batchExecutors 这个静态方法，有个 BatchWorkerRunnable 返回的实现类，因此我们再次进入 BatchWorkerRunnable 类看看究竟,而且既然是 Runnable，那么势必会有 run 方法。

@Override
public void run() {
    try {
        while (!isShutdown.get()) {
			// 注释：获取信号量释放 batchWorkRequests.release()，返回任务集合列表
            List<TaskHolder<ID, T>> holders = getWork();
            metrics.registerExpiryTimes(holders);

            List<T> tasks = getTasksOf(holders);
			// 注释：将批量任务打包请求Peer节点
            ProcessingResult result = processor.process(tasks);
            switch (result) {
                case Success:
                    break;
                case Congestion:
                case TransientError:
                    taskDispatcher.reprocess(holders, result);
                    break;
                case PermanentError:
                    logger.warn("Discarding {} tasks of {} due to permanent error", holders.size(), workerName);
            }
            metrics.registerTaskResult(result, tasks.size());
        }
    } catch (InterruptedException e) {
        // Ignore
    } catch (Throwable e) {
        // Safe-guard, so we never exit this loop in an uncontrolled way.
        logger.warn("Discovery WorkerThread error", e);
    }
}

【分析六】：这就是我们 BatchWorkerRunnable 类的 run 方法，这里面首先要获取信号量释放，才能获得任务集合，一旦获取到了任务集合的话，那么就直接调用 processor.process(tasks) 方法请求 Peer 节点同步数据，接下来我们看看 ReplicationTaskProcessor.process 方法；

@Override
public ProcessingResult process(List<ReplicationTask> tasks) {
    ReplicationList list = createReplicationListOf(tasks);
    try {
		// 注释：这里通过 JerseyReplicationClient 客户端对象直接发送list请求数据
        EurekaHttpResponse<ReplicationListResponse> response = replicationClient.submitBatchUpdates(list);
        int statusCode = response.getStatusCode();
        if (!isSuccess(statusCode)) {
            if (statusCode == 503) {
                logger.warn("Server busy (503) HTTP status code received from the peer {}; rescheduling tasks after delay", peerId);
                return ProcessingResult.Congestion;
            } else {
                // Unexpected error returned from the server. This should ideally never happen.
                logger.error("Batch update failure with HTTP status code {}; discarding {} replication tasks", statusCode, tasks.size());
                return ProcessingResult.PermanentError;
            }
        } else {
            handleBatchResponse(tasks, response.getEntity().getResponseList());
        }
    } catch (Throwable e) {
        if (isNetworkConnectException(e)) {
            logNetworkErrorSample(null, e);
            return ProcessingResult.TransientError;
        } else {
            logger.error("Not re-trying this exception because it does not seem to be a network exception", e);
            return ProcessingResult.PermanentError;
        }
    }
    return ProcessingResult.Success;
}

【分析七】：感觉快要见到真相了，所以我们迫不及待的进入 JerseyReplicationClient.submitBatchUpdates(ReplicationList replicationList) 方法一窥究竟。

@Override
public EurekaHttpResponse<ReplicationListResponse> submitBatchUpdates(ReplicationList replicationList) {
    ClientResponse response = null;
    try {
        response = jerseyApacheClient.resource(serviceUrl)
				// 注释：这才是重点，请求目的相对路径，peerreplication/batch/
                .path(PeerEurekaNode.BATCH_URL_PATH)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, replicationList);
        if (!isSuccess(response.getStatus())) {
            return anEurekaHttpResponse(response.getStatus(), ReplicationListResponse.class).build();
        }
        ReplicationListResponse batchResponse = response.getEntity(ReplicationListResponse.class);
        return anEurekaHttpResponse(response.getStatus(), batchResponse).type(MediaType.APPLICATION_JSON_TYPE).build();
    } finally {
        if (response != null) {
            response.close();
        }
    }
}

【分析八】：看到了相对路径地址，我们搜索下"batch"这样的字符串看看有没有对应的接收方法或者被@Path注解进入的；在 eureka-core-1.4.12.jar 这个包下面，果然搜到到了 @Path("batch") 这样的字样，直接进入，发现这是 PeerReplicationResource 类的方法 batchReplication，我们进入这方法看看。

@Path("batch")
@POST
public Response batchReplication(ReplicationList replicationList) {
    try {
        ReplicationListResponse batchResponse = new ReplicationListResponse();
		// 注释：这里将收到的任务列表，依次循环解析处理，主要核心方法在 dispatch 方法中。
        for (ReplicationInstance instanceInfo : replicationList.getReplicationList()) {
            try {
                batchResponse.addResponse(dispatch(instanceInfo));
            } catch (Exception e) {
                batchResponse.addResponse(new ReplicationInstanceResponse(Status.INTERNAL_SERVER_ERROR.getStatusCode(), null));
                logger.error(instanceInfo.getAction() + " request processing failed for batch item "
                        + instanceInfo.getAppName() + '/' + instanceInfo.getId(), e);
            }
        }
        return Response.ok(batchResponse).build();
    } catch (Throwable e) {
        logger.error("Cannot execute batch Request", e);
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
}

【分析九】：看到了循环一次遍历任务进行处理，不知不觉觉得心花怒放，胜利的重点马上就要到来了，我们进入 PeerReplicationResource.dispatch 方法看看。

private ReplicationInstanceResponse dispatch(ReplicationInstance instanceInfo) {
    ApplicationResource applicationResource = createApplicationResource(instanceInfo);
    InstanceResource resource = createInstanceResource(instanceInfo, applicationResource);

    String lastDirtyTimestamp = toString(instanceInfo.getLastDirtyTimestamp());
    String overriddenStatus = toString(instanceInfo.getOverriddenStatus());
    String instanceStatus = toString(instanceInfo.getStatus());

    Builder singleResponseBuilder = new Builder();
    switch (instanceInfo.getAction()) {
        case Register:
            singleResponseBuilder = handleRegister(instanceInfo, applicationResource);
            break;
        case Heartbeat:
            singleResponseBuilder = handleHeartbeat(resource, lastDirtyTimestamp, overriddenStatus, instanceStatus);
            break;
        case Cancel:
            singleResponseBuilder = handleCancel(resource);
            break;
        case StatusUpdate:
            singleResponseBuilder = handleStatusUpdate(instanceInfo, resource);
            break;
        case DeleteStatusOverride:
            singleResponseBuilder = handleDeleteStatusOverride(instanceInfo, resource);
            break;
    }
    return singleResponseBuilder.build();
}

【分析十】：随便抓一个类型，那我们也拿 Register 类型来看，进入 PeerReplicationResource.handleRegister 看看。

private static Builder handleRegister(ReplicationInstance instanceInfo, ApplicationResource applicationResource) {
	// 注释：private static final String REPLICATION = "true"; 定义的一个常量值，而且还是回调 ApplicationResource.addInstance 方法
    applicationResource.addInstance(instanceInfo.getInstanceInfo(), REPLICATION);
    return new Builder().setStatusCode(Status.OK.getStatusCode());
}

【分析十一】：Peer节点的同步旅程终于结束了，最终又回调到了 ApplicationResource.addInstance 这个方法，这个方法在最终是EurekaClient启动后注册调用的方法，然而Peer节点的信息同步也调用了这个方法，仅仅只是通过一个变量 isReplication 为true还是false来判断是否是节点复制。剩下的ApplicationResource.addInstance流程前面已经提到过了，相信大家已经明白了注册的流程是如何扭转的，包括批量任务是如何处理EurekaServer节点之间的信息同步的了。

```


## 五、EurekaClient 启动流程分析

详见 [SpringCloud（第 050 篇）Netflix Eureka 源码深入剖析（下）](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/flow-analysis/Eureka_02.md)


## 六、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























