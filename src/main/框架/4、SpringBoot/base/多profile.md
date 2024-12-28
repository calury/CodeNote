Spring Boot 的配置文件共有两种形式：.properties  文件和 .yml 文件，不管哪种形式，它们都能通过文件名的命名形式区分出不同的环境的配置，文件命名格式为：
application-{profile}.properties/yml

其中，{profile} 一般为各个环境的名称或简称，例如 dev、test 和 prod 等等。
properties 配置
在 helloworld 的 src/main/resources 下添加 4 个配置文件：
application.properties：主配置文件
application-dev.properties：开发环境配置文件
application-test.properties：测试环境配置文件
application-prod.properties：生产环境配置文件




激活 Profile
除了可以在配置文件中激活指定 Profile，Spring Boot 还为我们提供了另外 2 种激活 Profile 的方式：
命令行激活
虚拟机参数激活
命令行激活
我们可以将 Spring Boot 项目打包成 JAR 文件，并在通过命令行运行时，配置命令行参数，激活指定的 Profile。











