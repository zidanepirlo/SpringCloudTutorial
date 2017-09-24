# Nginx 安装步骤 & 配置文件详解
-


## 一、安装步骤

### 1.1 下载 nginx 相关安装包
``` 
    nginx 安装包
    # [root@svr01 ~]# wget http://nginx.org/download/nginx-1.12.0.tar.gz

    gzip模块需要zlib库
    # [root@svr01 ~]# wget http://prdownloads.sourceforge.net/libpng/zlib-1.2.11.tar.gz

    rewrite模块需要pcre库
    # [root@svr01 ~]# wget https://ftp.pcre.org/pub/pcre/pcre-8.41.tar.gz

    SSL功能需要openssl库
    # [root@svr01 ~]# wget https://www.openssl.org/source/openssl-1.0.2l.tar.gz
```


### 1.2 解压下载的压缩包
``` 
    # [root@svr01 ~]# cd /home/install/
    # [root@svr01 install]# tar -zxvf openssl-1.0.2l.tar.gz -C ../src/
    # [root@svr01 install]# tar -zxvf pcre-8.41.tar.gz -C ../src/
    # [root@svr01 install]# tar -zxvf openssl-1.0.2l.tar.gz -C ../src/
    # [root@svr01 install]# tar -zxvf zlib-1.2.11.tar.gz -C ../src/
    # [root@svr01 install]# tar -zxvf nginx-1.12.0.tar.gz -C ../src/

```

### 1.3 安装 openssl
``` 
    # [root@svr01 install]# cd /home/src/openssl-1.0.2l/
    # [root@svr01 openssl-1.0.2l]# ./config
    # [root@svr01 openssl-1.0.2l]# make && make install

```

### 1.4 安装 gzip
``` 
    # [root@svr01 openssl-1.0.2l]# cd /home/src/zlib-1.2.11/
    # [root@svr01 zlib-1.2.11]# ./configure
    # [root@svr01 zlib-1.2.11]# make && make install
```


### 1.5 安装pcre
``` 
    # [root@svr01 zlib-1.2.11]# cd /home/src/pcre-8.41/
    # [root@svr01 pcre-8.41]# yum install -y gcc gcc-c++
    # [root@svr01 pcre-8.41]# ./configure
    # [root@svr01 pcre-8.41]# make && make install
```


### 1.6 安装 nginx
``` 
    # [root@svr01 pcre-8.41]# cd /home/src/nginx-1.12.0/
    # [root@svr01 nginx-1.12.0]# ./configure --help
    # [root@svr01 nginx-1.12.0]# ./configure --with-pcre=/home/src/pcre-8.41/ --with-zlib=/home/src/zlib-1.2.11/ --with-openssl=/home/src/openssl-1.0.2l/
    # [root@svr01 nginx-1.12.0]# make && make install
```


### 1.7 开启默认80端口
``` 
    如果是阿里云服务器的话，需要在【ECS控制台->安全组->配置规则】里面开启默认80端口；
```



### 1.8 测试 nginx
``` 
    通过 http://ip 即可访问出现nginx的页面。
```



### 1.9 设置开机启动nginx
``` 
    # [root@svr01 nginx-1.12.0]# echo "/usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf" >> /etc/rc.local
    # [root@svr01 ~]# chmod +x /etc/rc.d/rc.local
```



### 1.10 查看nginx是否启动命令
``` 
    # [root@svr01 ~]# netstat -ntlp
```





## 二、配置文件详解

``` 
#定义Nginx运行的用户和用户组
#user  nobody;

#nginx进程数，建议设置为等于CPU总核心数
worker_processes  1;

#全局错误日志定义类型[ debug | info | notice | warn | error | crit ]
#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#进程pid文件
#pid        logs/nginx.pid;

#指定进程可以打开的最大描述符数目
#工作模式与连接数的上限值
#这个指令是指当一个nginx进程打开的最多文件描述符数据，理论值应该是最多打开文件数（ulimit -n）与nginx进程数相除，但是nginx分配请求并不是那么均匀，所以最好与ulimit -n 的值保持一致即可。
#现在linux 2.6内核下开启文件打开数为65535， worker_rlimit_nofile就相应应该填写65536。
#这是因为nginx调度时分配请求到进程并不是那么均衡，所以假如填写10240，总并发量达到3-4万时就有进程可能超过10240了，这时候就会返回 502 错误。 
#worker_rlimit_nofile 65535

events {
    
    #参考时间模型， use [ kqueue | rtsig | epoll | /dev/poll | select | poll ]；epoll模型
    #是Linux 2.6以上版本内核中的高性能网络I/O模型，Linux建议epoll，如果跑在FreeBSD上面，就用kqueue模型。
    #补充说明：
    #与apache相类，nginx针对不同的操作系统，有不同的事件模型：
    #A）标准事件模型

    #B）高效事件模型
    #use epoll;

    #单个进程最大连接数(最大连接数=连接数*进程数)
    #根据硬件调整，和前面工作进程配合起来用，尽量大，但是别把CPU跑到100%就行。每个进程允许的最多连接数，理论上每台nginx服务器的最大连接数为65535.
    worker_connections  1024;

    #keepalive超时时间。
    #keepalive_timeout 60;

    #客户端请求头部的缓冲区大小。这个可以根据你的系统分页大小来设置，一般一个请求头的大小不会超过1K，不过由于一般系统分页都要大于1K，所以这里设置为分页大小。
    #分页大小可以用命令 getconf PAGESIZE 取得。
    #但也有client_header_buffer_size超过4K的情况，但是client_header_buffer_size该值必须设置为“系统分页大小”的整数倍数值。
    #client_header_buffer_size 4k;

    #这个将为打开文件指定缓存，默认是没有启用的，max指定缓存数量，建议和打开文件数一致，inactive是指经过多长时间文件没被请求后删除缓存。
    #open_file_cache max=65535 inactive=60s;

    #这个是指多长时间检查一次缓存的有效信息。
    #open_file_cache_valid 80s;

    #open_file_cache指令中inactive参数时间内文件的最少使用次数，如果超过这个数字，文件描述符一直是缓存中打开的，如上例，如果有一个文件在inactive时间内一次没被使用，它将被移除。
    #open_file_cache_min_uses 1;

    #这个指令指定是否在搜索一个文件是记录cache错误
    #open_file_cache_error on;
}


#设定http服务器
http {

    #文件扩展名与文件类型映射表（conf/mime.types）
    include       mime.types;

    #默认文件类型
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #默认编码
    #charset utf-8

    #access_log  logs/access.log  main;

    #开启高效文件传输模式，sendfile指令指定nginx是否调用sendfile函数来输出文件，对于普通应用设为 on，如果用来进行下载等应用磁盘IO重负载应用，可设置为off，以平衡磁盘与网络IO处理速度，降低系统的负载，降低系统 uptime。注意：如果图片显示不正常的话把这个改成 off。
    sendfile        on;

    #开启目录列表访问，合适下载服务器，默认关闭;
    autoindex on;

    #此选项允许或禁止使用 socket 的 TCP_CORK 的选项，此选项仅在使用 sendfile 的时候使用。
    #tcp_nopush on;

    #长连接超时时间，单位是秒
    #keepalive_timeout  0;
    keepalive_timeout  65;

    #fastCGI相关参数是为了改善网站的性能，减少资源占用，提高访问速度。
    fastcgi_connect_timeout 300;
    fastcgi_send_timeout 300;
    fastcgi_read_timeout 300;
    fastcgi_buffer_size 64k;
    fastcgi_buffer 4 64k;
    fastcgi_busy_buffers_size 128k;
    fastcgi_temp_file_write_size 128k;

    #gzip模块设置
    gzip  on;  #开启gzip压缩输出
    gzip_min_length 1k; #最小压缩文件大小
    gzip_buffers 4 16k; #压缩缓冲区
    gzip_http_version 1.0;  #压缩版本（默认1.1，前端如果是squid2.5请使用1.0）
    gzip_comp_level 6;  #压缩等级，一般设置为6
    gzip_types text/plain application/x-javascript; #压缩类型，默认就已经包含text/xml，即使写了会有一个warn。
    gzip_vary on;

    #开始限制ip连接数的时候需要使用
    #limit_zone crawler $binary_remote_addr 10m;


    #负载均衡配置
    upstream piao.jd.com {
     
        #upstream的负载均衡，weight是权重，可以根据机器配置定义权重。weigth参数表示权值，权值越高被分配到的几率越大。
        server 192.168.80.121:80 weight=3;
        server 192.168.80.122:80 weight=2;
        server 192.168.80.123:80 weight=3;

        #nginx的upstream目前支持4种方式的分配
        #1、轮询（默认）
        #每个请求按时间顺序逐一分配到不同的后端服务器，如果后端服务器down掉，能自动剔除。
        #2、weight
        #指定轮询几率，weight和访问比率成正比，用于后端服务器性能不均的情况。
        #例如：
        #upstream bakend {
        #    server 192.168.0.14 weight=10;
        #    server 192.168.0.15 weight=10;
        #}
        #2、ip_hash
        #每个请求按访问ip的hash结果分配，这样每个访客固定访问一个后端服务器，可以解决session的问题。
        #例如：
        #upstream bakend {
        #    ip_hash;
        #    server 192.168.0.14:88;
        #    server 192.168.0.15:80;
        #}
        #3、fair（第三方）
        #按后端服务器的响应时间来分配请求，响应时间短的优先分配。
        #upstream backend {
        #    server server1;
        #    server server2;
        #    fair;
        #}
        #4、url_hash（第三方）
        #按访问url的hash结果来分配请求，使每个url定向到同一个后端服务器，后端服务器为缓存时比较有效。
        #例：在upstream中加入hash语句，server语句中不能写入weight等其他的参数，hash_method是使用的hash算法
        #upstream backend {
        #    server squid1:3128;
        #    server squid2:3128;
        #    hash $request_uri;
        #    hash_method crc32;
        #}

        #tips:
        #upstream bakend{#定义负载均衡设备的Ip及设备状态}{
        #    ip_hash;
        #    server 127.0.0.1:9090 down;
        #    server 127.0.0.1:8080 weight=2;
        #    server 127.0.0.1:6060;
        #    server 127.0.0.1:7070 backup;
        #}
        #在需要使用负载均衡的server中增加 proxy_pass http://bakend/;

        #每个设备的状态设置为:
        #1.down表示单前的server暂时不参与负载
        #2.weight为weight越大，负载的权重就越大。
        #3.max_fails：允许请求失败的次数默认为1.当超过最大次数时，返回proxy_next_upstream模块定义的错误
        #4.fail_timeout:max_fails次失败后，暂停的时间。
        #5.backup： 其它所有的非backup机器down或者忙的时候，请求backup机器。所以这台机器压力会最轻。

        #nginx支持同时设置多组的负载均衡，用来给不用的server来使用。
        #client_body_in_file_only设置为On 可以讲client post过来的数据记录到文件中用来做debug
        #client_body_temp_path设置记录文件的目录 可以设置最多3层目录
        #location对URL进行匹配.可以进行重定向或者进行新的代理 负载均衡
    }


    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }


    # another virtual host using mix of IP-, name-, and port-based configuration
    #
    #server {
    #    listen       8000;
    #    listen       somename:8080;
    #    server_name  somename  alias  another.alias;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}


    # HTTPS server
    #
    #server {
    #    listen       443 ssl;
    #    server_name  localhost;

    #    ssl_certificate      cert.pem;
    #    ssl_certificate_key  cert.key;

    #    ssl_session_cache    shared:SSL:1m;
    #    ssl_session_timeout  5m;

    #    ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}

}
```






























