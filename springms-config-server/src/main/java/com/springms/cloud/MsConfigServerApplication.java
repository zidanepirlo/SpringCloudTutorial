package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * ConfigServer 配置管理微服务。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/28
 *
 */
@SpringBootApplication
@EnableConfigServer
public class MsConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigServerApplication.class, args);
        System.out.println("【【【【【【 ConfigServer微服务 】】】】】】已启动.");
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
 一、ConfigServer 配置管理微服务（正常测试）（添加一个配置文件 application.yml 来做测试）：

 1、注解：EnableConfigServer
 2、编辑 application.yml 文件，注意只填写 cloud.config.server.git.uri 属性；
 3、启动 springms-config-server 模块服务，启动1个端口；

 4、在浏览器输入地址 http://localhost:8220/abc-default.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 5、在浏览器输入地址 http://localhost:8220/abc-default.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 6、在浏览器输入地址 http://localhost:8220/abc-hehui.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 7、在浏览器输入地址 http://localhost:8220/aaa-bbb.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 8、在浏览器输入地址 http://localhost:8220/aaa-bbb.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；

 总结一：按照 /{application}-{profile}.yml 和 /{application}-{profile}.properties 这种规则来测试的，当找不到路径的话，会默认找到 application.yml 文件读取字段内容；

 9、在浏览器输入地址 http://localhost:8220/master/abc-default.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 10、在浏览器输入地址 http://localhost:8220/master/abc-default.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 11、在浏览器输入地址 http://localhost:8220/master/abc-hehui.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 12、在浏览器输入地址 http://localhost:8220/master/aaa-bbb.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 13、在浏览器输入地址 http://localhost:8220/master/aaa-bbb.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 14、在浏览器输入地址 http://localhost:8220/springms-config-server-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；

 总结二：按照 /{label}/{application}-{profile}.yml 和 /{label}/{application}-{profile}.properties 这种规则来测试，当找不到路径的话，会默认找到 application.yml 文件读取字段内容；
 总结三：所以不管怎么测试路径规则，找不到的话，反正也不会抛什么异常，反正一律会映射到 application.yml 文件上；
 ****************************************************************************************/




/****************************************************************************************
 二、ConfigServer 配置管理微服务（再添加一个配置文件 foobar-dev.yml 来做测试）：

 1、注解：EnableConfigServer
 2、编辑 application.yml 文件，注意只填写 cloud.config.server.git.uri 属性；
 3、启动 springms-config-server 模块服务，启动1个端口；

 4、在浏览器输入地址 http://localhost:8220/foobar-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 5、在浏览器输入地址 http://localhost:8220/foobar-dev.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 6、在浏览器输入地址 http://localhost:8220/master/foobar-dev.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；
 7、在浏览器输入地址 http://localhost:8220/master/foobar-dev.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-dev）；

 总结一：按照 /{application}-{profile}.yml 和 /{application}-{profile}.properties
 /{label}/{application}-{profile}.yml 和 /{label}/{application}-{profile}.properties
 这种规则来测试的，会找到 foobar-dev.yml 文件，既然找到了 foobar-dev.yml 文件的话，那自然就没 application.yml 文件什么事情了；

 8、在浏览器输入地址 http://localhost:8220/foobar-aaa.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 9、在浏览器输入地址 http://localhost:8220/foobar-aaa.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 10、在浏览器输入地址 http://localhost:8220/master/foobar-aaa.yml 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；
 11、在浏览器输入地址 http://localhost:8220/master/foobar-aaa.properties 正常情况下会输出配置文件的内容（内容为：profile: profile-default）；

 总结二：将dev改成aaa的话，按照配置服务的路径规则配置，当路径中的配置文件不再url的目录下的话，那么则会找到默认配置的文件 application.yml 来加载显示。
 ****************************************************************************************/



