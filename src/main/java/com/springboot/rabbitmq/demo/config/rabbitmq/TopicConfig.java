package com.springboot.rabbitmq.demo.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liugp_oup
 * @email liugp@si-tech.com.cn
 * @create 2023-03-06 16:59
 * @desc 创建主题交换机
 */
@Configuration
public class TopicConfig {
    //主体交换机
    public static final String TOPIC_EXCHANGE = "topic_exchange";
    public static final String TOPIC_QUEUE_A = "topic_queue_a";
    public static final String TOPIC_QUEUE_B = "topic_queue_b";
    //死信交换机
    public static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";
    public static final String DEAD_LETTER_QUEUE = "dead_letter_queue";
    //备份交换机
    public static final String BACK_EXCHANGE = "back_exchange";
    public static final String BACK_QUEUE_A = "back_queue_a";
    public static final String BACK_QUEUE_B = "back_queue_b";

    //创建主体交换机并绑定备用交换机
    @Bean
    public TopicExchange topicExchange(){
        return ExchangeBuilder.topicExchange(TOPIC_EXCHANGE)//定义主体交换机
                .durable(true)//持久化
                .alternate(BACK_EXCHANGE) //绑定备用交换机
                .build();
    }
    //创建死信交换机(直接)
    @Bean
    public DirectExchange deadLetterExchange(){
        return ExchangeBuilder.directExchange(DEAD_LETTER_EXCHANGE)
                .durable(true)
                .build();
//        return new DirectExchange(DEAD_LETTER_EXCHANGE,true,false);

    }
    //创建备份交换机(扇出)
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(BACK_EXCHANGE,true,false);
    }


    //创建主体交换机队列A并绑定死信队列
    @Bean
    public Queue topicQueueA(){
        return QueueBuilder.durable(TOPIC_QUEUE_A)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)//关联死信交换机
                .deadLetterRoutingKey("dead_letter_routing") //死信队列路由
                .build();
    }
    //创建主体交换机队列B并绑定死信队列
    @Bean
    public Queue topicQueueB(){
        return QueueBuilder.durable(TOPIC_QUEUE_B)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE) //关联死信交换机
                .deadLetterRoutingKey("dead_letter_routing") //死信队列路由
                .build();
    }
    //创建死信队列
    @Bean
    public Queue deadLetterQueue(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }
    //创建备份队列A
    @Bean
    public Queue backQueueA(){
        return QueueBuilder.durable(BACK_QUEUE_A).build();
    }
    //创建备份队列B
    @Bean
    public Queue backQueueB(){
        return QueueBuilder.durable(BACK_QUEUE_B).build();
    }

    //主体交换机通过路由{*.orange.*}绑定主体队列A
    @Bean
    public Binding queueABindingTopicExchange(@Qualifier("topicQueueA") Queue topicQueueA,
                                              @Qualifier("topicExchange") TopicExchange topicExchange){
        return BindingBuilder.bind(topicQueueA).to(topicExchange).with("*.orange.*");
    }
    //主体交换机通过路由{lazy.#}绑定主体队列B
    @Bean
    public Binding queueBBindingTopicExchangeA(@Qualifier("topicQueueB") Queue topicQueueB,
                                              @Qualifier("topicExchange") TopicExchange topicExchange){
        return BindingBuilder.bind(topicQueueB).to(topicExchange).with("lazy.#");
    }
    //主体交换机通过路由{*.*.rabbit}绑定主体队列B
    @Bean
    public Binding queueBBindingTopicExchangeB(@Qualifier("topicQueueB") Queue topicQueueB,
                                              @Qualifier("topicExchange") TopicExchange topicExchange){
        return BindingBuilder.bind(topicQueueB).to(topicExchange).with("*.*.rabbit");
    }
    //死信交换机绑定死信队列
    @Bean
    public Binding deadLetterBindingExchange(@Qualifier("deadLetterQueue") Queue deadLetterQueue,
                                             @Qualifier("deadLetterExchange") DirectExchange deadLetterExchange){
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("dead_letter_routing");
    }
    //备份交换机绑定备份队列A
    @Bean
    public Binding backQueueABindingBackExchange(@Qualifier("backQueueA") Queue backQueueA,
                                                @Qualifier("fanoutExchange") FanoutExchange fanoutExchange){
        return BindingBuilder.bind(backQueueA).to(fanoutExchange);
    }
    //备份交换机绑定备份队列B
    @Bean
    public Binding backQueueBBindingBackExchange(@Qualifier("backQueueB") Queue backQueueB,
                                                @Qualifier("fanoutExchange") FanoutExchange fanoutExchange){
        return BindingBuilder.bind(backQueueB).to(fanoutExchange);
    }

}
