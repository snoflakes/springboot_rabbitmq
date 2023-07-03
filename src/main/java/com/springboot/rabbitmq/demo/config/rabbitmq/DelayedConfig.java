package com.springboot.rabbitmq.demo.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author liugp_oup
 * @email liugp@si-tech.com.cn
 * @create 2023-03-05 16:09
 * @desc 自定义基于插件的：延时队列
 */
@Configuration
public class DelayedConfig {
    public static final String DELAYED_EXCHANGE = "delayed_exchange";
    public static final String DELAYED_QUEUE = "delayed_queue";
    public static final String DELAYED_ROUTING = "delayed_routing";

    //自定义延时交换机
    @Bean
    public CustomExchange delayedExchange(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("x-delayed-type","direct");
        return new CustomExchange(DELAYED_EXCHANGE,"x-delayed-message",true,false,hashMap);
    }
    //自定义延时队列
    @Bean
    public Queue delayedQueue(){
        return QueueBuilder.durable(DELAYED_QUEUE).build();
    }
    //绑定
    @Bean
    public Binding bindingDelayedExchange(@Qualifier("delayedQueue") Queue delayedQueue,
                                          @Qualifier("delayedExchange") CustomExchange delayedExchange){
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTING).noargs();
    }
}
