package com.springboot.rabbitmq.demo.control;

import com.rabbitmq.client.Channel;
import com.springboot.rabbitmq.demo.config.rabbitmq.DelayedConfig;
import com.springboot.rabbitmq.demo.config.rabbitmq.DeadLetterConfig;
import com.springboot.rabbitmq.demo.config.rabbitmq.TopicConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * @author liugp_oup
 * @email liugp@si-tech.com.cn
 * @create 2023-03-05 15:40
 * @desc
 */
@Slf4j
@Component
public class RabbitListenerConsumer {
    @RabbitListener(queues = DeadLetterConfig.DEAD_LETTER_QUEUE_D)
    public void receiveD(Message message, Channel channel){
        String s = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("接受到的消息是{}：时间{}",s,new Date().toString());

    }

    @RabbitListener(queues = DelayedConfig.DELAYED_QUEUE)
    public void receiveDelayed(Message message,Channel channel) throws Exception{
        String s = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("接受到的消息是{}：时间{}",s,new Date().toString());
    }

    //top -> 正常队列
    @RabbitListener(queues = TopicConfig.TOPIC_QUEUE_B)
    public void receiveTopicMsg(Message message, Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long tag){
        MessageProperties messageProperties = message.getMessageProperties();
        log.info("正常队列接受到的消息是{}：时间{}:路由是：{}",new String(message.getBody(),StandardCharsets.UTF_8),
                new Date().toString(), messageProperties.getReceivedRoutingKey());
        try {
            //具体业务
            Map<String, Object> param = message.getMessageProperties().getHeader("param");
            /* 手动应答:若某条消息处理失败（如消费者宕机），则会自动重新入队
             * 第一个参数：为当前正在处理的消息做个标记
             * 第二个参数：是否批量应答
             *    true批量应答：当第一个消息成功消费后，将队列里的所有消息都应答为消费成功
             *    false不批量应答：为保证消息不丢失，设置成false
             * */
            channel.basicAck(tag,false);
        }catch (Exception e){
            try {
                /*
                * basic.nack方法为不确认deliveryTag对应的消息，第二个参数是否应用于多消息，第三个参数是否重新入队，
                * 与basic.reject区别就是同时支持多个消息，可以nack该消费者先前接收未ack的所有消息。
                * nack后的消息也会被自己消费到。
                * */
                channel.basicNack(tag, false, true);
            }catch (Exception exception){
                log.error("重新放入队列失败，失败原因:{}",e.getMessage(),e);
            }
            log.error("TopicConsumer消费者出错,mq参数:{}，错误信息：{}",message,e.getMessage(),e);
        }
    }
    //topic-> back_queue
//    @RabbitListener(queues = {TopicConfig.DEAD_LETTER_QUEUE})
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = TopicConfig.BACK_QUEUE_A,
                    durable="true"),
            exchange = @Exchange(value = TopicConfig.BACK_EXCHANGE,
                    type= "fanout",
                    ignoreDeclarationExceptions = "true")
    ))
    public void receiveTopicToBackMsg(Message message,Channel channel,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception{
        log.info("接受到的消息是：{}，路由是：{}，当前时间：{}",new String(message.getBody(),StandardCharsets.UTF_8),
                message.getMessageProperties().getReceivedRoutingKey(),
                new Date().toString());
        /*
        * basic.reject方法拒绝deliveryTag对应的消息，第二个参数是否重新入队，true则重新入队列，否则丢弃或者进入死信队列。
        * 该方法reject后，该消费者还是会消费到该条被reject的消息
        * */
        channel.basicReject(tag, false);
    }
    //topic-> dead_letter_queue
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = TopicConfig.DEAD_LETTER_QUEUE,
//                    arguments = {
//                            @Argument(name = "x-dead-letter-exchange",value = "dlx.exchange"), //指定一下死信交换机
//                            @Argument(name = "x-dead-letter-routing-key",value = "dead_letter_routing"),  //指定死信交换机的路由key
//                            @Argument(name = "x-message-ttl",value = "3000",type = "java.lang.Long") //指定队列的过期时间
//                    }),
//            exchange = @Exchange(value = TopicConfig.DEAD_LETTER_EXCHANGE,
//                    type= "direct",
//                    ignoreDeclarationExceptions = "true"),
//            key = "dead_letter_routing"
//    ))
    @RabbitListener(queues = TopicConfig.DEAD_LETTER_QUEUE)
    public void receiveTopicToDirectMsg(Message message,Channel channel,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception{
        log.info("死信队列接受到的消息是：{}，路由是：{}，时间是：{}:",new String(message.getBody(),StandardCharsets.UTF_8),
                message.getMessageProperties().getReceivedRoutingKey(),
                new Date().toString());
        //具体业务
        Map<String, Object> param = message.getMessageProperties().getHeader("param");
        //消费成功，确认消息
        channel.basicAck(tag,false);

    }
}
