package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 配置客户端ConfigClient链接经过对称加解密的配置微服务;<br/>
 *
 * （专门为测试经过对称加解密的配置微服务 springms-config-server-encrypt 微服务模块）。<br/>
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
public class MsConfigClientEncryptApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigClientEncryptApplication.class, args);
        System.out.println("【【【【【【 ConfigClientEncrypt微服务 】】】】】】已启动.");
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
 一、配置服务客户端Client应用入口（链接经过对称加解密的配置微服务）（专门为测试经过对称加解密的配置微服务 springms-config-server-encrypt 微服务模块）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 bootstrap.yml 文件，注意注释 profile 属性，然后添加相关客户端配置；
     spring:
         cloud:
             config:
                 uri: http://localhost:8255  # 链接 springms-config-server-encrypt 微服务
                 profile: prd  # 选择生产配置文件
                 label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

         application:
            name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
 3、启动 springms-config-server-encrypt 模块服务，启动1个端口；
 4、启动 springms-config-client-encrypt 模块服务，启动1个端口；

 5、在浏览器输入地址 http://localhost:8260/profile 正常情况下会输出配置文件的内容（内容为：foobar-prd）；

 总结：正常打印，说明配置服务客户端不需要做什么加解密的配置，加解密的配置在服务端做就好了；
 ****************************************************************************************/











