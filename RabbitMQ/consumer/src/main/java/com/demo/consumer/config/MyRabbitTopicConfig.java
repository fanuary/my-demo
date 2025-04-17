package com.demo.consumer.config;

import com.demo.consumer.constant.RabbitMQConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author tfan
 * @Description
 * @Date 2025/4/17 14:24
 **/

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
        return BindingBuilder.bind(topicQueue).to(topicExchange).with(RabbitMQConstant.TOPIC_ROUTING_KEY);
    }
}
