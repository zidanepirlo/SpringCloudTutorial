# Mysql 安装步骤
-


## 一、安装步骤

### 1.1 下载相关安装包
``` 
进入地址：https://dev.mysql.com/downloads/mysql/，然后选择Redhat 7.0版本；
点击“MySQL-5.6.37-1.el7.x86_64.rpm-bundle.tar”选项下载，即下载地址为：https://cdn.mysql.com//Downloads/MySQL-5.6/MySQL-5.6.37-1.el7.x86_64.rpm-bundle.tar；
```


### 1.2 解压下载的压缩包
``` 
    # [root@svr01 ~]# cd /home/install/
    # [root@svr01 ~]# mkdir -p /home/src/mysql
    # [root@svr01 install]# tar -zxvf MySQL-5.6.37-1.el7.x86_64.rpm-bundle.tar -C ../src/mysql

```

### 1.3 卸载 mariadb
``` 
	# 搜索 mariadb，执行：rpm -qa | grep mariadb
	# 删除搜索出来的东西，执行：rpm -e -–nodeps mariadb-libs-XXXXX.x86_64
	# 如果实在找不到搜索出来的东西话，执行：whereis mariadb 后，也可以找出mariadb相关内容，然后全部删除即可；
```

### 1.4 查看本机是否曾经装过 MySql，执行：mysql -V
``` 
	# 如果找不到命令则没有安装过，如果安装过，则会打印版本号，那么先删除以前的配置，以下执行删除mysql操作：
	# 查看已经安装的服务：执行：rpm -qa|grep -i mysql
			$ rpm -qa|grep -i mysql
			MySQL-server-5.6.14-1.el6.x86_64
			MySQL-devel-5.6.14-1.el6.x86_64
			MySQL-shared-5.6.14-1.el6.x86_64
			MySQL-embedded-5.6.14-1.el6.x86_64
			MySQL-shared-compat-5.6.14-1.el6.x86_64
			MySQL-client-5.6.14-1.el6.x86_64
	# 查看安装过的目录：
			$ whereis mysql
			mysql: /usr/bin/mysql /usr/lib64/mysql /usr/include/mysql /usr/share/mysql /usr/share/man/man1/mysql.1.gz				
	# 删除所有服务和目录：
			$ rpm -e MySQL-server-5.6.14-1.el6.x86_64
			$ rpm -e MySQL-devel-5.6.14-1.el6.x86_64
			$ rpm -e MySQL-shared-5.6.14-1.el6.x86_64
			$ rpm -e MySQL-embedded-5.6.14-1.el6.x86_64
			$ rpm -e MySQL-shared-compat-5.6.14-1.el6.x86_64
			$ rpm -e MySQL-client-5.6.14-1.el6.x86_64
			$ rm -rf /usr/bin/mysql
			$ rm -rf /usr/lib64/mysql
			$ rm -rf /usr/include/mysql
			$ rm -rf /usr/share/mysql
			$ rm -rf /usr/share/man/man1/mysql.1.gz
			
			$ rm -rf /usr/my.cnf
			$ rm -rf /root/.mysql_sercret
			$ rm -rf /var/lib/mysql
```


### 1.5 开始安装 Mysql
``` 
    # [root@svr01 install]# cd ../src/mysql
    # [root@svr01 mysql]# rpm -ivh MySQL-server-5.6.37-1.el7.x86_64.rpm
    # [root@svr01 mysql]# rpm -ivh MySQL-devel-5.6.37-1.el7.x86_64.rpm
    # [root@svr01 mysql]# rpm -ivh MySQL-shared-5.6.37-1.el7.x86_64.rpm
    # [root@svr01 mysql]# rpm -ivh MySQL-embedded-5.6.37-1.el7.x86_64.rpm
    # [root@svr01 mysql]# rpm -ivh MySQL-shared-compat-5.6.37-1.el7.x86_64.rpm
    # [root@svr01 mysql]# rpm -ivh MySQL-client-5.6.37-1.el7.x86_64.rpm
```


### 1.6 检查版本，执行：mysql -V
``` 
    # [root@svr01 mysql]# mysql -V
	mysql  Ver 14.14 Distrib 5.6.37, for Linux (x86_64) using  EditLine wrapper	
```


### 1.7 检查一下mysql的运行状态
``` 
    # [root@svr01 mysql]# /etc/rc.d/init.d/mysql status
	MySQL running (31512)

	注意：启动/关闭mysql数据库可以使用命令/etc/rc.d/init.d/mysql start/stop
```



### 1.8 配置mysql,登录mysql并修改root密码
``` 
		# [root@svr01 mysql]# mysql -u root –p
		有时候全新安装的mysql会无法登录报错：
		ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using password: NO)
		解决办法：
			# 停止服务，执行：service mysql stop  或 /etc/rc.d/init.d/mysql stop
			# 查看 /etc/my.conf 下这个文件是否存在，如果不存在，则新建该文件；
			$ vi /etc/my.conf
				dir=/usr/local/mysql/data

				socket=/var/lib/mysql/mysql.sock

				[mysql.server]
				user=mysql
				basedir=/usr/local/mysql

				#If there is not currently a section called [client], add one at the bottom of the file and copy the socket= line under the [mysqld] section such as:

				[client]
				socket=/var/lib/mysql/mysql.sock	
```



### 1.9 饶过密码登录，设置密码
``` 
		# [root@svr01 mysql]# mysqld_safe --user=mysql --skip-grant-tables --skip-networking & mysql -u root mysql
		mysql> use mysql;
		Database changed
		mysql> UPDATE mysql.user SET password=PASSWORD('root') WHERE User='root';
		Query OK, 4 rows affected (0.00 sec)
		Rows matched: 4  Changed: 4  Warnings: 0

		mysql> quit
		Bye
```



### 1.10 重启数据库服务
``` 
    # [root@svr01 mysql]# service mysql restart
	或者
	# [root@svr01 mysql]# /etc/rc.d/init.d/mysql restart
```



### 1.11 到此为此密码修改完成
``` 
    到此为此密码修改完成
```


### 1.12 再次尝试一下修改密码
``` 
		# [root@svr01 mysql]# mysql -u root –p
		Enter password:

		mysql> SET PASSWORD = PASSWORD('rootp');
		Query OK, 0 rows affected (0.00 sec)
		创建一个全局用户是其可以远程登录数据库
		mysql> use mysql
		Reading table information for completion of table and column names
		You can turn off this feature to get a quicker startup with -A

		Database changed

		mysql> select user,host,password from user;
		+------+--------------------+-------------------------------------------+
		| user | host               | password                                  |
		+------+--------------------+-------------------------------------------+
		| root | localhost          | *BAE53432D2D88E8BDF355F685CFDDBE5891A7B22 |
		| root | MB-OEL6.4-Mysql5.6 | *BAE53432D2D88E8BDF355F685CFDDBE5891A7B22 |
		| root | 127.0.0.1          | *BAE53432D2D88E8BDF355F685CFDDBE5891A7B22 |
		| root | ::1                | *BAE53432D2D88E8BDF355F685CFDDBE5891A7B22 |
		+------+--------------------+-------------------------------------------+
		4 rows in set (0.00 sec)

		mysql> GRANT ALL PRIVILEGES ON *.* TO 'mysqladmin'@'%' IDENTIFIED BY 'rootp';
		Query OK, 0 rows affected (0.00 sec)

		GRANT ALL PRIVILEGES ON *.* TO 'mysqladmin'@'%' IDENTIFIED BY 'rootp';

		在其他机器上面用mysqladmin登录一下如果没有问题就可以了。
		用户名/密码：root/rootp     mysqladmin/ rootp
		GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'rootp';
		
		或者直接创建一个用户供其它用户来访问：
		mysql> CREATE USER 'hadoop'@'hadoop0' IDENTIFIED BY 'hadoop';
		mysql> GRANT ALL PRIVILEGES ON *.* TO 'hadoop'@'hadoop0' WITH GRANT OPTION;
		mysql> exit
		$ mysql -h hadoop0 -u hadoop -p
		mysql> show databases;
```






















