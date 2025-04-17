package com.demo.consumer.consumer;

import com.demo.consumer.constant.RabbitMQConstant;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author tfan
 * @Description 消费者，由于接受消息
 * @Date 2025/4/17 13:34
 **/

@Component
public class MyConsumer {

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

    @RabbitListener(queues = RabbitMQConstant.TOPIC_QUEUE_NAME) // 指定监听的队列，当有消息到达队列时，该方法将被调用
    public void processTopicMessage(Message message) {
        byte[] body = message.getBody();
        System.out.println("Received topic message: " + new String(body));
    }
}
