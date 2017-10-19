package com.springms.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 链接Mysql数据库简单的集成Mybatis、ehcache框架采用MapperXml访问数据库。
 *
 * 简单用户链接Mysql数据库微服务（通过 mybatis 链接 mysql 并用 MapperXml 编写数据访问，并且通过 EhCache 缓存来访问）。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017-10-19
 *
 */
@SpringBootApplication
@MapperScan("com.springms.cloud.mapper.**")
@EnableCaching
public class MsProviderUserMysqlMybatisMapperEhCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProviderUserMysqlMybatisMapperEhCacheApplication.class, args);
		System.out.println("【【【【【【 链接MysqlMybatisMapperEhCache数据库微服务 】】】】】】已启动.");
	}
}




/****************************************************************************************
 Ehcache 相关资料：

 diskStore：为缓存路径，ehcache分为内存和磁盘两级，此属性定义磁盘的缓存位置。
 defaultCache：默认缓存策略，当ehcache找不到定义的缓存时，则使用这个缓存策略。只能定义一个。
 name:缓存名称。
 maxElementsInMemory:缓存最大数目
 maxElementsOnDisk：硬盘最大缓存个数。
 eternal:对象是否永久有效，一但设置了，timeout将不起作用。
 overflowToDisk:是否保存到磁盘，当系统当机时
 timeToIdleSeconds:设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
 timeToLiveSeconds:设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。
 diskPersistent：是否缓存虚拟机重启期数据 Whether the disk store persists between restarts of the Virtual Machine. The default value is false.diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。
 diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。
 memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）。
 clearOnFlush：内存数量最大时是否清除。
 memoryStoreEvictionPolicy:可选策略有：LRU（最近最少使用，默认策略）、FIFO（先进先出）、LFU（最少访问次数）。


 FIFO，first in first out，先进先出。
 LFU， Less Frequently Used，一直以来最少被使用的。如上面所讲，缓存的元素有一个hit属性，hit值最小的将会被清出缓存。
 LRU，Least Recently Used，最近最少使用的，缓存的元素有一个时间戳，当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存。


 一般情况下，我们在Sercive层进行对缓存的操作。先介绍 Ehcache 在 Spring 中的注解：在支持 Spring Cache 的环境下，
 * @Cacheable : Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。
 * @CacheEvict : 清除缓存。
 * @CachePut : @CachePut也可以声明一个方法支持缓存功能。使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。
 * 这三个方法中都有两个主要的属性：value 指的是 ehcache.xml 中的缓存策略空间；key 指的是缓存的标识，同时可以用 # 来引用参数。
 ****************************************************************************************/




/****************************************************************************************
 注意：Mybatis 需要加上 entity 等注解才可以使用，不然启动都会报错；
 @MapperScan("com.springms.cloud.mapper.**") 或者在每个 Mapper 接口文件上添加 @Mapper 也可以；

 一、简单用户链接Mysql数据库微服务（通过 mybatis 链接 mysql 并用 MapperXml 编写数据访问，并且通过 EhCache 缓存来访问）：

 1、启动 springms-provider-user-mysql-mybatis-mapper-ehcache 模块服务，启动1个端口；
 2、在浏览器输入地址 http://localhost:8385/user/10 可以看到用户ID=10的信息成功的被打印出来；

 3、使用 IDEA 自带工具 Test Restful WebService 发送 HTTP POST 请求,并添加 username、age、balance三个参数，然后执行请求，并去 mysql 数据库查看数据是否存在，正常情况下 mysql 数据库刚刚插入的数据成功了:
 4、使用 REST Client 执行 "/user/ehcache" 接口，也正常将 mysql 数据库中所有的用户信息打印出来了，并且打印的信息如下：

 5、在浏览器输入地址 http://localhost:8385/user/ehcache 可以看到如下信息被打印出来；

 2017-10-19 17:48:54.561  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  进行Encache缓存测试
 2017-10-19 17:48:54.610 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : ==>  Preparing: select * from user
 2017-10-19 17:48:54.629 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : ==> Parameters:
 2017-10-19 17:48:54.657 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : <==      Total: 65
 2017-10-19 17:48:54.658  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ====生成第一个用户====
 2017-10-19 17:48:54.661 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.insertUser  : ==>  Preparing: INSERT INTO user ( username, name, age, balance ) VALUES ( ?, ?, ?, ? )
 2017-10-19 17:48:54.662 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.insertUser  : ==> Parameters: user66(String), user66(String), 1000(Integer), 1000(String)
 2017-10-19 17:48:54.678 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.insertUser  : <==    Updates: 1
 2017-10-19 17:48:54.691 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : ==>  Preparing: select * from user
 2017-10-19 17:48:54.692 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : ==> Parameters:
 2017-10-19 17:48:54.707 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findAllUsers    : <==      Total: 66
 2017-10-19 17:48:54.708  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  第一次查询
 2017-10-19 17:48:54.714 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : ==>  Preparing: select * from user where id = ?
 2017-10-19 17:48:54.714 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : ==> Parameters: 147(Long)
 2017-10-19 17:48:54.721 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : <==      Total: 1
 2017-10-19 17:48:54.722  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  第一次查询结果: User{id=147, username='user66', name='user66', age=1000, balance='1000', from=''}
 2017-10-19 17:48:54.722  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 1 次查询
 2017-10-19 17:48:54.723  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 1 次查询结果: User{id=147, username='user66', name='user66', age=1000, balance='1000', from=''}
 2017-10-19 17:48:54.723  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 2 次查询
 2017-10-19 17:48:54.724  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 2 次查询结果: User{id=147, username='user66', name='user66', age=1000, balance='1000', from=''}
 2017-10-19 17:48:54.724  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 3 次查询
 2017-10-19 17:48:54.724  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  通过缓存第 3 次查询结果: User{id=147, username='user66', name='user66', age=1000, balance='1000', from=''}
 2017-10-19 17:48:54.724  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ====准备修改数据====
 2017-10-19 17:48:54.725 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.updateUser  : ==>  Preparing: update user set userName=?,name=?,age=?,balance=? where id=?
 2017-10-19 17:48:54.725 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.updateUser  : ==> Parameters: user66(String), user66(String), 2000(Integer), 2000(String), 147(Long)
 2017-10-19 17:48:54.738 DEBUG 2736 --- [nio-8385-exec-1] c.i.cloud.mapper.IUserMapper.updateUser  : <==    Updates: 1
 2017-10-19 17:48:54.747  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ==== 修改数据 == 成功 ==
 2017-10-19 17:48:54.747  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ====修改后再次查询数据
 2017-10-19 17:48:54.747 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : ==>  Preparing: select * from user where id = ?
 2017-10-19 17:48:54.747 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : ==> Parameters: 147(Long)
 2017-10-19 17:48:54.747 DEBUG 2736 --- [nio-8385-exec-1] c.i.c.mapper.IUserMapper.findUserById    : <==      Total: 1
 2017-10-19 17:48:54.747  INFO 2736 --- [nio-8385-exec-1] erMysqlMybatisMapperXmlEhCacheController : ===========  ====修改后再次查询数据结果: User{id=147, username='user66', name='user66', age=2000, balance='2000', from=''}

 总结：可以看出查询过一次后，就不会再查询数据库了，所以第二次、第三次都会去查找缓存的数据；
 ****************************************************************************************/

