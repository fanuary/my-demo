package com.demo.producer;

import com.demo.producer.constant.RabbitMQConstant;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

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
