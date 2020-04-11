# SimpleOJ
学完 Spring Boot 框架后所写的练手项目。目前只实现了最最基础的功能：浏览题目、用户提交代码、查询评测结果

前端使用 Vue 框架，后端使用 Spring Boot 框架，数据库使用 MySQL，配合 Redis 作为缓存。后端与判题模块之间通过 RabbitMQ 消息队列解耦。

为了防止用户提交恶意代码，判题核心是在 Docker 容器中运行。判题核心使用C语言编写，通过 Linux 系统调用 wait4 获取进程所使用的时间和内存信息以及是否异常退出（超时、超内存、段错误等等）。

项目在线演示：http://175.24.22.194:8080/

# 实现思路
Web部分没什么好说的，网上看看Spring Boot和Vue的教程即可，这里主要说一下判题模块部分的逻辑

1. 服务器收到用户的提交记录后，将该记录存到数据库中，且将状态设置成**等待评测**。然后向消息队列中发送一条消息，里面包含该提交记录的id

2. 一个模块用于监听消息队列，当收到一条消息后，根据消息中包含的提交记录id，从数据库中取出该提交记录，将该提交记录以及对应的语言配置信息交给判题模块

3. 判题模块的执行流程如下

   1. 设置该提交记录的状态为**正在评测中**

   2. 根据提交记录的id生成一个临时目录，以下简称cwd

   3. 将用C语言编写的judgeCore（一个可执行程序）复制到cwd目录中，注意Linux下需要设置文件的执行权限

   4. 将用户提交的代码保存到cwd目录中

   5. 尝试编译

      1. 读取对应编程语言的编译命令，如果命令为空则说明该语言不需要编译（比如Python），直接跳过

      2. 生成docker命令，比如对于Java，命令为

         ```
         docker run --rm -v $cwd:$containerCwd -w containerCwd openjdk:8 /bin/sh -c "javac Main.Java"
         --rm 表示容器退出后自动删除该容器
         -v 将服务器的cwd目录映射到容器中的containerCwd目录（比如/tmp）
         -w 设置容器启动后的工作目录
         ```

      3. 使用`ProcessBuilder`执行该命令

      4. 获取该进程的错误输出，如果编译成功，错误输出应该为空。

      5. 一旦错误输出不为空，则说明编译失败，更新最终状态为**编译错误**`CompileError`

   6. 尝试运行

      1. 对于将每条测试用例都保存为文件，如`0-in.txt`、`0-expected.txt`

      2. 生成docker命令，比如对于Java，命令为

         ```
         docker run --rm -v $cwd:$containerCwd -w $containerCwd openjdk:8 /bin/sh -c "./judgeCore 'java Main' 3000 65535000 0-in.txt 0-out.txt"
         judgeCore的5个命令参数分别是 运行命令 时间限制(ms) 内存限制(KB) 输入文件 输出文件
         ```

      3. 使用`ProcessBuilder`执行该命令
      4. `judgeCore`运行完成后会输出`{"status": 0, "timeUsed"：897, "memroyUsed":1234556}`这样的JSON字符串，并将程序的输出结果保存到`0-out.txt`中
      5. 如果`status`不为0则表示运行出错，比如`RuntimeError`、`TimeLimitExceed`、`MemoryLimitExceed`等，则更新数据库中该提交记录的最终状态
      6. 如果`status`为0表示程序运行正常。则比较用户的输出文件`0-out.txt`与标准答案`0-expected.txt`，如果不相等（注意忽略末尾的空白字符）则是`WrongAnswer`。
      7. 对于每条测试用例都需要重复以上步骤，都通过则为`Accept`

   7. 以上步骤任何一步出现异常，则更新最终状态为**服务器内部错误**`ServerInternalError`

4. 对于前端来说，当用户提交代码后，查询评测结果时，每2s向服务器发送查询请求（轮询），直到获取到最终状态......好吧这个确实有点粗暴，如果要优化的话可能需要使用`WebSocket`等技术由服务器主动推送状态吧


