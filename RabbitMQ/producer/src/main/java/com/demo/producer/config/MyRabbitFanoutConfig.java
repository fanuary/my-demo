package com.demo.producer.config;


import com.demo.producer.constant.RabbitMQConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author tfan
 * @Description
 * @Date 2025/4/17 13:28
 **/

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
