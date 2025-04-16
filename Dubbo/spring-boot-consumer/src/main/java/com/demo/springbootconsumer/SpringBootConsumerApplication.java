package com.demo.springbootconsumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class SpringBootConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootConsumerApplication.class, args);
	}

}
