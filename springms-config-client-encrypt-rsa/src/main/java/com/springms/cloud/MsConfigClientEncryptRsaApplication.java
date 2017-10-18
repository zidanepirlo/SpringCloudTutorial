package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 配置客户端ConfigClient链接经过RSA加解密的配置微服务。
 *
 * 配置服务客户端Client应用入口（链接经过 RSA 非对称加解密的配置微服务）（专门为测试经过 RSA 非加解密的配置微服务 springms-config-server-encrypt-rsa 微服务模块）。<br/>
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
public class MsConfigClientEncryptRsaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigClientEncryptRsaApplication.class, args);
        System.out.println("【【【【【【 ConfigClientEncryptRsa微服务 】】】】】】已启动.");
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
 一、配置客户端ConfigClient链接经过RSA加解密的配置微服务（链接经过 RSA 非加解密的配置微服务）（专门为测试经过 RSA 非加解密的配置微服务 springms-config-server-encrypt-rsa 微服务模块）：

 1、注解：pom.xml 先添加 configclient 的引用模；
 2、编辑 bootstrap.yml 文件，注意注释 profile 属性，然后添加相关客户端配置；
     spring:
         cloud:
             config:
                 uri: http://localhost:8265  # 链接 springms-config-client-encrypt-rsa 微服务
                 profile: stg1rsa  # 选择stg1rsa配置文件
                 label: master #当 ConfigServer 的后端存储的是 Git 的时候，默认就是 master

         application:
            name: foobar  #取 foobar-dev.yml 这个文件的 application 名字，即为 foobar 名称
 3、启动 springms-config-server-encrypt-rsa 模块服务，启动1个端口；
 4、启动 springms-config-client-encrypt-rsa 模块服务，启动1个端口；

 5、在浏览器输入地址 http://localhost:8270/profile 正常情况下会输出配置文件的内容（内容为：foobar-stg2rsa）；

 总结：正常打印，说明配置服务客户端不需要做什么加解密的配置，加解密的配置在服务端做就好了；
 ****************************************************************************************/











