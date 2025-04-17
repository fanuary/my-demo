package com.demo.producer.constant;

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
