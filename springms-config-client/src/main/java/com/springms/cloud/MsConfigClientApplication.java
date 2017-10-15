package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 配置客户端ConfigClient接入配置服务端。<br/>
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/15
 *
 */
@SpringBootApplication
public class MsConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigClientApplication.class, args);
        System.out.println("【【【【【【 ConfigClient微服务 】】】】】】已启动.");
    }
}


/****************************************************************************************
 * 配置服务的路劲规则：
 *
 * /{application}/{profile}[/{label}]
 * /{application}-{profile}.yml
 * /{label}/{application}-{profile}.yml
 * /{application}-{profile}.properties
 * /{label}/{application}-{profile}.properties
 ****************************************************************************************/





/****************************************************************************************
 application.yml 涉及到的链接文件内容展示如下：

 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/application.yml
 profile: profile-default

 http://git.oschina.net/ylimhhmily/OpenSource_CustomCircleLineProgressBar/blob/master/foobar-dev.yml
 profile: profile-dev
 ****************************************************************************************/





/****************************************************************************************
 一、配置客户端ConfigClient接入配置服务端（正常测试 ConfigClient ）：

 1、注解：pom.xml 先删除 configclient 的引用模块，以便测试正常情况 ConfigClientController 接口是否畅通；
 2、编辑 application.yml 文件，注意添加 profile: profile-dev(local) 属性；
 3、启动 springms-config-client 模块服务，启动1个端口；

 4、在浏览器输入地址 http://localhost:8225/profile 正常情况下会输出配置文件的内容（内容为：profile-dev(local)）；

 注意：这里还暂时不需要 bootstrap.yml 配置文件，所以测试一是不需要添加 bootstrap.yml 文件的；
 ****************************************************************************************/





/****************************************************************************************
 二、配置客户端ConfigClient接入配置服务端（链接 ClientServer 测试遇到挫折）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 application.yml 文件，注意注释 profile 属性，然后添加相关客户端配置；
    spring:
        cloud:
            config:
                uri: http://localhost:8220
                profile: dev
                label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

        application:
            name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
 3、启动 springms-config-server 模块服务，启动1个端口；
 4、启动 springms-config-client 模块服务，启动1个端口；

 5、然后发现启动 springms-config-client 模块出现错误，报错信息为：Fetching config from server at: http://localhost:8888, Could not locate PropertySource: I/O error on GET request for "http://localhost:8888/foobar/dev/master": Connection refused；
 6、发现错误信息中，为什么链接的是远端的 8888 端口呢？百思不得其解，难道是默认加载的配置 8888 端口？？？
 7、SpringCloud里面有个“启动上下文”，主要是用于加载远端的配置，也就是加载ConfigServer里面的配置，默认加载顺序为：加载bootstrap.*里面的配置 --> 链接configserver，加载远程配置 --> 加载application.*里面的配置；

 总结：这里需要借助于“启动上下文”来处理加载远程配置，请看下面环节测试三。
 ****************************************************************************************/





/****************************************************************************************
 三、配置客户端ConfigClient接入配置服务端（链接 ClientServer 测试遇到挫折）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 application.yml 文件，注释'测试二'的属性配置；
 3、新建一个 bootstrap.yml 文件，将相关客户端配置挪到 bootstrap.yml 文件即可；
 4、启动 springms-config-server 模块服务，启动1个端口；
 5、启动 springms-config-client 模块服务，启动1个端口；
 6、在浏览器输入地址 http://localhost:8225/profile 正常情况下会输出配置文件的内容（内容为：profile-dev）；

 总结：这里成功获取了远端配置，并成功打印了属性值出来，说明添加 bootstrap.yml 配置文件对我们项目的顺利进行起到了有效的作用。
 ****************************************************************************************/





/****************************************************************************************
 四、配置客户端ConfigClient接入配置服务端（链接 ClientServer 测试，同时本地也有一份配置文件，那么该如何抉择呢？）：

 1、在测试三的基础上，咱们再做点其它配置测试；
 2、在 application.yml 文件，再次添加 profile 属性，看看加载的是本地配置还是远端配置？
 3、停止并重新启动 springms-config-client 模块服务，启动1个端口；
 4、在浏览器输入地址 http://localhost:8225/profile 正常情况下会输出远端服务的配置内容；

 总结：在ConfigServer服务启动的时候，bootstrap 拿到远端配置注入到 profile 的属性中的话，那么就不会再次覆盖这个属性了，所以只会选择远端配置的内容。
      那是不是会有人认为把ConfigServer再重启一下就行了呢？答案是不行的，因为首选的是远端配置内容；
 ****************************************************************************************/











