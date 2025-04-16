

# SpringBoot集成Dubbo

## 1. 准备工作

1. 安装zookeeper

   使用zookeeper作为注册中心，Dubbo只要成功连上zookeeper，就可以自动实现注册发现等功能，无需额外操作

2. 导入依赖

```xml
<!-- 提供Dubbo与Spring Boot集成的自动配置能力 -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>3.2.0</version>
</dependency>
<!--提供ZooKeeper客户端高阶封装，简化原生API操作-->
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>5.4.0</version>
</dependency>
<!--提供服务发现与注册的高级API，可用于构建自己的注册中心逻辑-->
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-x-discovery</artifactId>
    <version>5.1.0</version> <!-- 请根据需要选择合适的版本 -->
</dependency>
```

2. ## 基础三模块

作为一个RPC框架，Dubbo最基本的功能就是远程调用方法，在这个过程中，有两个角色：

- 方法提供者-provider
- 方法消费者-consumer

如果将调用的方法独立出来，作为一个本地依赖，则可以有第三个角色：

- 方法本身-interface

接下来依次实现者三个角色，总体目录如下：

```shell

    ├─spring-boot-consumer # 消费者端
    ├─spring-boot-interface # 方法
    └─spring-boot-provider # 提供者端
```

### spring-boot-interface

方法端需要提供所有放的接口，但不用实现。只用于演示，只实现这一个接口

```java
public interface UserService {
    User getUserById(Long id);
}
```

将这个项目提供maven打包，供**spring-boot-provider**和**spring-boot-consumer**使用，需要在这两个项目内导入

```sh
mvn install
```

### spring-boot-provider

导入准备好的本地依赖

```xml
<!--自定义的接口依赖-->
<dependency>
    <groupId>com.demo</groupId>
    <artifactId>spring-boot-interface</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

实现接口

```java
@Slf4j
@DubboService // 标记为远程调用的服务
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Long id) {
        log.info("provider ====> getUserById");
        return new User(1L, "Tom", "123456");
    }
}
```

配置文件：

```yaml
dubbo:
  application:
    name: spring-boot-provider
  registry:
    address: zookeeper://127.0.0.1:2181
    check: false # 启动时不校验注册中心连接
  #  指定Dubbo通信使用的协议名。默认值是 dubbo
  protocol:
    name: dubbo
    port: 20880
spring:
  application:
    name: spring-boot-provider
server:
  port: 8081
```

### spring-boot-consumer

导入本地依赖，同上

调用远程方法：

```java
@RestController
public class UserController {


    @DubboReference(check = false) // dubbo提供的Reference注解，用于调用远程服务
    private UserService userService;

    @GetMapping("/test")
    public String getUser() {
        User userById = userService.getUserById(1L);
        System.out.println(userById);
        return "success";
    }
}
```

配置文件：

```yaml
spring:
  application:
    name: spring-boot-consumer
dubbo:
  application:
    name: spring-boot-consumer
  registry:
    address: zookeeper://127.0.0.1:2181
    check: false # 关闭注册时检查，否则会导致服务启动失败
  protocol:
    name: dubbo
    port: 20881
server:
  port: 8082
```

### 测试

分别启动**spring-boot-consumer**和**spring-boot-provider**，并向consumer端发起`/test`请求，成功调用provider端的具体实现方法

## 过滤器

Dubbo 中的过滤器处于服务调用链的中间层，可用于在服务调用前后进行各种增强处理，可以实现链路追踪、日志记录、访问控制、参数校验等功能，具体见实例代码