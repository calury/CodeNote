# 基础工具的安装与部署

## nginx部署
Nginx是用C语言编写的，使用前需要编译安装

1、安装依赖包
```
sudo yum -y install openssl openssl-devel pcre pcre-devel zlib zlib-devel gcc gcc-c++
```
2、解压Nginx源码包到/opt/modules
```
tar -zxvf nginx-1.12.2.tar.gz -C /opt/modules/
```
3、进入源码目录，执行以下命令
```
cd /opt/modules/nginx-1.12.2

./configure prefix=/opt/module/nginx 
make && make install
--prefix=要安装到的目录
```
4、启动
```
cd /opt/modules/nginx
sudo sbin/nginx
```
5、查看启动情况
```
ps -ef | grep nginx
```
6、重启
```
sbin/nginx -s reload
```
7、停止
```
sbin/nginx -s stop
```

> nginx占用80端口，默认情况下非root用户不允许使用1024以下端口
> 
> 解决：让当前用户的某个应用也可以使用 1024 以下的端口
```sudo setcap cap_net_bind_service=+eip /opt/modules/nginx/sbin/nginx```

##