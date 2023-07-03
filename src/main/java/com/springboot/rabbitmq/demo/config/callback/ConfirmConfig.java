//package com.springboot.rabbitmq.demo.config.message;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.ReturnedMessage;
//import org.springframework.amqp.rabbit.connection.CorrelationData;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
///**
// * @author liugp_oup
// * @email liugp@si-tech.com.cn
// * @create 2023-03-07 9:42
// * @desc 回调
// */
//@Slf4j
//@Component
//public class ConfirmConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback{
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    //方法注入
//    @PostConstruct
//    private void sendMessage(){
//        rabbitTemplate.setConfirmCallback(this);
//        rabbitTemplate.setReturnsCallback(this);
//        /*
//         * 当mandatory标志位设置为true时
//         * 如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息
//         * 那么broker会调用basic.return方法将消息返还给生产者
//         * 当mandatory设置为false时，出现上述情况broker会直接将消息丢弃
//         */
//        rabbitTemplate.setMandatory(true);
//    }
//    /**
//     * 交换机是否收到消息的一个回调方法：是否到达队列不知道
//     * CorrelationData：消息相关数据
//     * ack：交换机是否收到消息
//     * cause：未收到消息的原因
//     */
//    @Override
//    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//        if (correlationData != null){
//            String exchange = correlationData.getReturned().getExchange();
//            String routingKey = correlationData.getReturned().getRoutingKey();
//            if (ack){
//                log.info("消息确认回调机制——>交换机成功接收到了来自交换机：{}，路由：{}的消息",exchange,routingKey);
//            }else {
//                log.info("未收到来自交换机：{}，路由：{}的消息,原因是{},",exchange,routingKey,cause);
//            }
//        }
//    }
//    /*
//    * 消息是否到达队列的回调方法
//    * */
//    @Override
//    public void returnedMessage(ReturnedMessage returnedMessage) {
//        String routingKey = returnedMessage.getRoutingKey();
//        log.info("路由:{}不可达",routingKey);
//    }
//
//}
