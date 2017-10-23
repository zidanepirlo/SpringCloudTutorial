# SpringCloud（第 050 篇）Netflix Eureka 源码深入剖析（下）
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

详见 [SpringCloud（第 049 篇）Netflix Eureka 源码深入剖析（上）](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/flow-analysis/Eureka_01.md)

## 四、EurekaServer 处理服务注册、集群数据复制

详见 [SpringCloud（第 049 篇）Netflix Eureka 源码深入剖析（上）](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/flow-analysis/Eureka_01.md)


## 五、EurekaClient 启动流程分析

### 5.1 调换运行模式，Run运行 springms-discovery-eureka 服务，Debug 运行 springms-provider-user 服务，先观察日志先；
``` 
2017-10-23 19:43:07.688  INFO 1488 --- [           main] o.s.c.support.DefaultLifecycleProcessor  : Starting beans in phase 0
2017-10-23 19:43:07.694  INFO 1488 --- [           main] o.s.c.n.eureka.InstanceInfoFactory       : Setting initial instance status as: STARTING
2017-10-23 19:43:07.874  INFO 1488 --- [           main] c.n.d.provider.DiscoveryJerseyProvider   : Using JSON encoding codec LegacyJacksonJson
2017-10-23 19:43:07.874  INFO 1488 --- [           main] c.n.d.provider.DiscoveryJerseyProvider   : Using JSON decoding codec LegacyJacksonJson
2017-10-23 19:43:07.971  INFO 1488 --- [           main] c.n.d.provider.DiscoveryJerseyProvider   : Using XML encoding codec XStreamXml
2017-10-23 19:43:07.971  INFO 1488 --- [           main] c.n.d.provider.DiscoveryJerseyProvider   : Using XML decoding codec XStreamXml
2017-10-23 19:43:08.134  INFO 1488 --- [           main] c.n.d.s.r.aws.ConfigClusterResolver      : Resolving eureka endpoints via configuration
2017-10-23 19:43:08.344  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Disable delta property : false
2017-10-23 19:43:08.344  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Single vip registry refresh property : null
2017-10-23 19:43:08.344  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Force full registry fetch : false
2017-10-23 19:43:08.344  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Application is null : false
2017-10-23 19:43:08.344  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Registered Applications size is zero : true
2017-10-23 19:43:08.344  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Application version is -1: true
2017-10-23 19:43:08.345  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Getting all instance registry info from the eureka server
2017-10-23 19:43:08.630  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : The response status is 200
2017-10-23 19:43:08.631  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Starting heartbeat executor: renew interval is: 30
2017-10-23 19:43:08.634  INFO 1488 --- [           main] c.n.discovery.InstanceInfoReplicator     : InstanceInfoReplicator onDemand update allowed rate per min is 4
2017-10-23 19:43:08.637  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Discovery Client initialized at timestamp 1508758988637 with initial instances count: 0
2017-10-23 19:43:08.657  INFO 1488 --- [           main] c.n.e.EurekaDiscoveryClientConfiguration : Registering application springms-provider-user with eureka with status UP
2017-10-23 19:43:08.658  INFO 1488 --- [           main] com.netflix.discovery.DiscoveryClient    : Saw local status change event StatusChangeEvent [timestamp=1508758988658, current=UP, previous=STARTING]
2017-10-23 19:43:08.659  INFO 1488 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_SPRINGMS-PROVIDER-USER/springms-provider-user:192.168.3.101:7900: registering service...
2017-10-23 19:43:08.768  INFO 1488 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 7900 (http)
2017-10-23 19:43:08.768  INFO 1488 --- [           main] c.n.e.EurekaDiscoveryClientConfiguration : Updating port to 7900
2017-10-23 19:43:08.773  INFO 1488 --- [           main] c.s.cloud.MsProviderUserApplication      : Started MsProviderUserApplication in 9.694 seconds (JVM running for 10.398)
【【【【【【 用户微服务 】】】】】】已启动.

【分析一】：根据日志粗粒度看，大多数日志都是在 DiscoveryClient 打印出来的，由此我们先不妨将这些打印日志的地方都打上断点，为了后序
断点查看调用堆栈信息。

【分析二】：仔细查看下日志，先是 DefaultLifecycleProcessor 类处理了一些 bean,然后接下来肯定会调用一些实现 SmartLifecycle 类的
 start 方法；

【分析三】： 接着初始化设置了EurekaClient的状态为 STARTING，初始化编码使用的格式，哪些用JSON，哪些用XML；

【分析四】： 紧接着打印了强制获取注册信息状态为false，已注册的应用大小为0，客户端发送心跳续约，心跳续约间隔为30秒，最后打印Client
初始化完成；

【分析五】：带着这些通过日志查看出来的端倪，然后我们还得吸取分析EurekaServer的教训，我们得先去 @EnableEurekaClient 注解瞧瞧。
```


### 5.2 有目的性的先去 MsProviderUserApplication 看看，链接点进 EnableEurekaClient 瞧瞧。
``` 
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableDiscoveryClient
public @interface EnableEurekaClient {

}

【分析一】：我们会发现，@EnableEurekaClient 注解类竟然也使用了注解 @EnableDiscoveryClient，那么我们有必要去这个注解类看看。

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EnableDiscoveryClientImportSelector.class)
public @interface EnableDiscoveryClient {

}

【分析二】：我们看到的是 @EnableDiscoveryClient 注解类有个比较特殊的注解 @Import，由此我们猜想，这里的大多数逻辑是不是都写在这个 EnableDiscoveryClientImportSelector 类呢？
```


### 5.3 进入 EnableDiscoveryClientImportSelector 看看到底做了些啥？
``` 
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class EnableDiscoveryClientImportSelector
		extends SpringFactoryImportSelector<EnableDiscoveryClient> {

	@Override
	protected boolean isEnabled() {
		return new RelaxedPropertyResolver(getEnvironment()).getProperty(
				"spring.cloud.discovery.enabled", Boolean.class, Boolean.TRUE);
	}

	@Override
	protected boolean hasDefaultFactory() {
		return true;
	}

}

【分析一】：EnableDiscoveryClientImportSelector 类集成了 SpringFactoryImportSelector 类，但是重写了一个 isEnabled() 方
法，默认值返回 true，为什么会返回true，也得有个说法吧，于是我们进入父类 EnableDiscoveryClientImportSelector 看看。

/**
 * Select and return the names of which class(es) should be imported based on
 * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
 */
@Override
public String[] selectImports(AnnotationMetadata metadata) {
	if (!isEnabled()) { // 打上断点
		return new String[0];
	}
	AnnotationAttributes attributes = AnnotationAttributes.fromMap(
			metadata.getAnnotationAttributes(this.annotationClass.getName(), true));

	Assert.notNull(attributes, "No " + getSimpleName() + " attributes found. Is "
			+ metadata.getClassName() + " annotated with @" + getSimpleName() + "?");

	// Find all possible auto configuration classes, filtering duplicates
	List<String> factories = new ArrayList<>(new LinkedHashSet<>(SpringFactoriesLoader
			.loadFactoryNames(this.annotationClass, this.beanClassLoader)));

	if (factories.isEmpty() && !hasDefaultFactory()) {
		throw new IllegalStateException("Annotation @" + getSimpleName()
				+ " found, but there are no implementations. Did you forget to include a starter?");
	}

	if (factories.size() > 1) {
		// there should only ever be one DiscoveryClient, but there might be more than
		// one factory
		log.warn("More than one implementation " + "of @" + getSimpleName()
				+ " (now relying on @Conditionals to pick one): " + factories);
	}

	return factories.toArray(new String[factories.size()]);
}

【分析二】：发现父类有这么一个 selectImports 方法使用了 isEnabled() 方法，这个方法干了些啥事情呢？我们细看下 selectImports 方法上面的英文注释，大致意思是：选择并且返回需要导入经过注解配置的类，由此我们猜想这个导入的类肯定对我们此次客户端分析有莫大的帮助，于
是我们现在这个方法打上断点先。于是我们现在该干的事情也干了，没有头绪的时候，我们现在才Run运行EurekaServer，Debug运行springms-provider-user。
```


### 5.4 EnableDiscoveryClientImportSelector.selectImports 这个方法果然进断点了。
``` 
【分析一】：既然进了断点，我们看看这个方法，首先通过注解获取了一些属性，然后加载了一些类名称，于是我们进入 loadFactoryNames 方法看看。

public static List<String> loadFactoryNames(Class<?> factoryClass, ClassLoader classLoader) {
	String factoryClassName = factoryClass.getName();
	try {
		// 注释：public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
		// 注释：这个 jar 包下的一个配置文件
		Enumeration<URL> urls = (classLoader != null ? classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
				ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
		List<String> result = new ArrayList<String>();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			Properties properties = PropertiesLoaderUtils.loadProperties(new UrlResource(url));
			String factoryClassNames = properties.getProperty(factoryClassName);
			result.addAll(Arrays.asList(StringUtils.commaDelimitedListToStringArray(factoryClassNames)));
		}
		return result;
	}
	catch (IOException ex) {
		throw new IllegalArgumentException("Unable to load [" + factoryClass.getName() +
				"] factories from location [" + FACTORIES_RESOURCE_LOCATION + "]", ex);
	}
}

【分析二】：加载了一个配置文件，配置文件里面写了啥呢？打开SpringFactoryImportSelector该文件所在的jar包的spring.factories文件一看。

# AutoConfiguration
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.cloud.client.CommonsClientAutoConfiguration,\
org.springframework.cloud.client.discovery.noop.NoopDiscoveryClientAutoConfiguration,\
org.springframework.cloud.client.hypermedia.CloudHypermediaAutoConfiguration,\
org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration,\
org.springframework.cloud.commons.util.UtilAutoConfiguration


# Environment Post Processors
org.springframework.boot.env.EnvironmentPostProcessor=\
org.springframework.cloud.client.HostInfoEnvironmentPostProcessor

【分析三】：看名称，都是一些 Configuration 后缀的类名，所以这些都是加载的一堆堆的配置文件类。于是我们继续断点往下走，发现 
factories 对象里面只有一个类名路径为 org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration 。看这个
名字就应该知道这是我们分析EurekaClient的一个重要的配置类，先不管三七二十一，找到该类先。
```


### 5.5 进入 EurekaDiscoveryClientConfiguration 看看，这个配置类有哪些重要的方法？
``` 
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass(EurekaClientConfig.class)
@ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
@CommonsLog
public class EurekaDiscoveryClientConfiguration implements SmartLifecycle, Ordered {

	@Override
	public void start() {
		// only set the port if the nonSecurePort is 0 and this.port != 0
		if (this.port.get() != 0 && this.instanceConfig.getNonSecurePort() == 0) {
			this.instanceConfig.setNonSecurePort(this.port.get());
		}

		// only initialize if nonSecurePort is greater than 0 and it isn't already running
		// because of containerPortInitializer below
		if (!this.running.get() && this.instanceConfig.getNonSecurePort() > 0) {

			maybeInitializeClient();

			if (log.isInfoEnabled()) {
				log.info("Registering application " + this.instanceConfig.getAppname()
						+ " with eureka with status "
						+ this.instanceConfig.getInitialStatus());
			}

			this.applicationInfoManager
					.setInstanceStatus(this.instanceConfig.getInitialStatus());

			if (this.healthCheckHandler != null) {
				this.eurekaClient.registerHealthCheck(this.healthCheckHandler);
			}
			this.context.publishEvent(
					new InstanceRegisteredEvent<>(this, this.instanceConfig));
			this.running.set(true);
		}
	}

	。。。 其它省略了
｝

【分析一】：进入这个类，首先看到该类实现了 SmartLifecycle 接口，那么就肯定会实现 start 方法，而且这个 start 方法感觉应在 “步骤5.1之分析二” 会被加载执行的。

【分析二】：因为 start 这段代码不多，所以我就索性将 start 方法中的每段代码都点进去看了看，发现 this.applicationInfoManager.setInstanceStatus(this.instanceConfig.getInitialStatus()) 这段代码有一个观察者模式的回调存在。

// ApplicationInfoManager.setInstanceStatus 的方法
public synchronized void setInstanceStatus(InstanceStatus status) {// 打上断点
    InstanceStatus prev = instanceInfo.setStatus(status);
    if (prev != null) {
        for (StatusChangeListener listener : listeners.values()) {
            try {
                listener.notify(new StatusChangeEvent(prev, status));
            } catch (Exception e) {
                logger.warn("failed to notify listener: {}", listener.getId(), e);
            }
        }
    }
}

【分析三】：这个方法会因为状态的改变而回调所有实现 StatusChangeListener 这个类的地方，前提得先注册到 listeners 中去才行。

【分析四】：于是乎，我们断定，若想要回调，那么就必须有地方先注册这个事件，而且这个注册还必须提前执行在 start 方法前执行，于是我们得先
在 ApplicationInfoManager 这个类中找到注册到 listeners 的这个方法。

public void registerStatusChangeListener(StatusChangeListener listener) {// 打上断点
    listeners.put(listener.getId(), listener);
}

【分析五】：没错，就是这个方法，肯定有地方调用这个方法，不然的话，那调用 setInstanceStatus 这个方法的意义就什么用了。于是我们逆向找
下 registerStatusChangeListener 被调用的地方。

【分析六】：很不巧的是，尽然只有1个地方被调用，这个地方就是 DiscoveryClient.initScheduledTasks 方法，而且 initScheduledTasks 
方法又是在 DiscoveryClient 的构造函数里面调用的，同时我们也对 initScheduledTasks 以及 initScheduledTasks 被调用的构造方法地方
打上断点。
```


### 5.6 由于翻阅代码时间有点久了，因此我们关闭 springms-provider-user 微服务，重新 Debug 运行一下。
``` 
【分析一】：果不其然，EurekaDiscoveryClientConfiguration.start 方法被调用了，紧接着 this.applicationInfoManager.setInstanceStatus(this.instanceConfig.getInitialStatus()) 也进入断点，然后在往下走，又进入的 
DiscoveryClient.initScheduledTasks 方法中的 notify 回调处。

【分析二】：看着断点依次经过我们上述分析的地方，然后也符合日志打印的顺序，所以我们现在应该是有必要好好看看 DiscoveryClient.initScheduledTasks 这个方法究竟干了什么伟大的事情。然而又想了想，还不如看看 initScheduledTasks 被调用的构造方法。
```

### 5.7 进入 DiscoveryClient 经过 @Inject 注解过的构造方法。
``` 
@Inject
DiscoveryClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config, DiscoveryClientOptionalArgs args, Provider<BackupRegistry> backupRegistryProvider) {
    if (args != null) {
        this.healthCheckHandlerProvider = args.healthCheckHandlerProvider;
        this.healthCheckCallbackProvider = args.healthCheckCallbackProvider;
        this.eventListeners.addAll(args.getEventListeners());
    } else {
        this.healthCheckCallbackProvider = null;
        this.healthCheckHandlerProvider = null;
    }
    
    this.applicationInfoManager = applicationInfoManager;
    InstanceInfo myInfo = applicationInfoManager.getInfo();

    clientConfig = config;
    staticClientConfig = clientConfig;
    transportConfig = config.getTransportConfig();
    instanceInfo = myInfo;
    if (myInfo != null) {
        appPathIdentifier = instanceInfo.getAppName() + "/" + instanceInfo.getId();
    } else {
        logger.warn("Setting instanceInfo to a passed in null value");
    }

    this.backupRegistryProvider = backupRegistryProvider;

    this.urlRandomizer = new EndpointUtils.InstanceInfoBasedUrlRandomizer(instanceInfo);
    localRegionApps.set(new Applications());

    fetchRegistryGeneration = new AtomicLong(0);

    remoteRegionsToFetch = new AtomicReference<String>(clientConfig.fetchRegistryForRemoteRegions());
    remoteRegionsRef = new AtomicReference<>(remoteRegionsToFetch.get() == null ? null : remoteRegionsToFetch.get().split(","));

    if (config.shouldFetchRegistry()) {
        this.registryStalenessMonitor = new ThresholdLevelsMetric(this, METRIC_REGISTRY_PREFIX + "lastUpdateSec_", new long[]{15L, 30L, 60L, 120L, 240L, 480L});
    } else {
        this.registryStalenessMonitor = ThresholdLevelsMetric.NO_OP_METRIC;
    }

    if (config.shouldRegisterWithEureka()) {
        this.heartbeatStalenessMonitor = new ThresholdLevelsMetric(this, METRIC_REGISTRATION_PREFIX + "lastHeartbeatSec_", new long[]{15L, 30L, 60L, 120L, 240L, 480L});
    } else {
        this.heartbeatStalenessMonitor = ThresholdLevelsMetric.NO_OP_METRIC;
    }

    if (!config.shouldRegisterWithEureka() && !config.shouldFetchRegistry()) {
        logger.info("Client configured to neither register nor query for data.");
        scheduler = null;
        heartbeatExecutor = null;
        cacheRefreshExecutor = null;
        eurekaTransport = null;
        instanceRegionChecker = new InstanceRegionChecker(new PropertyBasedAzToRegionMapper(config), clientConfig.getRegion());

        // This is a bit of hack to allow for existing code using DiscoveryManager.getInstance()
        // to work with DI'd DiscoveryClient
        DiscoveryManager.getInstance().setDiscoveryClient(this);
        DiscoveryManager.getInstance().setEurekaClientConfig(config);

        initTimestampMs = System.currentTimeMillis();

        logger.info("Discovery Client initialized at timestamp {} with initial instances count: {}",
                initTimestampMs, this.getApplications().size());
        return;  // no need to setup up an network tasks and we are done
    }

    try {
		// 注释：定时任务调度准备
        scheduler = Executors.newScheduledThreadPool(3,
                new ThreadFactoryBuilder()
                        .setNameFormat("DiscoveryClient-%d")
                        .setDaemon(true)
                        .build());

		// 注释：实例化心跳定时任务线程池
        heartbeatExecutor = new ThreadPoolExecutor(
                1, clientConfig.getHeartbeatExecutorThreadPoolSize(), 0, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("DiscoveryClient-HeartbeatExecutor-%d")
                        .setDaemon(true)
                        .build()
        );  // use direct handoff

		// 注释：实例化缓存刷新定时任务线程池
        cacheRefreshExecutor = new ThreadPoolExecutor(
                1, clientConfig.getCacheRefreshExecutorThreadPoolSize(), 0, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("DiscoveryClient-CacheRefreshExecutor-%d")
                        .setDaemon(true)
                        .build()
        );  // use direct handoff

        eurekaTransport = new EurekaTransport();
        scheduleServerEndpointTask(eurekaTransport, args);

        AzToRegionMapper azToRegionMapper;
        if (clientConfig.shouldUseDnsForFetchingServiceUrls()) {
            azToRegionMapper = new DNSBasedAzToRegionMapper(clientConfig);
        } else {
            azToRegionMapper = new PropertyBasedAzToRegionMapper(clientConfig);
        }
        if (null != remoteRegionsToFetch.get()) {
            azToRegionMapper.setRegionsToFetch(remoteRegionsToFetch.get().split(","));
        }
        instanceRegionChecker = new InstanceRegionChecker(azToRegionMapper, clientConfig.getRegion());
    } catch (Throwable e) {
        throw new RuntimeException("Failed to initialize DiscoveryClient!", e);
    }

    if (clientConfig.shouldFetchRegistry() && !fetchRegistry(false)) {
        fetchRegistryFromBackup();
    }

	// 注释：初始化调度任务
    initScheduledTasks();
    try {
        Monitors.registerObject(this);
    } catch (Throwable e) {
        logger.warn("Cannot register timers", e);
    }

    // This is a bit of hack to allow for existing code using DiscoveryManager.getInstance()
    // to work with DI'd DiscoveryClient
    DiscoveryManager.getInstance().setDiscoveryClient(this);
    DiscoveryManager.getInstance().setEurekaClientConfig(config);

    initTimestampMs = System.currentTimeMillis();
    logger.info("Discovery Client initialized at timestamp {} with initial instances count: {}",
            initTimestampMs, this.getApplications().size());
}

【分析一】：从往下看，initScheduledTasks 这个方法顾名思义就是初始化调度任务，所以这里面的内容应该就是重头戏，进入看看。

private void initScheduledTasks() {
    if (clientConfig.shouldFetchRegistry()) {
        // registry cache refresh timer
		// 注释：间隔多久去拉取服务注册信息，默认时间 30秒
        int registryFetchIntervalSeconds = clientConfig.getRegistryFetchIntervalSeconds();
        int expBackOffBound = clientConfig.getCacheRefreshExecutorExponentialBackOffBound();
		// 注释：定时任务，每间隔 30秒 去拉取一次服务注册信息
        scheduler.schedule(
                new TimedSupervisorTask(
                        "cacheRefresh",
                        scheduler,
                        cacheRefreshExecutor,
                        registryFetchIntervalSeconds,
                        TimeUnit.SECONDS,
                        expBackOffBound,
                        new CacheRefreshThread()
                ),
                registryFetchIntervalSeconds, TimeUnit.SECONDS);
    }

    if (clientConfig.shouldRegisterWithEureka()) {
		// 注释：间隔多久发送一次心跳续约，默认间隔时间 30 秒
        int renewalIntervalInSecs = instanceInfo.getLeaseInfo().getRenewalIntervalInSecs();
        int expBackOffBound = clientConfig.getHeartbeatExecutorExponentialBackOffBound();
        logger.info("Starting heartbeat executor: " + "renew interval is: " + renewalIntervalInSecs);

        // Heartbeat timer
		// 注释：定时任务，每间隔 30秒 去想 EurekaServer 发送一次心跳续约
        scheduler.schedule(
                new TimedSupervisorTask(
                        "heartbeat",
                        scheduler,
                        heartbeatExecutor,
                        renewalIntervalInSecs,
                        TimeUnit.SECONDS,
                        expBackOffBound,
                        new HeartbeatThread()
                ),
                renewalIntervalInSecs, TimeUnit.SECONDS);

        // InstanceInfo replicator
		// 注释：实例信息复制器，定时刷新dataCenterInfo数据中心信息，默认30秒
        instanceInfoReplicator = new InstanceInfoReplicator(
                this,
                instanceInfo,
                clientConfig.getInstanceInfoReplicationIntervalSeconds(),
                2); // burstSize

		// 注释：实例化状态变化监听器
        statusChangeListener = new ApplicationInfoManager.StatusChangeListener() {
            @Override
            public String getId() {
                return "statusChangeListener";
            }

            @Override
            public void notify(StatusChangeEvent statusChangeEvent) {
                if (InstanceStatus.DOWN == statusChangeEvent.getStatus() ||
                        InstanceStatus.DOWN == statusChangeEvent.getPreviousStatus()) {
                    // log at warn level if DOWN was involved
                    logger.warn("Saw local status change event {}", statusChangeEvent);
                } else {
                    logger.info("Saw local status change event {}", statusChangeEvent);
                }

				// 注释：状态有变化的话，会回调这个方法
                instanceInfoReplicator.onDemandUpdate();
            }
        };

		// 注释：注册状态变化监听器
        if (clientConfig.shouldOnDemandUpdateStatusChange()) {
            applicationInfoManager.registerStatusChangeListener(statusChangeListener);
        }

        instanceInfoReplicator.start(clientConfig.getInitialInstanceInfoReplicationIntervalSeconds());
    } else {
        logger.info("Not registering with Eureka server per configuration");
    }
}

【分析二】：在这个方法从上往下一路注释分析下来，干了EurekaClient我们最想知道的一些事情，定时任务获取注册信息，定时任务刷新缓存，定时
任务心跳续约，定时任务同步数据中心数据，状态变化监听回调等。但是唯独没看到注册，这是怎么回事呢？

【分析三】：我们忘记了一个重要的方法，instanceInfoReplicator.onDemandUpdate() 就是在状态改变的时候，我们是如何处理的？由此，我们觉得这里面肯定有猫腻，不然没办法注册呀。

public boolean onDemandUpdate() {
    if (rateLimiter.acquire(burstSize, allowedRatePerMinute)) {
        scheduler.submit(new Runnable() {
            @Override
            public void run() {
                logger.debug("Executing on-demand update of local InstanceInfo");

                Future latestPeriodic = scheduledPeriodicRef.get();
                if (latestPeriodic != null && !latestPeriodic.isDone()) {
                    logger.debug("Canceling the latest scheduled update, it will be rescheduled at the end of on demand update");
                    latestPeriodic.cancel(false);
                }

				// 注释：这里进行了实例信息刷新和注册
                InstanceInfoReplicator.this.run();
            }
        });
        return true;
    } else {
        logger.warn("Ignoring onDemand update due to rate limiter");
        return false;
    }
}

【分析四】：onDemandUpdate 这个方法，看来看去，唯独 InstanceInfoReplicator.this.run() 这个方法还有点用，而且还是 run 方法呢，感情 InstanceInfoReplicator 这个类还是实现了 Runnable 接口？经过查看这个类，还真是实现了 Runnable 接口。

【分析五】：于是乎，我们有理由相信，这个方法应该我们要找的注册所在的地方，翻开代码看看究竟。

public void run() {
    try {
        discoveryClient.refreshInstanceInfo();

        Long dirtyTimestamp = instanceInfo.isDirtyWithTime();
        if (dirtyTimestamp != null) {
            discoveryClient.register();
            instanceInfo.unsetIsDirty(dirtyTimestamp);
        }
    } catch (Throwable t) {
        logger.warn("There was a problem with the instance info replicator", t);
    } finally {
        Future next = scheduler.schedule(this, replicationIntervalSeconds, TimeUnit.SECONDS);
        scheduledPeriodicRef.set(next);
    }
}

【分析六】：映入眼帘的就是 discoveryClient.register() 这个刺眼的 register 方法，终于有点苗头了，原来注册方法找的这么千辛万苦。虽然找到了这里，但是我还是想看看这个让我们找的千辛万苦的注册方法到底是怎么注册的呢？

boolean register() throws Throwable {
    logger.info(PREFIX + appPathIdentifier + ": registering service...");
    EurekaHttpResponse<Void> httpResponse;
    try {
        httpResponse = eurekaTransport.registrationClient.register(instanceInfo);
    } catch (Exception e) {
        logger.warn("{} - registration failed {}", PREFIX + appPathIdentifier, e.getMessage(), e);
        throw e;
    }
    if (logger.isInfoEnabled()) {
        logger.info("{} - registration status: {}", PREFIX + appPathIdentifier, httpResponse.getStatusCode());
    }
    return httpResponse.getStatusCode() == 204;
}

【分析七】：原来调用了 EurekaHttpClient 封装的客户端请求对象来进行注册的，再继续深探 registrationClient.register 方法，于是我们来到了 AbstractJerseyEurekaHttpClient.register 方法。

@Override
public EurekaHttpResponse<Void> register(InstanceInfo info) {
    String urlPath = "apps/" + info.getAppName();
    ClientResponse response = null;
    try {
        Builder resourceBuilder = jerseyClient.resource(serviceUrl).path(urlPath).getRequestBuilder();
        addExtraHeaders(resourceBuilder);
        response = resourceBuilder
                .header("Accept-Encoding", "gzip")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON)
				// 注释：打包带上当前应用的所有信息 info
                .post(ClientResponse.class, info);
        return anEurekaHttpResponse(response.getStatus()).headers(headersOf(response)).build();
    } finally {
        if (logger.isDebugEnabled()) {
            logger.debug("Jersey HTTP POST {}/{} with instance {}; statusCode={}", serviceUrl, urlPath, info.getId(),
                    response == null ? "N/A" : response.getStatus());
        }
        if (response != null) {
            response.close();
        }
    }
}

【分析八】：原来调用的是 Jersey RESTful 框架来进行请求的，然后在 EurekaServer 那边就会在 ApplicationResource.addInstance 方法接收客户端的注册请求，因此我们的 EurekaClient 是如何注册的就到此为止了。

【分析九】：至于那些续约、心跳的流程分析和这个注册的流程大体差不多，相信大家按照我刚刚这么分析断点下去，一定能分析的很到位的。
```


## 六、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























