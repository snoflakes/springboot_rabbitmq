package com.springboot.rabbitmq.demo.config.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author liugp_oup
 * @email liugp@si-tech.com.cn
 * @create 2023-03-08 10:00
 * @desc RabbitMqTemplate工具封装
 */
@Slf4j
@Configuration
public class RabbitTemplateConfig {
        @Bean
        public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
            RabbitTemplate rabbitTemplate = new RabbitTemplate();
            rabbitTemplate.setConnectionFactory(connectionFactory);
            /*
             * 当mandatory标志位设置为true时
             * 如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息
             * 那么broker会调用basic.return方法将消息返还给生产者
             * 当 mandatory 设置为false时，出现上述情况broker会直接将消息丢弃
             **/
            rabbitTemplate.setMandatory(true);//与setReturnsCallback对应
            /*
             * 交换机是否收到消息的一个回调方法（注：并不关心该消息是否到达队列）
             * CorrelationData：消息相关数据
             * ack：交换机是否收到消息
             * cause：未收到消息的原因
             **/
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                String exchange = "";
                String routingKey = "";
                if (correlationData != null){
                    exchange = Objects.requireNonNull(correlationData.getReturned()).getExchange();
                    routingKey = correlationData.getReturned().getRoutingKey();
                }
                if (ack){
                    log.info("消息确认回调机制——>交换机成功接收到了来自交换机：{}，路由：{}的消息",exchange,routingKey);
                }else {
                    log.info("未收到来自交换机：{}，路由：{}的消息,原因是{},",exchange,routingKey,cause);
                }
            });
            /*
             * 消息是否到达队列的回调方法：若配置了备用交换机，该配置优先级低于备份交换机
             * */
            rabbitTemplate.setReturnsCallback(returnedMessage -> {
                String routingKey = returnedMessage.getRoutingKey();
                log.info("交换机{}的路由:{}不可达",returnedMessage.getExchange(),routingKey);
            });
            return rabbitTemplate;
        }

        /**
         * 消费者数量，默认10
         */
        public static final int DEFAULT_CONCURRENT = 10;
        /**
         * 每个消费者获取最大投递数量 默认50
         */
        public static final int DEFAULT_PREFETCH_COUNT = 50;
        @Bean("pointTaskContainerFactory")
        public SimpleRabbitListenerContainerFactory pointTaskContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setPrefetchCount(DEFAULT_PREFETCH_COUNT);
            factory.setConcurrentConsumers(DEFAULT_CONCURRENT);
            configurer.configure(factory, connectionFactory);
            return factory;
        }
}
