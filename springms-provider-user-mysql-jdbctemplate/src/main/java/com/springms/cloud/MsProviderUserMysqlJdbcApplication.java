package com.springms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 链接Mysql数据库,通过JdbcTemplate编写数据库访问。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
@SpringBootApplication
public class MsProviderUserMysqlJdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProviderUserMysqlJdbcApplication.class, args);
		System.out.println("【【【【【【 链接MysqlJdbc数据库微服务 】】】】】】已启动.");
	}
}




/****************************************************************************************
 一、链接Mysql数据库,通过JdbcTemplate编写数据库访问：

 1、启动 springms-provider-user-mysql-jdbctemplate 模块服务，启动1个端口；
 2、在浏览器输入地址 http://localhost:8315/simplejdbc/6 可以看到用户ID=10的信息成功的被打印出来；

 3、使用 IDEA 自带工具 Test Restful WebService 发送 HTTP POST 请求,并添加 username、age、balance三个参数，然后执行请求，并去 mysql 数据库查看数据是否存在，正常情况下 mysql 数据库刚刚插入的数据成功了:
 4、使用 REST Client 执行 "/simplejdbc/list" 接口，也正常将 mysql 数据库中所有的用户信息打印出来了；

 5、然后再操作 http://localhost:8315/account/ 该链接的接口操作，也是可以正常操作访问的；
 ****************************************************************************************/

