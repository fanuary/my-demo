# RabbitMQ

## 基础知识

对于**消息队列**而言，功能无非是一方发送消息，消息经过消息队列被另一方接收到

发送的这一方叫做生产者**producer**

接受的这一方叫做消费者**consumer**

而按发送接受的方式不同，分为了四种模式：

- **Direct Exchange**（直连模式）
  精确匹配 routing key。

  适合场景：一个队列处理一个任务，比如下单 → 创建订单队列

- **Fanout Exchange**（发布订阅模式）
  不看 routing key，**群发广播**给所有绑定的队列。

  适合场景：广播通知、新闻、IM 群聊、系统更新。

-  **Topic Exchange**（主题模式）
  支持通配符 `*`（一个词）、`#`（多个词）

  灵活筛选消息投递范围。

  适合：日志分级、功能模块间通信。

- **Headers Exchange**（头部匹配模式）
  用 routing key，**根据消息头部属性进行匹配**。

  队列和交换机的绑定时指定 header 条件。

  适合：更复杂或高维度的路由（例如不同地区、语言、用户等级等）

| 交换机类型  | 名称说明           | 路由方式                               | 是否用 routing key | 使用场景举例                           |
| ----------- | ------------------ | -------------------------------------- | ------------------ | -------------------------------------- |
| **direct**  | 直连交换机         | 精确匹配 routing key                   | ✅ 是               | 点对点传输，如订单创建消息             |
| **fanout**  | 扇形（广播）交换机 | 广播到所有绑定的队列                   | ❌ 否               | 消息推送、系统通知                     |
| **topic**   | 主题交换机         | 通配符匹配 routing key                 | ✅ 是               | 日志系统、模块化路由                   |
| **headers** | 头部交换机         | 根据 header 属性匹配（非 routing key） | ❌ 否               | 多维复杂规则匹配，如权限、语言、版本等 |

这四种模式中，较为常用的是direct、fanout和topic

## SpringBoot集成RabbitMQ

分两个模块，分别模拟消费者和生产者

### 前期准备

1. 在服务器或者本地安装**RabbitMQ**

2. 创建项目，导入依赖

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-amqp</artifactId>
   </dependency>
   ```

3. 配置文件

   ```yaml
   spring:
     application:
       name: rabbitmq-producer
     rabbitmq:
       host: 
       port: 
       username: 
       password: 
       virtual-host: /
       publisher-confirm-type: correlated # 开启消息发送端确认
       publisher-returns: true # 开启消息抵达队列后确认
       template:
         mandatory: true # 只要抵达队列，异步回调确认
       listener:
         simple:
           acknowledge-mode: manual # 消费端手动确认消息
   ```

4. 常量类

   ```java
   public class RabbitMQConstant {
   
       public static final String FANOUT_QUEUE_NAME = "fanout_test_queue";
       public static final String FANOUT_EXCHANGE_NAME = "fanout_test_exchange";
       public static final String TOPIC_QUEUE_NAME = "topic_test_queue";
       public static final String TOPIC_EXCHANGE_NAME = "topic_test_exchange";
   
       /**
        * routing key是支持通配符的，这里的.*表示匹配所有以product开头的routing key
        */
       public static final String TOPIC_ROUTING_KEY = "product.*";
   
   }
   ```

   

### 消费者端

1. 创建配置类，注册交换机、队列等组件·

   1. **fanout**模式配置

      ```java
      @Configuration
      public class MyRabbitFanoutConfig {
      
      
      
          /**
           * 以下配置只需要在代码中完成，程序启动后，会自动在RabbitMQ中创建交换机、队列、绑定关系
           */
      
          /**
           * 声明交换机
           */
          @Bean
          public FanoutExchange fanoutExchange() {
              return new FanoutExchange(RabbitMQConstant.FANOUT_EXCHANGE_NAME, true, false);
          }
          /**
           * 声明队列
           */
          @Bean
          public Queue fanoutQueue() {
              return new Queue(RabbitMQConstant.FANOUT_QUEUE_NAME, true, false, false);
          }
      
          /**
           * 声明绑定关系，将交换机和队列绑定起来
           */
          @Bean
          public Binding fanoutBinding(Queue fanoutQueue, FanoutExchange fanoutExchange) {
              return BindingBuilder.bind(fanoutQueue).to(fanoutExchange);
          }
      }
      ```

   2. **topic**模式配置

      ```java
      @Configuration
      public class MyRabbitTopicConfig {
      
          /**
           * 以下配置只需要在代码中完成，程序启动后，会自动在RabbitMQ中创建交换机、队列、绑定关系
           */
      
          /**
           * 声明交换机
           */
          @Bean
          public TopicExchange topicExchange() {
              return new TopicExchange(RabbitMQConstant.TOPIC_EXCHANGE_NAME, true, false);
          }
          /**
           * 声明队列
           */
          @Bean
          public Queue topicQueue() {
              return new Queue(RabbitMQConstant.TOPIC_QUEUE_NAME, true, false, false);
          }
      
          /**
           * 声明绑定关系，将交换机和队列绑定起来
           */
          @Bean
          public Binding topicBinding(Queue topicQueue, TopicExchange topicExchange) {
              return BindingBuilder
                  .bind(topicQueue).to(topicExchange).with(RabbitMQConstant.TOPIC_ROUTING_KEY);
          }
      }
      ```

      这两种模式都需要在消费者端声明交换机、队列，不同的是在Topic模式下，队列越交换机绑定时还需要指定`RoutingKey`

2. 创建消费监听器

   ```java
   @Component
   public class MyConsumer {
   
       /**
        * 消费者监听队列，处理接收到的消息
        *
        * @param message
        */
       @RabbitListener(queues = RabbitMQConstant.FANOUT_QUEUE_NAME) // 指定监听的队列，当有消息到达队列时，该方法将被调用
       public void processFanoutMessage(Message message) {
           byte[] body = message.getBody();
           System.out.println("Received fanout message: " + new String(body));
       }
   
       @RabbitListener(queues = RabbitMQConstant.TOPIC_QUEUE_NAME) // 指定监听的队列，当有消息到达队列时，该方法将被调用
       public void processTopicMessage(Message message) {
           byte[] body = message.getBody();
           System.out.println("Received topic message: " + new String(body));
       }
   }
   ```

完成以上两步后，就可以直接启动程序，消费者会一直监听队列，一有消息来就马上处理

### 生产者端

生产者端的配置文件和消费者端差别不大，完全可以直接复制过来，删除其中声明队列和绑定关系的部分。因为在生产者端，只关心往哪个交换机发发消息

```java
/**
 * 以下基于fanout模式配置
 */
@Configuration
public class MyRabbitFanoutConfig {



    /**
     * 以下配置只需要在代码中完成，程序启动后，会自动在RabbitMQ中创建交换机、队列、绑定关系
     */

    /**
     * 声明交换机
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitMQConstant.FANOUT_EXCHANGE_NAME, true, false);
    }
}
```

```java
/**
 * 以下基于topic模式配置
 */
@Configuration
public class MyRabbitTopicConfig {

    /**
     * 以下配置只需要在代码中完成，程序启动后，会自动在RabbitMQ中创建交换机、队列、绑定关系
     */

    /**
     * 声明交换机
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitMQConstant.TOPIC_EXCHANGE_NAME, true, false);
    }
}
```

这里用**Test**用例来模拟发送消息：

```java
@SpringBootTest
class ProducerApplicationTests {

    /**
     * 注入RabbitTemplate,会自动加载配置文件中配置好的RabbitMQ相关信息
     */
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * fanout模式测试
     */
    @Test
    void testFanoutSendMessages() {
        rabbitTemplate.convertAndSend(RabbitMQConstant.FANOUT_EXCHANGE_NAME, "", "Hello RabbitMQ!");
    }

    /**
     * topic模式测试
     */
    @Test
    void testTopicSendMessages() {
        //  TOPIC_ROUTING_KEY = "product.*", 匹配所有以"product."开头的routing key
        rabbitTemplate.convertAndSend(RabbitMQConstant.TOPIC_EXCHANGE_NAME, "product.create", "Hello RabbitMQ!");
    }

}
```

消费者端控制台输出：

```shell
Received fanout message: Hello RabbitMQ!
Received topic message: Hello RabbitMQ!
```

### 手动ACK

为什么需要ACK？因为只有消费者确认了消息，消息才算处理成功，被正确消费，否则就需要判断是否需要消息重发等操作

springboot默认会自动ACK，需要在配置文件中设置为手动：

```yaml
spring:
  application:
    name: rabbitmq-producer
  rabbitmq:
    host: 
    port: 
    username: 
    password: 
    virtual-host: /
    publisher-confirm-type: correlated # 开启消息发送端确认
    publisher-returns: true # 开启消息抵达队列后确认
    template:
      mandatory: true # 只要抵达队列，异步回调确认
    listener:
      simple:
        acknowledge-mode: manual # 消费端手动确认消息
```

手动ACK发生在消费者端，修改之前的代码：

```java
    /**
     * 消费者监听队列，处理接收到的消息
     *
     * @param message
     */
    @RabbitListener(queues = RabbitMQConstant.FANOUT_QUEUE_NAME) // 指定监听的队列，当有消息到达队列时，该方法将被调用
    public void processFanoutMessage(Message message, Channel channel) {
        try {
            byte[] body = message.getBody();
            // 手动ACK，通过告知broker要签收对的消息的ID来实现，消息的ID就是deliveryTag
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("Received fanout message: " + new String(body));
            System.out.println("Message acked:" + message.getMessageProperties().getDeliveryTag());
        } catch (IOException e) {
            // TODO 可选消息是否重回队列
            throw new RuntimeException(e);
        }
    }
```

控制台打印：

```
Received fanout message: Hello RabbitMQ!
Message acked:2
```

