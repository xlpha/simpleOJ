# SimpleOJ
学完 Spring Boot 框架后所写的练手项目。前端使用 Vue 框架，后端使用 Spring Boot 框架，数据库使用 MySQL，配合 Redis 作为缓存。后端与判题模块之间通过 RabbitMQ 消息队列解耦。

为了防止用户提交恶意代码，判题核心是在 Docker 容器中运行。判题核心使用C语言编写，通过 Linux 系统调用 wait4 获取进程所使用的时间和内存信息。

演示网址：http://175.24.22.194:8080/

# 实现思路
//todo
