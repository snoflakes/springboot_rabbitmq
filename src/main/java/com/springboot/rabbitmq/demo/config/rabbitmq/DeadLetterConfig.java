package com.springboot.rabbitmq.demo.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author liugp_oup
 * @email liugp@si-tech.com.cn
 * @create 2023-03-05 15:02
 * @desc  死信队列
 */
@Configuration
public class DeadLetterConfig {
    public static final String X_EXCHANGE = "X";
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String QUEUE_C = "QC";
    public static final String DEAD_LETTER_QUEUE_D = "QD";

    //延时队列交换机
    @Bean("xExchange")
    public DirectExchange xDirectExchange(){
        return new DirectExchange(X_EXCHANGE);
    }
    //死信队列交换机
    @Bean("yExchange")
    public DirectExchange yDirectExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }


    //设置延时为10s的队列A并关联死信交换机
    @Bean("queueA")
    public Queue queueA(){
        return QueueBuilder.durable(QUEUE_A)
                .deadLetterExchange(Y_DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey("YD")
                .ttl(10000)
                .build();
    }
    //设置延时为30s的队列A并关联死信交换机
    @Bean("queueB")
    public Queue queueB(){
        return QueueBuilder.durable(QUEUE_B)
                .deadLetterExchange(Y_DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey("YD")
                .ttl(30000)
                .build();
    }
    //设置通用延时队列C关联死信交换机，并将该队列声明为优先级队列
    /*
     * 要声明优先级队列，请使用x-max-priority可选queue参数。该参数应该是1到255之间的正整数，表示队列应该支持的最大优先级。
     * 发布者可以使用basic.properties的优先级字段发布优先级的消息。数字越大，优先级越高。
     * RabbitMQ队列默认不支持优先级。当创建优先级队列时，开发人员可以根据需要选择最大优先级。在选择值时，必须考虑几件事：
     * 每个队列的每个优先级级别都有一些内存和磁盘成本。还有一个额外的CPU成本，特别是在使用时，所以您可能不希望创建大量的级别。
     * message priority字段被定义为一个无符号字节，因此在实践中优先级应该在0到255之间。
     * 没有优先级属性的消息将被视为其优先级为0。优先级高于队列最大优先级的消息将被视为以最大优先级发布。
     * @return
     */
    @Bean("queueC")
    public Queue queueC(){
        return QueueBuilder.durable(QUEUE_C) //声明队列
                .deadLetterExchange(Y_DEAD_LETTER_EXCHANGE) //关联死信交换机
                .deadLetterRoutingKey("YD") //关联死信队列路由
                .maxPriority(10) //配置优先级队列（范围0-255，此处设为10，则允许优先级的范围为0-10）
                .build();
    }
    //设置死信队列D
    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_D).build();
    }


    //队列绑定交换机
    @Bean
    public Binding queueABindingExchangeX(@Qualifier("queueA") Queue queueA,
                                          @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }
    //队列绑定交换机
    @Bean
    public Binding queueBBindingExchangeX(@Qualifier("queueB") Queue queueB,
                                          @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }
    //队列绑定交换机
    @Bean
    public Binding queueCBindingExchangeX(@Qualifier("queueC") Queue queueC,
                                          @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
    }
    //死信队列绑定死信交换机
    @Bean
    public Binding queueDBindingExchangeY(@Qualifier("queueD") Queue queueD,
                                          @Qualifier("yExchange") DirectExchange yExchange){
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }
}
