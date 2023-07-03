package com.springboot.rabbitmq.demo.control;

import com.springboot.rabbitmq.demo.config.rabbitmq.DelayedConfig;
import com.springboot.rabbitmq.demo.config.rabbitmq.DeadLetterConfig;
import com.springboot.rabbitmq.demo.config.rabbitmq.TopicConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author liugp_oup
 * @email liugp@si-tech.com.cn
 * @create 2023-03-05 15:27
 * @desc
 */
@Slf4j
@RestController
//@RequestMapping("/ttl")
public class SendMessageProduct {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //定时deadLetter
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable("message") String msg){
        log.info("当前时间{}：发送两个消息给两个队列{}",new Date().toString(),msg);
        rabbitTemplate.convertAndSend(DeadLetterConfig.X_EXCHANGE,"XA",msg.getBytes(StandardCharsets.UTF_8));
        rabbitTemplate.convertAndSend(DeadLetterConfig.X_EXCHANGE,"XB",msg.getBytes(StandardCharsets.UTF_8));
    }
    //通用deadLetter
    @GetMapping("/sendExpireMsg/{message}/{ttl}")
    public void sendMsg(@PathVariable("message") String msg,@PathVariable("ttl") String ttl){
        log.info("当前时间{}：发送一个延时{}，的消息给队列{}",new Date().toString(),ttl,msg);
        rabbitTemplate.convertAndSend(DeadLetterConfig.X_EXCHANGE,"XC",msg.getBytes(StandardCharsets.UTF_8),
                message -> {
                    //设置过期时间
                    message.getMessageProperties()
                            .setPriority(5);
                    message.getMessageProperties()
                            .setExpiration(ttl);
                    return message;
        });
    }

    //基于插件的delayed
    @GetMapping("/sendDelayMsg/{message}/{delayTime}")
    public void sendDelayedMsg(@PathVariable("message") String msg,@PathVariable("delayTime") String delayTime){
        log.info("当前时间{}：发送一个延时{}，的消息给队列{}",new Date().toString(),delayTime,msg);

        rabbitTemplate.convertAndSend(DelayedConfig.DELAYED_EXCHANGE,DelayedConfig.DELAYED_ROUTING,
                msg.getBytes(StandardCharsets.UTF_8), message -> {
                //设置过期时间
                //注意：此处设置时间和上面的不同上面为setExpiration 这里为setDelay
                message.getMessageProperties()
                        .setDelay(Integer.parseInt(delayTime));
                return message;
        });
    }

    //topic
    @RequestMapping(value = {"/topic/{message}/{routing}"},
                    method = {RequestMethod.POST,RequestMethod.GET})
    public void sendTopicMsg(@PathVariable("message") String message,
                             @PathVariable("routing") String routing){
        log.info("给Topic交换机发送一个消息：{},路由是：{},当前时间是：{}",message,routing,new Date().toString());

        //修改执行相应业务逻辑
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("createTime", new Date());
        //常规写法
        /*
        * 在 RabbitMQ 中， MessageProperties 是用于描述消息属性的对象。
        * 它包含了很多标准的 AMQP 消息属性，如消息主体的MIME类型、时间戳、消息ID、消息过期时间、优先级、应答标志、持久化标志等。
        * 利用它，可以实现各种复杂的消息行为，比如消息持久化、TTL、死信队列等。
        * 在 Spring Boot 的 RabbitMQ 中，MessageProperties 通常会与 Message 对象一起使用。
         * */
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(UUID.randomUUID().toString());
        messageProperties.setContentType("application/json"); //内容类型
        messageProperties.setContentEncoding("UTF-8");//内容编码
        messageProperties.setHeader("param",hashMap); //
        messageProperties.setExpiration("20000");//设置消息过期时间
        Message msg = new Message(message.getBytes(StandardCharsets.UTF_8), messageProperties);

        rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE,routing,msg);
    }
}
