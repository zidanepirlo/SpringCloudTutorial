# SpringCloud（第 026 篇）简单异构系统之 nodejs 微服务
-

## 一、大致介绍

``` 
1、因为在后面要利用 SpringCloud 集成异构系统，所以才有了本章节的 nodejs 微服务；
2、本章节使用了最简单的 http 请求截取 url 的方式，截取不同 url 的后缀做不同的响应处理，简直 so easy；
```

## 二、实现步骤


### 2.1 添加nodejs服务端js文件（springms-node-service\node-service.js）
``` 
// nodejs 引入 http、url、path模块
var http = require('http');
var url = require("url");
var path = require('path');

// 创建server
var server = http.createServer(function(req, res) {
  // 获得请求的路径
  var pathname = url.parse(req.url).pathname;  
  res.writeHead(200, { 'Content-Type' : 'application/json; charset=utf-8' });
  // 访问http://localhost:8060/，将会返回{"index":"欢迎来到简单异构系统之 nodejs 服务首页"}
  if (pathname === '/') {
    res.end(JSON.stringify({ "index" : "欢迎来到简单异构系统之 nodejs 服务首页" }));
  }
  // 访问http://localhost:8060/health，将会返回{"status":"UP"}
  else if (pathname === '/health.json') {
    res.end(JSON.stringify({ "status" : "UP" }));
  }
  // 其他情况返回404
  else {
    res.end("404");
  }
});

// 创建监听，并打印日志
server.listen(8205, function() {
  console.log('开始监听本地端口: 8205');
});
```




### 2.2 如何启动
``` 
windows窗口执行命令：node.exe node-service.js
```




## 三、测试

``` 
/****************************************************************************************
 一、简单异构系统之 nodejs 微服务：

 1、编写 node-service.js 文件；
 2、启动服务（windows 命令）；
 3、输入 node.exe node-service.js 命令，正常情况下会打印 “开始监听本地端口: 8205”，说明启动成功了；

 注意：至于 node.exe 这个命令要下载什么安装包什么的，请大家移步寻找度娘，相信大家的聪明才智很快就可以搞定这个命令的最简单用法；

 4、新起网页页签，输入 http://localhost:8205/ ，然后打印信息为：{"index":"欢迎来到简单异构系统之 nodejs 服务首页"}
 5、新起网页页签，输入 http://localhost:8205/health.json ，然后打印信息为：{"status":"UP"}
 5、新起网页页签，输入 http://localhost:8205/abc ，然后打印信息为：404

 总结：简单的 nodejs 微服务，处理客户端请求就是如此的简单，所以市场上也有好多服务端就是用nodejs玩的；
 ****************************************************************************************/
```


## 四、下载地址

``` 
https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git

SpringCloudTutorial交流QQ群: 235322432

欢迎关注，您的肯定是对我最大的支持!!!
```






























