## 1. 基本概念

### 1.1 什么是 MQ

​		  MQ(message queue)，从字面意思上看，本质是个队列，FIFO 先入先出，只不过队列中存放的内容是 message 而已，还是一种跨进程的通信机制，用于上下游传递消息。

### 1.2 为什么要用 MQ 

1.**<font color='#900000'>流量消峰</font>**

​		 举个例子，如果订单系统最多能处理一万次订单，这个处理能力应付正常时段的下单时绰绰有余，正常时段我们下单一秒后就能返回结果。但是在高峰期，如果有两万次下单操作系统是处理不了的，只能限 制订单超过一万后不允许用户下单。使用消息队列做缓冲，我们可以取消这个限制，把一秒内下的订单分 散成一段时间来处理，这时有些用户可能在下单十几秒后才能收到下单成功的操作，但是比不能下单的体 验要好。

2.**<font color='#900000'>应用解耦</font>**

​		 以电商应用为例，应用中有订单系统、库存系统、物流系统、支付系统。用户创建订单后，如果耦合 调用库存系统、物流系统、支付系统，任何一个子系统出了故障，都会造成下单操作异常。当转变成基于 消息队列的方式后，系统间调用的问题会减少很多，比如物流系统因为发生故障，需要几分钟来修复。在 这几分钟的时间里，物流系统要处理的内存被缓存在消息队列中，用户的下单操作可以正常完成。当物流 系统恢复后，继续处理订单信息即可，中单用户感受不到物流系统的故障，提升系统的可用性。

3.**<font color='#900000'>异步处理</font>**

例如 A 调用 B，B 需要花费很长时间执行，当 B 处理完成后，会发送一条消息给 MQ，MQ 会将此 消息转发给 A 服务。这样 A 服务既不用循环调用 B 的查询 api，也不用提供 callback api。同样 B 服务也不用做这些操作。

​		 有些服务间调用是异步的，例如 A 调用 B，B 需要花费很长时间执行，但是 A 需要知道 B 什么时候可以执行完，以前一般有两种方式，A 过一段时间去调用 B 的查询 api 查询。或者 A 提供一个 callback api， B 执行完之后调用 api 通知 A 服务。这两种方式都不是很优雅，使用消息总线，可以很方便解决这个问题， A 调用 B 服务后，只需要监听 B 处理完成的消息，当 B 处理完成后，会发送一条消息给 MQ，MQ 会将此 消息转发给 A 服务。这样 A 服务既不用循环调用 B 的查询 api，也不用提供 callback api。同样 B 服务也不用做这些操作。A 服务还能及时的得到异步处理成功的消息。

### 1.2 Kafka、RocketMQ、RabbitMQ的优劣势比较

#### 1.2.1 Kafka

优势: 日志采集功能，肯定是首选kafka了

- 单机写入TPS约在**百万条/秒**，最大的优点，就是**吞吐量高**
- 在**大数据**领域的**实时计算**以及**日志采集**被大规模使用

缺点:

- Kafka单机**<font color='#900000'>超过64个队列/分区，Load会发生明显的飙高现象</font>**，队列越多，load越高，发送消息响应时间变长
- <font color='#900000'>**消费失败不支持重试**</font>
- 使用短轮询方式，实时性取决于轮询间隔时间

#### 1.2.2 RocketMQ

优势: **为金融互联网领域**

- 支持10亿级别的消息堆积，不会因为堆积导致性能下降
- 经过参数优化配置，消息可以做到0丢失

缺点:

- **支持的客户端语言不多**，目前是java及c++，其中c++不成熟
- 没有在 mq 核心中去实现JMS等接口，**有些系统要迁移需要修改大量代码**

#### 1.2.3 RabbitMQ

- 由于erlang语言的特性，mq **性能较好，高并发**
- 吞吐量到万级，MQ功能比较完备
- 健壮、稳定、易用、跨平台、支持多种语言、文档齐全

缺点:

- erlang开发，很难去看懂源码，基本只能依赖于开源社区的快速维护和修复bug，不利于做二次开发和维护
- RabbitMQ确实吞吐量会低一些，这是因为他做的实现机制比较重
- 需要学习比较复杂的接口和协议，学习和维护成本较高。

#### <font color='#900000'>针对RabbitMQ确实吞吐量低的解决方案</font>：

① 既然消费能力不足，那就**扩展更多消费节点**，提升消费能力；
② 建立专门的队列消费服务，将消息批量**取出并持久化**，之后再慢慢消费。

① 就是最直接的方式，也是消息积压最常用的解决方案，但有些企业考虑到服务器**成本**压力，会选择第 

② 种方案进行迂回，先通过一个独立服务把要消费的消息存起来，比如存到数据库，之后再慢慢处理这些消息即可。

#### <font color='#900000'>消息重复解决方案</font>：

1. 原因：

   ①消息消费成功，事务已提交，签收时结果**服务器宕机或网络原因**导致签收失败，消息状态会由unack转变为ready，重新发送给其他消费方；
   ②**消息消费失败**，由于retry重试机制，重新入队又将消息发送出去。

2. 解决方案：

   ①消费方业务接口做好幂等；
   ②消息日志表保存MQ发送时的唯一消息ID，消费方可以根据这个唯一ID进行判断避免消息重复

   幂等性解决：Redis 原子性利用 redis 执行 **`setnx`** 命令，天然具有幂等性。从而实现不重复消费

### 1.3 MQ 的选择 

1.Kafka

​		主要特点是基于 Pull 的模式来处理消息消费，追求高吞吐量，一开始的目的就是用于日志收集 和传输，适合产生大量数据的互联网服务的数据收集业务。大型公司建议可以选用，**如果有日志采集功能， 肯定是首选 kafka** 了。尚硅谷官网 kafka 视频连接 http://www.gulixueyuan.com/course/330/tasks 

2.RocketMQ 

​		天生**为金融互联网领域**而生，对于**可靠性要求很高**的场景，尤其是电商里面的订单扣款，以及业务削 峰，在大量交易涌入时，后端可能无法及时处理的情况。RoketMQ 在稳定性上可能更值得信赖，这些业务 场景在阿里双 11 已经经历了多次考验，如果你的业务有上述并发场景，建议可以选择 RocketMQ。 

3.RabbitMQ 结合 

​		erlang 语言本身的并发优势，性能好时效性微秒级，社区活跃度也比较高，管理界面用起来十分 方便，如果你的**数据量没有那么大**，中小型公司优先选择功能比较完备的 RabbitMQ。

## 2. RabbitMQ

### 2.1 四大核心

生产者、交换机、队列、消费者

### 2.2 RabbitMQ工作原理

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220623224204394.png" alt="image-20220623224204394" style="zoom:67%;" />

**<font color='#900000'>Broker</font>：**接收和分发消息的应用，RabbitMQ Server 就是 Message Broker 

**<font color='#900000'>Virtual host</font>：**出于多租户和安全因素设计的，把 AMQP 的基本组件划分到一个虚拟的分组中，类似 于网络中的 namespace 概念。当多个不同的用户使用同一个 RabbitMQ server 提供的服务时，可以划分出 多个 vhost，每个用户在自己的 vhost 创建 exchange／queue 等 

**<font color='#900000'>Connection</font>：**publisher／consumer 和 broker 之间的 TCP 连接 

**<font color='#900000'>Channel</font>**：如果每一次访问 RabbitMQ 都建立一个 Connection，在消息量大的时候建立 TCP  **<font color='#900abc'>Connection 的开销将是巨大的，效率也较低</font>**，Channel 是在 connection 内部建立的逻辑连接，如果应用程序支持多线程，通常每个 thread 创建单独的 channel 进行通讯，AMQP method 包含了 channel id 帮助客户端和 message broker 识别 channel，所以 channel 之间是完全隔离的。**Channel 作为轻量级的 Connection 极大减少了操作系统建立 TCP connection 的开销**  

**<font color='#900000'>Exchange</font>**：message 到达 broker 的第一站，根据分发规则，匹配查询表中的 routing key，分发 消息到 queue 中去。常用的类型有：direct (point-to-point), topic (publish-subscribe) and fanout  (multicast) 

**<font color='#900000'>Queue</font>**：消息最终被送到这里等待 consumer 取走 

**<font color='#900000'>Binding</font>**：exchange 和 queue 之间的虚拟连接，binding 中可以包含 routing key，Binding 信息被保 存到 exchange 中的查询表中，用于 message 的分发依据

## 3. RabbitMQ的安装 

1.官网地址 

​		https://www.rabbitmq.com/download.html 

2.文件上传 

​		上传到/usr/local/software 目录下(如果没有 software 需要自己创建)

![image-20230303095422627](C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20230303095422627.png)

3.安装文件(分别按照以下顺序安装)

```shell
rpm -ivh erlang-21.3-1.el7.x86_64.rpm
yum install socat -y
rpm -ivh rabbitmq-server-3.8.8-1.el7.noarch.rpm
```

 4.常用命令(按照以下顺序执行) 

```shell
# 添加开机启动 RabbitMQ 服务
chkconfig rabbitmq-server on
# 启动服务
/sbin/service rabbitmq-server start 
# 查看服务状态
/sbin/service rabbitmq-server status
# 停止服务(选择执行)
/sbin/service rabbitmq-server stop 
# 开启 web 管理插件（安装该插件之前需要先停止服务）
rabbitmq-plugins enable rabbitmq_management
```

注：用默认账号密码(guest)访问地址 http://192.168.10.104:15672/出现权限问题

![image-20230303100713667](C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20230303100713667.png)

5.添加一个新的用户

```shell
# 创建账号
rabbitmqctl add_user admin admin
# 设置用户角色
rabbitmqctl set_user_tags admin administrator
# 设置用户权限
eg：set_permissions [-p <vhostpath>] <user> <conf> <write> <read>
rabbitmqctl set_permissions -p "/" admin ".*" ".*" ".*"
#用户 user_admin 具有/vhost1 这个 virtual host 中所有资源的配置、写、读权限 当前用户和角色
rabbitmqctl list_users
```

6.再次利用 admin 用户登录

```
账号：admin
密码：admin
```

![image-20230303100942982](C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20230303100942982.png)

7.重置命令

```shell
# 关闭应用的命令为
rabbitmqctl stop_app
# 清除的命令为
rabbitmqctl reset
# 重新启动命令为
rabbitmqctl start_app
```

## 4. 代码样例

### 4.1 导入依赖

```xml
<!--指定 jdk 编译版本-->
<build>
 	<plugins>
 		<plugin>
 			<groupId>org.apache.maven.plugins</groupId>
 			<artifactId>maven-compiler-plugin</artifactId>
 			<configuration>
 				<source>8</source>
 				<target>8</target>
 			</configuration>
 		</plugin>
 	</plugins>
</build>
<dependencies>
 	<!--rabbitmq 依赖客户端-->
 	<dependency>
 		<groupId>com.rabbitmq</groupId>
 		<artifactId>amqp-client</artifactId>
 		<version>5.8.0</version>
 	</dependency>
 	<!--操作文件流的一个依赖-->
 	<dependency>
 		<groupId>commons-io</groupId>
 		<artifactId>commons-io</artifactId>
 		<version>2.6</version>
 	</dependency>
</dependencies>
```

### 4.2 抽取工具类

```java
public class RabbitMqUtils {
 	//得到一个连接的 channel
 	public static Channel getChannel() throws Exception{
 		//创建一个连接工厂
 		ConnectionFactory factory = new ConnectionFactory();
 		factory.setHost("182.92.234.71");
 		factory.setUsername("admin");
 		factory.setPassword("123");
 		Connection connection = factory.newConnection();
 		Channel channel = connection.createChannel();
 		return channel;
 	}
}
```

### 4.3 轮训分发(默认方式)

#### 4.3.1 生产者

```java
public class Producer {
    //定义队列名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        //创建连接工厂
        Channel channel = RabbitMQUtil.getChannel();
        /*
        * 生成一个队列
        * 1. 队列名称
        * 2. 队列里面的消息是否持久化
        * 3. 该队列是否只供一个消费者进行消费,是否进行共享 true:可以多个消费者消费
        * 4. 是否自动删除,最后一个消费者端开连接以后该队列是否自动删除 true:自动删除
        * 5. 其他参数
        * */
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //要发送的消息
        String message="hello world";
        /*
         * 发送一个消息
         * 1.发送到那个交换机
         * 2.路由的 key 是哪个
         * 3.其他的参数信息
         * 4.发送消息的消息体
         * */
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes(StandardCharsets.UTF_8));
        System.out.println("消息发送完毕");
    }
}
```

#### 4.3.2 消费者

```java
public class Customer {
    //定义队列名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        //创建连接工厂
        Channel channel = RabbitMQUtil.getChannel();
        //推送的消息如何进行消费的接口回调
        DeliverCallback deliverCallback = (var1,var2) ->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("成功接受消息" + );
        };
        //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback = (var1) ->{
            System.out.println("接受消息失败");
        };
        /*
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.成功的回调
         * 3.消费者未成功消费的回调
         * */
        String s = channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
```

### 4.4 消息应答（消费者方）

#### 4.4.1 自动应答：

​		消息发送后立即被认为已经传送成功。（**<font color='#900000'>不安全</font>**）

#### 4.4.2 手动应答：

​	消费者在接收到消息**<font color='#900000'>并且</font><font color='#900000'>处理</font>**该消息之后，告诉 rabbitmq 它已经处理了，rabbitmq 可以把该消息删除了。

#### 4.4.3 消息应答的方法

1. Channel.basicAck(用于肯定确认) ：RabbitMQ 已知道该消息并且成功的处理消息，可以将其丢弃了 
2. Channel.basicNack(用于否定确认)
3. Channel.basicReject(用于否定确认)

### 4.5 发布确认（生产者方）

#### 4.5.1 开启发布确认

​		 发布确认默认是没有开启的，如果要开启需要调用方法 confirmSelect，每当你要想使用发布 确认，都需要在 channel 上调用该方法。

#### 4.5.2 单个确认发布：

​		 发布速度特别的慢

```java
public class Producer {
    //定义队列名称
    private final static String QUEUE_NAME = UUID.randomUUID().toString();

    public static void main(String[] args) throws Exception{
        //创建连接工厂
        Channel channel = RabbitMQUtil.getChannel();
        /*
        * 生成一个队列
        * 1. 队列名称
        * 2. 队列里面的消息是否持久化
        * 3. 该队列是否只供一个消费者进行消费,是否进行共享 true:可以多个消费者消费
        * 4. 是否自动删除,最后一个消费者端开连接以后该队列是否自动删除 true:自动删除
        * 5. 其他参数
        * */
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        /*
         * 发送一个消息
         * 1.发送到那个交换机
         * 2.路由的 key 是哪个
         * 3.其他的参数信息(如：消息实现持久化[MessageProperties.PERSISTENT_TEXT_PLAIN])
         * 4.发送消息的消息体
         * */
        for (int i = 0; i < 1000; i++) {
            String message = i + "发送的消息";
            channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes(StandardCharsets.UTF_8));
            //单个发布确认：服务端返回 false 或超时时间内未返回，生产者可以消息重发
            boolean flag = channel.waitForConfirms();
        }
        System.out.println("消息发送完毕");
    }
}
```

#### 4.5.3 批量确认发布：

​		 当发生故障导致发布出现问题时，不知道是哪个消息出现 问题了，我们必须将整个批处理保存在内存中，以记录重要的信息而后重新发布消息。

```java
for (int i = 0; i < 1000; i++) {
            String message = i + "发送的消息";
            channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes(StandardCharsets.UTF_8));
            //批量发布确认
            if(i % batchSize == 0){
                boolean b = channel.waitForConfirms();
            }
        }
```

#### 4.5.4 异步确认发布

​		异步确认虽然编程逻辑比上两个要复杂，但是性价比最高，无论是可靠性还是效率都没得说， 他是利用回调函数来达到消息可靠性传递的。

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220624012611491.png" alt="image-20220624012611491" style="zoom:50%;" />

如何处理异步未确认消息：

​		 最好的解决的解决方案就是把未确认的消息放到一个基于内存的能被发布线程访问的队列， 比如说用 ConcurrentLinkedQueue 这个队列在 confirm callbacks 与发布线程之间进行消息的传递。

```java
import com.example.messagequeue.demo.utils.RabbitMQUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Producer {
    //定义队列名称
    private final static String QUEUE_NAME = UUID.randomUUID().toString();

    public static void main(String[] args) throws Exception{
        //创建连接工厂
        Channel channel = RabbitMQUtil.getChannel();
        /*
        * 生成一个队列
        * 1. 队列名称
        * 2. 队列里面的消息是否持久化
        * 3. 该队列是否只供一个消费者进行消费,是否进行共享 true:可以多个消费者消费
        * 4. 是否自动删除,最后一个消费者端开连接以后该队列是否自动删除 true:自动删除
        * 5. 其他参数
        * */
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        /*
        * （2）定义一个线程安全有序的一个哈希表，用于记录发送的消息
        * 将序号与消息关联
        * */
        ConcurrentSkipListMap<Long,Object> concurrentSkipListMap = new ConcurrentSkipListMap<>();
        ConfirmCallback confirmCallback_success = (sequenceNumber,multiple) ->{
            //multiple:是否为批量 清除已确认的消息，剩下的就是未确认的消息
            if (multiple){
                ConcurrentNavigableMap<Long, Object> success_message = concurrentSkipListMap.headMap(sequenceNumber,true);
                success_message.clear();
            }else {
                concurrentSkipListMap.remove(sequenceNumber);
            }
        };
        ConfirmCallback confirmCallback_error = (sequenceNumber,multiple) ->{
            //获取未被确认的消息
            ConcurrentNavigableMap<Long, Object> error_message = concurrentSkipListMap.headMap(sequenceNumber);
            Set<Map.Entry<Long, Object>> entries = error_message.entrySet();
            for (Map.Entry<Long, Object> entry : entries) {
                Object value = entry.getValue();
            }
        };
        /*
        * (1)添加一个异步的确认监视器
        * confirmCallback_success:成功的回调函数
        * confirmCallback_error:失败的回调函数
        * */
        channel.addConfirmListener(confirmCallback_success,confirmCallback_error);

        /*
         * 发送一个消息
         * 1.发送到那个交换机
         * 2.路由的 key 是哪个
         * 3.其他的参数信息(如：消息实现持久化[MessageProperties.PERSISTENT_TEXT_PLAIN])
         * 4.发送消息的消息体
         * */
        for (int i = 0; i < 1000; i++) {
            String message = i + "发送的消息";
            //channel.getNextPublishSeqNo()获取下一个消息的序列号,通过序列号与消息体进行一个关联
            concurrentSkipListMap.put(channel.getNextPublishSeqNo(),message);
            channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes(StandardCharsets.UTF_8));

        }
        System.out.println("消息发送完毕");
    }
}
```

发布确认和消息应答的区别：`**发布确认**`的目的是**<font color='#900000'>保证消息正确的发布</font>**到队列上，而`**消息应答**`的目的是**<font color='#900000'>确保每个消息都被消费</font>**了。	

## 5.交换机(Exchanges)

交换机的类型：默认、扇出、主题、直接

默认交换，我们通过空字符串("")进行标识。

### 临时队列

​		每当我们连接到 Rabbit 时，我们都需要一个全新的空队列，为此我们可以创建一个**具有随机名称 的队列**，或者能让服务器为我们选择一个随机队列名称那就更好了。其次**一旦我们断开了消费者的连 接，队列将被自动删除**。

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220625145524839.png" alt="image-20220625145524839" style="zoom:50%;" />

```java
//创建临时队列的方式如下:
String queueName = channel.queueDeclare().getQueue();
```



### 5.1 扇出交换机（fanout）[广播交换机]

特点：交换机**<font color='#900000'>向所有队列</font>**发送消息，可以被多个消费者消费。

实现：**只绑定交换机与队列**，而**<font color='#900000'>不声明路由</font>**值

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220625145045303.png" alt="image-20220625145045303" style="zoom:50%;" />

代码：

```java
//消费者1
public class Customer1 {
    final static String EXCHANGE_NAME = "exchange1";
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //声明一个随机队列
        String queue = channel.queueDeclare().getQueue();
        //交换机与队列绑定
        channel.queueBind(queue,EXCHANGE_NAME,"");
        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("成功接受消息" + message);
        };
        channel.basicConsume(queue,true,deliverCallback,consumerTag ->{});
        System.out.println("消费者1：");
    }
}
//消费者2
public class Customer2 {
    final static String EXCHANGE_NAME = "exchange1";
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //声明一个随机队列
        String queue = channel.queueDeclare().getQueue();
        //交换机与队列绑定
        channel.queueBind(queue,EXCHANGE_NAME,"");
        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("成功接受消息" + message);
        };
        channel.basicConsume(queue,true,deliverCallback,consumerTag ->{});
        System.out.println("消费者2：");
    }
}
//生产者
public class Produce {
    final static String EXCHANGE_NAME = "exchange1";
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //批量发送消息
        for (int i = 0;i<10;i++){
            String message = "发送的消息" + i;
            channel.basicPublish(EXCHANGE_NAME, "",MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
```

### 5.2 直接交换机（direct）

特点：这种类型的工作方式是，**消息只去到它绑定的 routingKey 队列中去**。

实现：

- **交换机**根据**路由**绑定**队列**；
- **生产者**根据**交换机和路由**发送消息到指定队列；
- **消费者**根据**指定队列**进行消费

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220625165814787.png" alt="image-20220625165814787" style="zoom:50%;" />

```java
//消费者1
public class Customer1 {
    final static String EXCHANGE_NAME = "exchange";
    final static String QUEUE_NAME = "queue1";
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //交换机与队列和路由绑定
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,RouteUtil.MASSAGE.getValue());
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,RouteUtil.INFO.getValue());
        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("成功接受消息" + message);
        };
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,consumerTag ->{});
        System.out.println("消费者1：");
    }
}
//消费者2
public class Customer2 {
    final static String EXCHANGE_NAME = "exchange";
    final static String QUEUE_NAME = "queue2";
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //交换机与队列绑定
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,RouteUtil.WARRING.getValue());
        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("成功接受消息" + message);
        };
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,consumerTag ->{});
        System.out.println("消费者2：");
    }
}
//生产者
public class Produce {
    final static String EXCHANGE_NAME = "exchange";
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        //批量发送消息
        for (int i = 0;i<10;i++){
            String message = "发送的消息" + i;
            channel.basicPublish(EXCHANGE_NAME, RouteUtil.INFO.getValue(), MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
//枚举类路由
public enum RouteUtil {
    MASSAGE("massage"),
    INFO("info"),
    WARRING("warring");

    private final String value;
    private RouteUtil(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }
}
```

### 5.3 主题交换机

特点：这种类型的工作方式是，**消息只去到它绑定的 routingKey 队列中去**。

实现：

- **交换机**根据**路由**绑定**队列**；
- **生产者**根据**交换机和路由**发送消息到指定队列；
- **消费者**根据**指定队列**进行消费

规则：

*(星号)可以代替一个单词

 #(井号)可以替代零个或多个单词

样例：

中间带 orange 带 3 个单词的字符串`*.orange.*`

中间带 orange 带 3 个单词的字符串`*.orange.*`

第一个单词是 lazy 的多个单词`lazy.#`



<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220625175114486.png" alt="image-20220625175114486" style="zoom:50%;" />

```java
//消费者1
public class Customer1 {
    final static String EXCHANGE_NAME = ExchangeUtil.TOPIC.getExchangeType();
    final static String QUEUE_NAME = QueueUtil.QUEUE_FIRST.getQueueValue();
    final static String QUEUE_FIRST = QueueUtil.QUEUE_FIRST.getQueueValue();
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        //创建主题交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //创建两个队列
        channel.queueDeclare(QUEUE_FIRST, true,false,false,null);
        //绑定关系
        channel.queueBind(QUEUE_FIRST,EXCHANGE_NAME,"*.orange.*");
        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("成功接受消息：" + message);
        };
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,consumerTag ->{});
        System.out.println("消费者1：");
    }
}
//消费者2
public class Customer2 {
    final static String EXCHANGE_NAME = ExchangeUtil.TOPIC.getExchangeType();
    final static String QUEUE_NAME = QueueUtil.QUEUE_SECOND.getQueueValue();
    final static String QUEUE_SECOND = QueueUtil.QUEUE_SECOND.getQueueValue();
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        //创建主题交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //创建两个队列
        channel.queueDeclare(QUEUE_SECOND, true,false,false,null);
        //绑定关系
        channel.queueBind(QUEUE_SECOND,EXCHANGE_NAME,"*.*.rabbit");
        channel.queueBind(QUEUE_SECOND,EXCHANGE_NAME,"lazy.#");
        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("成功接受消息" + message);
        };
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,consumerTag ->{});
        System.out.println("消费者2：");
    }
}
//生产者
public class Produce {
    final static String EXCHANGE_NAME = ExchangeUtil.TOPIC.getExchangeType();
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,BuiltinExchangeType.TOPIC);
        HashMap<String, String> bindingKeyMap = new HashMap<>();
        bindingKeyMap.put("quick.orange.rabbit","quick.orange.rabbit被队列 Q1Q2 接收到1");
        bindingKeyMap.put("lazy.orange.elephant","lazy.orange.elephant被队列 Q1Q2 接收到2");
        bindingKeyMap.put("quick.orange.fox","quick.orange.fox被队列 Q1 接收到3");
        bindingKeyMap.put("lazy.brown.fox","lazy.brown.fox被队列 Q2 接收到4");
        for (Map.Entry<String, String> stringStringEntry : bindingKeyMap.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            System.out.println("发送：key="+key +"  value="+value);
            channel.basicPublish(EXCHANGE_NAME, key,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,value.getBytes(StandardCharsets.UTF_8));
        }
    }
}
//交换机类型
public enum ExchangeUtil {
    TOPIC("topic"),
    DIRECT("direct"),
    FANOUT("fanout");

    private final String value;
    private ExchangeUtil(String value) {
        this.value = value;
    }
    public String getExchangeType() {
        return this.value;
    }
}
```

## 6.死信队列

### 6.1 死信的概念

​        queue 中的某些消息无法被消费，这样的消息如果没有 后续的处理，就变成了死信，有死信自然就有了死信队列。

​        应用场景:为了保证订单业务的消息数据不丢失，需要使用到 RabbitMQ 的死信队列机制，当**消息消费发生异常**时，将消息投入死信队列中。还有比如说: 用户在**<font color='#900000'>商城下单成功并点击去支付后在指定时间未支付时自动失效。</font>**

### 6.2 死信的来源

- 消息 TTL 过期 
- 队列达到最大长度(队列满了，无法再添加数据到 mq 中) 
- 消息被拒绝(basic.reject 或 basic.nack)并且 requeue=false

### 6.3 代码架构图

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220629104420237.png" alt="image-20220629104420237" style="zoom:50%;" />

```java
//整合死信队列的消费者
public class Customer2 {
    final static String EXCHANGE_NAME = ExchangeUtil.TOPIC.getExchangeType();
    final static String DEAD_EXCHANGE = "dead_exchange";
    final static String QUEUE_SECOND = QueueUtil.QUEUE_SECOND.getQueueValue();
    final static String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();

        //声明死信交换机
        channel.exchangeDeclare(DEAD_EXCHANGE,BuiltinExchangeType.DIRECT);
        //创建死信队列
        channel.queueDeclare(DEAD_QUEUE,true,false,false,null);
        //死信队列绑定死信交换机及路由
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"dead_routing");

        //声明主题交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //设置正常队列绑定死信队列
        HashMap<String, Object> map = new HashMap<>();
        //正常队列设置死信交换机 参数 key 是固定值
        map.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //正常队列设置死信 routing-key 参数 key 是固定值
        map.put("x-dead-letter-routing-key","dead_routing");
        //设置队列最大长度
        map.put("x-max-length",100);
        channel.queueDeclare(QUEUE_SECOND, true,false,false,map);
        //正常队列绑定关系
        channel.queueBind(QUEUE_SECOND,EXCHANGE_NAME,"lazy.#");

        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            if(message.equals("info5")){
                //requeue 设置为 false 代表拒绝重新入队 该队列如果配置了死信交换机将发送到死信队列中
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            }
            System.out.println("成功接受消息" + message);
        };
        channel.basicConsume(QUEUE_SECOND,true,deliverCallback,consumerTag ->{});
        System.out.println("消费者2：");
    }
}
//死信队列消费者
public class Customer1 {
    final static String DEAD_EXCHANGE = "dead_exchange";
    final static String DEAD_QUEUE = "dead_queue";
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        //创建主题交换机
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.TOPIC);
        //创建队列
        channel.queueDeclare(DEAD_QUEUE, true,false,false,null);
        //绑定关系
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"dead_routing");
        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("成功接受消息：" + message);
        };
        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,consumerTag ->{});
        System.out.println("消费者1：");
    }
}
//生产者
public class Produce {
    final static String EXCHANGE_NAME = ExchangeUtil.TOPIC.getExchangeType();
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtil.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,BuiltinExchangeType.TOPIC);
        //设置消息的过期时间10S
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();

        HashMap<String, String> bindingKeyMap = new HashMap<>();
        bindingKeyMap.put("quick.orange.rabbit","quick.orange.rabbit被队列 Q1Q2 接收到1");
        bindingKeyMap.put("lazy.orange.elephant","lazy.orange.elephant被队列 Q1Q2 接收到2");
        bindingKeyMap.put("quick.orange.fox","quick.orange.fox被队列 Q1 接收到3");
        bindingKeyMap.put("lazy.brown.fox","lazy.brown.fox被队列 Q2 接收到4");
        for (Map.Entry<String, String> stringStringEntry : bindingKeyMap.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            System.out.println("发送：key="+key +"  value="+value);
            channel.basicPublish(EXCHANGE_NAME, key,
                    properties,value.getBytes(StandardCharsets.UTF_8));
        }
    }
}
```

## 7. 延迟队列

### 7.1  延迟队列使用场景

1.订单在十分钟之内未支付则自动取消 

2.新创建的店铺，如果在十天内都没有上传过商品，则自动发送消息提醒。 

3.用户注册成功后，如果三天内没有登陆则进行短信提醒。 

4.用户发起退款，如果三天内没有得到处理则通知相关运营人员。 

5.预定会议后，需要在预定的时间点前十分钟通知各个与会人员参加会议

### 7.2 订单流程

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220629112355900.png" alt="image-20220629112355900" style="zoom:33%;" />

### 7.3 整合SpringBoot

#### 7.3.1 依赖

```xml
<dependencies>
 	<!--RabbitMQ 依赖-->
 	<dependency>
 		<groupId>org.springframework.boot</groupId>
 		<artifactId>spring-boot-starter-amqp</artifactId>
 	</dependency>
 	<!--RabbitMQ 测试依赖-->
 	<dependency>
 		<groupId>org.springframework.amqp</groupId>
 		<artifactId>spring-rabbit-test</artifactId>
 		<scope>test</scope>
 	</dependency>
</dependencies>
```

#### 7.3.2 配置文件

```properties
spring.rabbitmq.host=182.92.234.71
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin
```

### 7.4 代码架构图

​			创建两个队列 QA 和 QB，两者队列 TTL 分别设置为 10S 和 40S，然后在创建一个交换机 X 和死信交 换机 Y，它们的类型都是 direct，创建一个死信队列 QD，它们的绑定关系如下：

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220629113153759.png" alt="image-20220629113153759" style="zoom:50%;" />

### 7.5 设置固定时长的延时队列

```java
@Configuration
public class TtlQueueConfig {
    public static final String X_EXCHANGE = "X";
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    public static final String DEAD_LETTER_QUEUE = "QD";
    //声明正常交换机
    @Bean("xExchange")
    public DirectExchange getXExchange(){
        return new DirectExchange(X_EXCHANGE);
    }
    //声明死信交换机
    @Bean("yExchange")
    public DirectExchange getYExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }
    //声明队列A 消息过期时间为 10s 并绑定到对应的死信交换机
    @Bean("queueA")
    public Queue queueA(){
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");
        //声明队列的 TTL
        args.put("x-message-ttl", 10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(args).build();
    }
    //声明队列A绑定到交换机
    @Bean
    public Binding queueaBindingX(@Qualifier("queueA") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }
    //声明队列B 消息过期时间为 40s 并绑定到对应的死信交换机
    @Bean("queueB")
    public Queue queueB(){
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");
        //声明队列的 TTL
        args.put("x-message-ttl", 40000);
        return QueueBuilder.durable(QUEUE_A).withArguments(args).build();
    }
    //声明队列B绑定到交换机
    @Bean
    public Binding queueaBindingB(@Qualifier("queueB") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XB");
    }
    //声明死信队列 QD
    @Bean("queueD")
    public Queue queueD(){
        return new Queue(DEAD_LETTER_QUEUE);
    }
    //声明死信队列 QD 绑定关系
    @Bean
    public Binding deadLetterBindingQAD(@Qualifier("queueD") Queue queueD,
                                        @Qualifier("yExchange") DirectExchange yExchange){
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }
}
```

### 7.6 延时队列优化

```java
//生产者
@RestController
@RequestMapping("hello")
public class SendMsgController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("send/{message}")
    public void sendMsg(@PathVariable String message){
        //生产者设置过期时间
        String ttlTime = "10000";
        rabbitTemplate.convertAndSend("X","XA",message,correlationData ->{
            correlationData.getMessageProperties().setExpiration(ttlTime);
            return correlationData;
        });
    }
}
//消息中间件配置
@Configuration
public class TtlQueueConfig {
    public static final String X_EXCHANGE = "X";
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    public static final String DEAD_LETTER_QUEUE = "QD";
    //声明正常交换机
    @Bean("xExchange")
    public DirectExchange getXExchange(){
        return new DirectExchange(X_EXCHANGE);
    }
    //声明死信交换机
    @Bean("yExchange")
    public DirectExchange getYExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }
    //声明队列A 消息过期时间为 10s 并绑定到对应的死信交换机
    @Bean("queueA")
    public Queue queueA(){
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");
        return QueueBuilder.durable(QUEUE_A).withArguments(args).build();
    }
    //声明队列A绑定到交换机
    @Bean
    public Binding queueaBindingX(@Qualifier("queueA") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    //声明死信队列 QD
    @Bean("queueD")
    public Queue queueD(){
        return new Queue(DEAD_LETTER_QUEUE);
    }
    //声明死信队列 QD 绑定关系
    @Bean
    public Binding deadLetterBindingQAD(@Qualifier("queueD") Queue queueD,
                                        @Qualifier("yExchange") DirectExchange yExchange){
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }
}
//消费者
@Component
public class TtlQueueConsumer {
    @RabbitListener(queues = "QD")
    public void receive(Message message, Channel channel){
        String s = new String(message.getBody());
    }
}
```

**<font color='#900000'>缺点</font>**：看起来似乎没什么问题，但是在最开始的时候，就介绍过如果使用在消息属性上设置 TTL 的方式，消息可能并不会按时“死亡“，因为 **RabbitMQ 只会检查第一个消息是否过期**，如果过期则丢到死信队列， **如果第一个消息的延时时长很长，而第二个消息的延时时长很短，第二个消息并不会优先得到执行**。

## ★★★8. Rabbitmq 插件实现延迟队列

### 8.1 安装延时队列插件

在官网上下载 https://www.rabbitmq.com/community-plugins.html，下载 `rabbitmq_delayed_message_exchange` 插件，然后解压放置到 RabbitMQ 的插件目录。 

进入 RabbitMQ 的安装目录下的 plgins 目录，执行下面命令让该插件生效，然后重启 RabbitMQ

```shell
cd /usr/lib/rabbitmq/lib/rabbitmq_server-3.8.8/plugins
cp /home/software/rabbitmq_delayed_message_exchange-3.8.0.ez ./
rabbitmq-plugins enable rabbitmq_delayed_message_exchange
systemctl restart rabbitmq-server
```

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220629214616202.png" alt="image-20220629214616202" style="zoom:33%;" />

```java
@Configuration
public class TtlQueueConfig {
    public static final String DELAYED_EXCHANGE = "delayed_exchange";
    public static final String DELAYED_QUEUE = "delayed_queue";
    public static final String DELAYED_ROUTING = "delayed_routing";
    //自定义交换机
    @Bean("exchange")
    public CustomExchange getExchange(){
        HashMap<String, Object> hashMap = new HashMap<>();
        //自定义交换机类型
        hashMap.put("x-delayed-type","direct");
        return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false,
                hashMap);
    }
    //声明队列A 消息过期时间为 10s 并绑定到对应的死信交换机
    @Bean("queue")
    public Queue queueA(){
        return new Queue(DELAYED_QUEUE);
    }
    //声明队列A绑定到交换机
    @Bean
    public Binding queueaBindingX(@Qualifier("queue") Queue queue,
                                  @Qualifier("exchange") CustomExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(DELAYED_ROUTING).noargs();
    }
}
//生产者
@RestController
@RequestMapping("hello")
public class SendMsgController {
    public static final String DELAYED_EXCHANGE = "delayed_exchange";
    public static final String DELAYED_QUEUE = "delayed_queue";
    public static final String DELAYED_ROUTING = "delayed_routing";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("send/{message}")
    public void sendMsg(@PathVariable String message){
        //生产者设置过期时间
        String ttlTime = "10000";
        rabbitTemplate.convertAndSend(DELAYED_EXCHANGE,DELAYED_ROUTING,message,correlationData ->{
            correlationData.getMessageProperties().setExpiration(ttlTime);
            return correlationData;
        });
    }
}
//消费者
public static final String DELAYED_QUEUE = "delayed_queue";
@RabbitListener(queues = DELAYED_QUEUE)
public void receiveDelayedQueue(Message message){
 String msg = new String(message.getBody());
 log.info("当前时间：{},收到延时队列的消息：{}", new Date().toString(), msg);
}
```

## 9. 发布确认高级

### 9.1 确认机制方案

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220630002258487.png" alt="image-20220630002258487" style="zoom:40%;" />

### 9.2 新增配置（application.properties）

⚫  `NONE `禁用发布确认模式，是默认值 

⚫ `CORRELATED` 发布消息成功到交换器后会触发回调方法 

⚫ `SIMPLE` 经测试有两种效果，其一效果和 CORRELATED 值一样会触发回调方法， 其二在发布消息成功后使用 rabbitTemplate 调用 waitForConfirms 或 waitForConfirmsOrDie 方法 等待 broker 节点返回发送结果，根据返回结果来判定下一步的逻辑，要注意的点是 waitForConfirmsOrDie 方法如果返回 false 则会关闭 channel，则接下来无法发送消息到 broker

```properties
spring.rabbitmq.publisher-confirm-type=correlated
```

### 9.3 添加配置类 

#### (1)配置交换机

```java
//配置交换机
@Configuration
public class TtlQueueConfig {
    public static final String DELAYED_EXCHANGE = "delayed_exchange";
    public static final String DELAYED_QUEUE = "delayed_queue";
    public static final String DELAYED_ROUTING = "delayed_routing";
    //自定义交换机
    @Bean("exchange")
    public CustomExchange getExchange(){
        HashMap<String, Object> hashMap = new HashMap<>();
        //自定义交换机类型
        hashMap.put("x-delayed-type","direct");
        return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false,
                hashMap);
    }
    //声明队列A
    @Bean("queue")
    public Queue queueA(){
        return new Queue(DELAYED_QUEUE);
    }
    //声明队列A绑定到交换机
    @Bean
    public Binding queueaBindingX(@Qualifier("queue") Queue queue,
                                  @Qualifier("exchange") CustomExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(DELAYED_ROUTING).noargs();
    }
}
```

#### (2)回调接口

```java
/**
 * @author liugp_oup
 * @email liugp@si-tech.com.cn
 * @create 2022-06-30 16:18
 * @desc 消息回调接口
 */
@Component
@Slf4j
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {
    //消息确认回调
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        String id=correlationData!=null?correlationData.getId():"";
        if(b){
            log.info("交换机已经收到 id 为:{}的消息",id);
        }else{
            log.info("交换机还未收到 id 为:{}消息,由于原因:{}",id,s);
        }
    }
    //当消息无法路由的时候的回调方法
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        //退回的消息
        String backMessage = new String(message.getBody());
        //退回的原因
        System.out.println(replyText);
    }
}
```

#### (3)消息生产者

```java
/**
 * @author liugp_oup
 * @email liugp@si-tech.com.cn
 * @create 2022-06-29 11:54
 * @desc 生产者消息确认与回退,设置延时队列
 */
@RestController
@RequestMapping("hello")
public class SendMsgController {
    public static final String DELAYED_EXCHANGE = "delayed_exchange";
    public static final String DELAYED_QUEUE = "delayed_queue";
    public static final String DELAYED_ROUTING = "delayed_routing";
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MyCallBack myCallBack;
    //依赖注入 rabbitTemplate 之后再设置它的回调对象
    //在整个Bean初始化中的执行顺序：
    //Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的方法)
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(myCallBack);
        /**
         * true：
         * 交换机无法将消息进行路由时，会将该消息返回给生产者
         * false：
         * 如果发现消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        //设置回退消息交给谁处理
        rabbitTemplate.setReturnCallback(myCallBack);

    }

    @GetMapping("send/{message}")
    public void sendMsg(@PathVariable String message){
        //生产者设置过期时间
        String ttlTime = "10000";
        //指定消息 id 为 1
        CorrelationData correlationData1 = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(DELAYED_EXCHANGE,DELAYED_ROUTING,message,correlationData ->{
            correlationData.getMessageProperties().setExpiration(ttlTime);
            return correlationData;
        },correlationData1);
        //指定消息 id 为 1
        CorrelationData correlationData2 = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(DELAYED_EXCHANGE,"routing2",message,correlationData ->{
            correlationData.getMessageProperties().setExpiration(ttlTime);
            return correlationData;
        },correlationData2);
    }
}
```

#### (4)消息消费者

```java
@Component
public class TtlQueueConsumer {
    @RabbitListener(queues = "QD")
    public void receive(Message message, Channel channel){
        String s = new String(message.getBody());
    }
}
```

## 10.备份交换机

### 10.1 代码架构图

<img src="C:/Users/13656/Desktop/学习笔记/消息中间件/rabbitMq/image-20220701230630333.png" alt="image-20220701230630333" style="zoom:50%;" />

```java
/**
 * @author liugp_oup
 * @email liugp@si-tech.com.cn
 * @create 2022-06-29 11:41
 * @desc 备份交换机
 */
@Configuration
public class TtlQueueConfig {
    //主交换机
    public static final String CONFIRM_EXCHANGE = "confirm_exchange";
    public static final String CONFIRM_QUEUE = "confirm_queue";
    public static final String CONFIRM_ROUTING = "confirm_routing";
    //备份交换机
    public static final String BACKUP_EXCHANGE_NAME = "backup_exchange";
    public static final String BACKUP_QUEUE_NAME = "backup_queue";
    public static final String BACKUP_ROUTING_NAME = "backup_routing";

    //自定义交换机绑定备份交换机
    @Bean("confirmExchange")
    public DirectExchange confirmExchange(){
        ExchangeBuilder exchangeBuilder = ExchangeBuilder.directExchange(CONFIRM_EXCHANGE).durable(true)
                .withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME);
        return  exchangeBuilder.build();
    }
    //声明队列A
    @Bean("confirmQueue")
    public Queue confirmQueue(){
        //不能直接  new Queue(),会连接不上rabbitMq
        return QueueBuilder.durable(CONFIRM_QUEUE).build();
    }
    //声明队列A绑定到交换机
    @Bean
    public Binding queueBinding(@Qualifier("confirmQueue") Queue queue,
                                  @Qualifier("confirmExchange") CustomExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(CONFIRM_ROUTING).noargs();
    }
    //声明备份扇型交换机
    @Bean("backupExchange")
    public FanoutExchange backupExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }
    // 声明备份队列
    @Bean("backQueue")
    public Queue backQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }
    // 声明备份队列绑定关系
    @Bean
    public Binding backupBinding(@Qualifier("backQueue") Queue queue,
                                 @Qualifier("backupExchange") FanoutExchange backupExchange){
        return BindingBuilder.bind(queue).to(backupExchange);
    }
}
```

注意：`mandatory` 参数与备份交换机可以一起使用的时候，如果两者同时开启，消息究竟何去何从？谁优先 级高，经过上面结果显示答案是**备份交换机优先级高**。

## 11 RabbitMQ 高可用集群搭建

### 11.1 初始化环境

> （1）**分别修改主机名**
>
> ```
> hostnamectl set-hostname rabbit-node1
> hostnamectl set-hostname rabbit-node2
> hostnamectl set-hostname rabbit-node3
> ```
>
> （2）**修改每台机器的 `/etc/hosts` 文件**
>
> ```
> cat >> /etc/hosts <<EOF
> 192.168.10.104 rabbit-node1
> 192.168.10.105 rabbit-node2
> 192.168.10.106 rabbit-node3
> EOF
> ```
>
> （3）**重启虚拟机便于系统识别hosts**
>
> ```
> systemctl restart network
> ```
>
> （4）配置 Erlang Cookie：RabbitMQ 服务启动时，erlang VM 会自动创建该 cookie 文件，默认的存储路径为 `/var/lib/rabbitmq/.erlang.cookie` 或 `$HOME/.erlang.cookie`，该文件是一个隐藏文件，需要使用 `ls -al` 命令查看
>
> ```
> #拷贝 Erlang Cookie
> scp /var/lib/rabbitmq/.erlang.cookie root@rabbit-node2:/var/lib/rabbitmq/
> scp /var/lib/rabbitmq/.erlang.cookie root@rabbit-node3:/var/lib/rabbitmq/
> ```
>
> （5）启动 RabbitMQ 服务,顺带启动 Erlang 虚拟机和 RbbitMQ 应用服务(在三台节点上分别执行以 下命令
>
> ```
> rabbitmq-server -detached
> ```
>
> （6）在节点 2 执行
>
> ```
> # 1.停止服务
> rabbitmqctl stop_app
> # 2.重置状态
> rabbitmqctl reset
> # 3.节点加入, 在一个node加入cluster之前，必须先停止该node的rabbitmq应用，即先执行stop_app
> rabbitmqctl join_cluster rabbit@rabbit-node1
> # 4.启动服务
> rabbitmqctl start_app
> ```
>
> （7）在节点 3 执行
>
> ```
> # 1.停止服务
> rabbitmqctl stop_app
> # 2.重置状态
> rabbitmqctl reset
> # 3.节点加入, 在一个node加入cluster之前，必须先停止该node的rabbitmq应用，即先执行stop_app
> rabbitmqctl join_cluster rabbit@rabbit-node2
> # 4.启动服务
> rabbitmqctl start_app
> ```
>
> （8）集群状态
>
> ```
> rabbitmqctl cluster_status
> ```
>
> （9）需要重新设置用户
>
> ```
> 创建账号
> rabbitmqctl add_user admin possword
> 设置用户角色
> rabbitmqctl set_user_tags admin administrator
> 设置用户权限
> rabbitmqctl set_permissions -p "/" admin ".*" ".*" ".*"
> ```
>
> （10）解除集群节点(node2 和 node3 机器分别执行)
>
> ```shell
> rabbitmqctl stop_app
> rabbitmqctl reset
> rabbitmqctl start_app
> rabbitmqctl cluster_status
> rabbitmqctl forget_cluster_node rabbit@node2(node1 机器上执行)
> ```
>
> 

## 12 配置镜像队列

#### ★★★12.1 开启镜像队列

目的：<font color='#900000'>**配置镜像队列的目的就是为了实现高可用**</font>

★**注：开启镜像队列时，虽然有多个备份队列<font color='#900000'>但</font>每次消息只会走一条队列，因此并不会出现多消费的情况**

这里我们为**所有队列开启镜像配置**，其语法如下：

```
rabbitmqctl set_policy ha-all "^" '{"ha-mode":"all"}'
```

#### 12.2 复制系数

在上面我们指定了 ha-mode 的值为 all ，代表消息会被同步到所有节点的相同队列中。这里我们之所以这样配置，因为我们本身只有三个节点，因此复制操作的性能开销比较小。如果你的集群有很多节点，那么此时复制的性能开销就比较大，此时需要选择合适的复制系数。通常可以遵循过半写原则，即对于一个节点数为 n 的集群，只需要同步到 n/2+1 个节点上即可。此时需要同时修改镜像策略为 exactly，并指定复制系数 ha-params，示例命令如下：

```shell
# ha-two:策略名 
#“^”:表示任何消息队列都可以备份 “^mirror”:表示只有mirror开头的消息队列才能备份
#"ha-mode":"exactly":策略模式 
#"ha-params":2:镜像个数 
#"ha-sync-mode":"automatic"：自动备份镜像模式
rabbitmqctl set_policy ha-two "^" '{"ha-mode":"exactly","ha-params":2,"ha-sync-mode":"automatic"}'
```

除此之外，RabbitMQ 还支持使用正则表达式来过滤需要进行镜像操作的队列，示例如下：

```
rabbitmqctl set_policy ha-all "^ha\." '{"ha-mode":"all"}'
```

此时只会对 ha 开头的队列进行镜像。更多镜像队列的配置说明，可以参考官方文档：(Highly Available (Mirrored) Queues)[https://www.rabbitmq.com/ha.html]

## 13 HAProxy+Keepalived搭建RabbitMQ高可用镜像模式集群

|       ip       |        主机名         |        **当前节点上部署的服务**         |
| :------------: | :-------------------: | :-------------------------------------: |
| 192.168.10.104 |        master1        | rabbitmq服务、HAProxy、Keepalived（主） |
| 192.168.10.105 |        master2        | rabbitmq服务、HAProxy、Keepalived（备） |
| 192.168.10.106 |        mastre3        |            rabbitmq服务、vip            |
|  192.168.10.3  | 虚拟 ip(确保ip不存在) |                   VIP                   |

（1）linux 开启 IP 转发功能

linux发行版默认情况下是不开启ip转发功能的。因为大部分用不到。但是，如果我们想架设一个linux路由或者vpn服务我们就需要开启该服务了。

```shell
1）查看是否开启转发
cat /proc/sys/net/ipv4/ip_forward
#返回1代表IP已开启，0 未开启
2）临时开启
echo 1 > /proc/sys/net/ipv4/ip_forward
3）永久开启
vim /etc/sysctl.conf
net.ipv4.ip_forward = 1
4）立即生效
sysctl -p /etc/sysctl.conf &
```

（2）关闭selinux

- 它叫做“安全增强型Linux（Security-Enhanced Linux）”，简称 SELinux，它是 Linux 的一个安全子系统。
- 其主要作用就是最大限度地减小系统中服务进程可访问的资源（根据的是最小权限原则）。避免权限过大的角色给系统带来灾难性的结果。

```shell
#查看selinux状态
[root@rabbitmq-1 ~]# getenforce
Disabled
#临时关闭selinux
[root@rabbitmq-1 ~]# setenforce 0
#永久关闭selinux
[root@rabbitmq-1]# vim /etc/selinux/config
将 SELINUX=enforcing 改为 SELINUX=disabled
```

（3）安装HAProxy

> 用haproxy做负载均衡，在192.168.10.104、192.168.10.105 节点上安装haproxy服务

```shell
#安装命令。其中 -y 的含义：当安装过程提示选择全部为"yes"。
yum install -y haproxy
```

（4）配置HAProxy

```shell
#1）首先备份原始配置（仅仅是备份下方便查阅其他配置项，不用的话可以直接改）
cp /etc/haproxy/haproxy.cfg{,.bak}

#2）编辑配置文件，配置文件具体内容如下
vim /etc/haproxy/haproxy.cfg  
```

> ```shell
> #----------------------------------------------------
> # Global settings
> #----------------------------------------------------
> global
>  log         127.0.0.1 local2 info #定义全局的syslog服务器。日志服务器需要开启UDP协议，最多可以定义两个。基于syslog记录日志到指定设备，级别有(err、warning、info、debug)
>  chroot      /var/lib/haproxy #锁定haproxy的运行目录，把haproxy的进程禁锢在一个文件夹内
>  pidfile     /var/run/haproxy.pid #指定haproxy的pid文件存放路径，启动进程的用户必须有权限访问此文件。要和service指定的pid路径一样
>  maxconn     100000 #每个haproxy进程的最大并发连接数，默认4000，可以是100000，还可以更大：一百万
>  maxconnrate 4000 #每个进程每秒创建的最大连接数量，控制瞬间并发
>  user        haproxy #默认用户
>  group       haproxy #默认组
>  daemon #以后台守护进程的方式运行
>  stats socket /var/lib/haproxy/stats #创建监控所用的套接字目录
> #--------------------------------------------------
> # defaults settings
> #--------------------------------------------------
> defaults
>  mode                http #默认的模式mode { tcp|http|health }，tcp是4层，http是7层，health只会返回OK。后面listen的优先级比默认高，可以单独设置。
>  log                 global
>  option              dontlognull #启用该项，日志中将不会记录空连接。所谓空连接就是在上游的负载均衡器
>  option              http-server-close #每次请求完毕后主动关闭http通道
>  option              http-keep-alive #开启与客户端的会话保持
>  option              redispatch #serverId对应的服务器挂掉后，强制定向到其他健康的服务器，重新派发。
>  retries             3 #3次连接失败就认为服务不可用
>  timeout http-request    10s
>  timeout queue           1m
>  timeout connect         30s #客户端请求从haproxy到后端server最长连接等待时间(TCP连接之前)，默认单位ms
>  timeout client          2m #设置haproxy与客户端的最长非活动时间，默认单位ms，建议和timeout server相同
>  timeout server          2m #客户端请求从haproxy到后端服务端的请求处理超时时长(TCP连接之后)，默认单位ms，如果超时，会出现502错误，此值建议设置较大些，访止502错误。
>  timeout http-keep-alive 30s #session会话保持超时时间，此时间段内会转发到相同的后端服务器
>  timeout check           10s #对后端服务器的默认检测超时时间
>  maxconn                 10000 #最大连接数
> #--------------------------------------------------
> # haproxy监控统计界面 settings
> #--------------------------------------------------
> listen admin_stats
>      stats   enable #自动开启
>      bind    0.0.0.0:9188 #访问检测界面入口绑定的端口跟地址
>      mode    http #http的七层模型
>      option  httplog #采用http日志格式
>      log     global
>      maxconn 10 #默认最大连接数
>      stats refresh 30s #统计页面自动刷新时间
>      stats uri /admin_stats #统计页面url，设置haproxy监控地址为http://localhost:9188/admin_stats
>      stats auth admin:admin  #设置监控页面的用户和密码认证：admin:dhgate20221012，可以设置多个用户名
>      stats hide-version #隐藏统计页面上HAProxy的版本信息
>      stats realm (Haproxy statistic platform) #统计页面密码框上提示文本
>      stats admin if TRUE #设置手工启动/禁用，后端服务器(haproxy-1.4.9以后版本)
> #--------------------------------------------------
> # 监听rabbimq_server settings
> #--------------------------------------------------
> listen rabbitmq_server
>      bind 0.0.0.0:55672 #指定HAProxy的监听地址，可以是IPV4或IPV6，可以同时监听多个IP或端口。MQ连接端口，避免跟5672端口冲突，将rabbitmq的5672端口映射为55672端口
>      mode tcp #指定负载协议类型
>      log global
>      balance roundrobin #balance roundrobin 负载轮询，balance source 保存session值，支持static-rr，leastconn，first，uri等参数。
>      server  rabbitmq1 192.168.10.104:5672 maxconn 4000 weight 1 check inter 5s rise 2 fall 2
>      server  rabbitmq2 192.168.10.105:5672 maxconn 4000 weight 1 check inter 5s rise 2 fall 2
>      server  rabbitmq3 192.168.10.106:5672 maxconn 4000 weight 1 check inter 5s rise 2 fall 2
>      #rise 2是2次正确认为服务器可用
>      #fall 2是2次失败认为服务器不可用
>      #check inter 5s 表示检查心跳频率
>      #weight代表权重
>      #maxconn 4000 当前服务器支持的最大并发连接数，超出此值的连接将被放置于请求队列中
> #--------------------------------------------------
> # 监听rabbitmq_web settings
> #--------------------------------------------------
> listen rabbitmq_web
>      bind 0.0.0.0:35672 #将rabbitmq的15672端口映射为35672端口
>      mode http
>      log global
>      option httplog
>      option httpclose
>      balance roundrobin
>      server  rabbitmq1 192.168.10.104:15672 maxconn 2000 weight 1 check inter 5s rise 2 fall 2
>      server  rabbitmq2 192.168.10.105:15672 maxconn 2000 weight 1 check inter 5s rise 2 fall 2
>      server  rabbitmq3 192.168.10.106:15672 maxconn 2000 weight 1 check inter 5s rise 2 fall 2
> #--------------------------------------------------
> ```
>
> 

（5）haproxy rsyslog日志配置

```shell
vim /etc/rsyslog.conf
#在文件的最后一行，加上：
local2.* /var/log/haproxy/rabbitmq.log
```

（6）重启日志服务+启动haproxy代理服务

```shell
#重启日志服务
mkdir -p /var/log/haproxy && systemctl restart rsyslog.service

#★启动haproxy。其中 -f 代表指定配置文件的路径。
haproxy -f /etc/haproxy/haproxy.cfg

#★查看haproxy启动的端口
ss -nplt | grep haproxy

#重新启动haproxy
haproxy -f /etc/haproxy/haproxy.cfg -st `cat /var/run/haproxy.pid`

#停止haproxy
kill -9 `cat /var/run/haproxy.pid`
```

（7）访问HAProxy监控统计界面

```shell
http://192.168.10.104:9188/admin_stats
http://192.168.10.105:9188/admin_stats
这两个能正常访问HAProxy监控界面，用设定的amdin/admin 登录
```

（8）访问RabbitMQ管理界面

```shell
http://192.168.10.104:35672/#/
http://192.168.10.105:35672/#/
```

### 安装Keepalived

注：用keepalived做主备，避免单点问题、实现高可用。在172.22.40.104（主）、172.22.40.105（备） 节点上分别安装Keepalived。

（9）安装相关命令

```shell
#安装
yum install -y keepalived
报错：
--> 解决依赖关系完成
错误：软件包：1:net-snmp-agent-libs-5.7.2-49.el7_9.2.x86_64 (updates)
          需要：libmysqlclient.so.18()(64bit)
错误：软件包：2:postfix-2.10.1-9.el7.x86_64 (@base)
          需要：libmysqlclient.so.18(libmysqlclient_18)(64bit)
错误：软件包：2:postfix-2.10.1-9.el7.x86_64 (@base)
          需要：libmysqlclient.so.18()(64bit)
错误：软件包：1:net-snmp-agent-libs-5.7.2-49.el7_9.2.x86_64 (updates)
          需要：libmysqlclient.so.18(libmysqlclient_18)(64bit)
解决：
# wget http://www.percona.com/redir/downloads/Percona-XtraDB-Cluster/5.5.37-25.10/RPM/rhel6/x86_64/Percona-XtraDB-Cluster-shared-55-5.5.37-25.10.756.el6.x86_64.rpm
# rpm -ivh Percona-XtraDB-Cluster-shared-55-5.5.37-25.10.756.el6.x86_64.rpm 

#备份原有配置
cp /etc/keepalived/keepalived.conf{,.bak}
#配置Keepalived
vim /etc/keepalived/keepalived.conf

```

> 192.168.10.104节点为master，配置内容：
>
> ```shell
> ! Configuration File for keepalived
> 
> #keepalived全局配置
> global_defs {
> 	notification_email {
>   1365689275@qq.com
> }
>  #keepalived检测到故障主备切换时发送邮件通知配置项。
>  #这里我直接改成了调邮件和短信服务接口，没有使用keepalived的notify功能。
>  #短信和邮件接口配置在/etc/keepalived/haproxy_check.sh脚本里。
> 
>  #每个keepalived节点的唯一标识，不能与备机相同。
>  router_id keepalived_master_192-168-10-104
> }
> 
> #检测HAProxy脚本
> vrrp_script check_haproxy {
> script "/etc/keepalived/haproxy_check.sh" #脚本所在的目录
> interval 10 #检测 haproxy 心跳频率：每隔10秒检测一次
> weight 2 #权重
> }
> 
> #虚拟路由器配置
> vrrp_instance haproxy {
>  state MASTER #设置虚拟路由器状态为MASTER，表示为主。
>  interface ens33 #绑定当前虚拟路由器所使用的物理网卡，如eth0、bond0等，可通过ifconfig获得。
>  virtual_router_id 51 #每个虚拟路由器的唯一标识。同属一个虚拟路由器的多个keepalived节点必须相同，务必要确保在同一网络中此值唯一。
>  priority 100 #当前物理节点在此虚拟路由器中的优先级，值越大优先级越高。注意：主机的优先权要比备机高。
>  advert_int 1 #心跳检查频率，单位：秒。
> 
>  #认证机制
>  authentication {
>      auth_type PASS #认证类型
>      auth_pass admin #秘钥，同一虚拟路由器的多个keepalived节点auth_pass值必须保持一致
>  }
> 
>  #虚拟路由器的VIP，不指定网卡时默认添加在eth0上。在添加VIP地址时，需确保将要使用的VIP不存在，避免冲突。
>  virtual_ipaddress {
>      192.168.10.3 #对外开放的虚拟ip
>  }
> 
>  #调用检测HAProxy的脚本
>  track_script {
>      check_haproxy
>  }
> }
> ```
>
> 192.168.10.105节点为backup，配置内容：
>
> ```shell
> ! Configuration File for keepalived
> 
> #keepalived全局配置
> global_defs {
> 	notification_email {
>   1365689275@qq.com
> }
>  #keepalived检测到故障主备切换时发送邮件通知配置项。
>  #这里我直接改成了调邮件和短信服务接口，没有使用keepalived的notify功能。
>  #短信和邮件接口配置在/etc/keepalived/haproxy_check.sh脚本里。
> 
>  #每个keepalived节点的唯一标识，不能与备机相同。
>  router_id keepalived_backup_192-168-10-105
> }
> 
> #检测HAProxy脚本
> vrrp_script check_haproxy {
> script "/etc/keepalived/haproxy_check.sh" #脚本所在的目录
> interval 10 #检测 haproxy 心跳频率：每隔10秒检测一次
> weight 2 #权重
> }
> 
> #虚拟路由器配置
> vrrp_instance haproxy {
>  state BACKUP #设置虚拟路由器状态为BACKUP，表示为备。
>  interface ens33 #绑定当前虚拟路由器所使用的物理网卡，如eth0、bond0等，可通过ifconfig获得。
>  virtual_router_id 51 #每个虚拟路由器的唯一标识。同属一个虚拟路由器的多个keepalived节点必须相同，务必要确保在同一网络中此值唯一。
>  priority  80 #当前物理节点在此虚拟路由器中的优先级，值越大优先级越高。注意：主机的优先权要比备机高。
>  advert_int 1 #心跳检查频率，单位：秒。
> 
>  #认证机制
>  authentication {
>      auth_type PASS #认证类型
>      auth_pass admin #秘钥，同一虚拟路由器的多个keepalived节点auth_pass值必须保持一致
>  }
> 
>  #虚拟路由器的VIP，不指定网卡时默认添加在eth0上。在添加VIP地址时，需确保将要使用的VIP不存在，避免冲突。
>  virtual_ipaddress {
>      192.168.10.3 #对外开放的虚拟ip
>  }
> 
>  #调用检测HAProxy的脚本
>  track_script {
>      check_haproxy
>  }
> }
> ```
>
> 

（10）编写HAProxy检测脚本

接着，我们需要编写脚本：`haproxy_check.sh`
脚本作用：检测haproxy服务，如果haproxy服务挂了则先尝试自动重启haproxy服务；如果重启不成功， 则关闭Keepalived服务并切换到backup。

```shell
#!/bin/bash
#haproxy存活检测脚本
#当发现haproxy服务挂掉时，先自动重启HAProxy的服务，如果启动不成功则关闭当前节点的Keepalived服务，并将虚拟vip飘到备用节点上。

#-----------全局变量------------start
#本机ip
localIP=`ip a|grep inet|grep global|grep brd|head -n 1|awk '{printf $2}'|cut -d "/" -f1`
#backup节点ip
backupIP="172.22.40.105"

#邮件联系人
mailTo="yuanjiabo@xxx.com"
#短信联系人
phoneTo="1342621xxxx"

#邮件主题
mailsubject="队列集群高可用之Keepalived主备切换通知（vip floating）"
mailsubjectForHAProxyUp="队列集群高可用之HAProxy服务挂掉通知"
#邮件内容
nodeList="172.22.40.104、172.22.40.105、172.22.40.106"
mailbody="队列集群（集群节点:${nodeList}）高可用keepalived vrrp transition, 虚拟VIP地址切换至：${backupIP}，切换前的地址：${localIP}"
#短信内容
messagebody="检测到节点${localIP}的HAProxy服务挂掉，并尝试重启失败，停掉此节点上的keepalived服务，虚拟vip将floating到backup节点：${backupIP}"
messageForHAProxyUp="检测到节点${localIP}的HAProxy服务挂掉，正在尝试重启中...重启成功！"
#-----------全局变量------------end

#haproxy服务存活检测
if [ $(ps -C haproxy --no-header | wc -l) -eq 0 ];then
    #记录日志
    echo "检测到节点${localIP}的HAProxy服务挂掉，正在尝试重启中..." >> /etc/keepalived/keepalived_master2backup.log
    #重启haproxy服务
    haproxy -f /etc/haproxy/haproxy.cfg

    sleep 2s

    #haproxy自动重启成功，邮件+短信通知
    if [ $(ps -C haproxy --no-header | wc -l) -eq 1 ];then
        echo "检测到节点${localIP}的HAProxy服务挂掉，正在尝试重启中...重启成功！" >> /etc/keepalived/keepalived_master2backup.log
        #发邮件
        curl "http://172.21.200.247/message.php?item=mail&mailto=${mailTo}&subject=${mailsubjectForHAProxyUp}&msg=${messageForHAProxyUp}"
        #发短信
        curl "http://172.21.200.247/message.php?phone=${phoneTo}&msg=${messageForHAProxyUp}"
    fi
fi

#这里最好休眠2s等待haproxy启动成功，不然下面的判断有可能还会出现找不到haproxy服务进程的情况
#注意：这个sleep时间一定要比keepalived.conf配置里"检测 haproxy 心跳频率：interval  10"设置的时间要短，否则将卡在sleep这！
sleep 2s

#自动重启不成功，进行vip切换操作，邮件+短信通知
if [ $(ps -C haproxy --no-header | wc -l) -eq 0 ];then
    echo "检测到节点${localIP}的HAProxy服务挂掉，尝试重启失败，停掉此节点上的keepalived服务，虚拟vip将飘到backup节点：${backupIP}" >> /etc/keepalived/keepalived_master2backup.log

    #发邮件
    curl "http://邮件服务ip:port/message.php?item=mail&mailto=${mailTo}&subject=${mailsubject}&msg=${mailbody}"

    #发短信
    curl "http://短信服务ip:port/message.php?phone=${phoneTo}&msg=${messagebody}"

    service keepalived stop
fi

```

（11）启动keepalived的服务

> 启动顺序：先启动master节点服务，再启动backup节点的服务。

```shell
#★启动keepalived服务
service keepalived start

#★查看keepalived状态
[root@rabbitmq-1 keepalived] service keepalived status
Redirecting to /bin/systemctl status  keepalived.service
● keepalived.service - LVS and VRRP High Availability Monitor
   Loaded: loaded (/usr/lib/systemd/system/keepalived.service; disabled; vendor preset: disabled)
   Active: active (running) since Thu 2022-10-13 15:58:35 CST; 8min ago
  Process: 19704 ExecStart=/usr/sbin/keepalived 
  
#查看keepalived启动日志：
journalctl -xe

#查看keepalived日志
tail -f  /var/log/messages

#停掉keepalived服务
service keepalived stop

```

（11）测试vip是否可用

```
http://192.168.10.3:35672/#/
```

（12）测试vip飘移（主备切换）并验证短信和邮件通知

```shell
#启动 haproxy 服务
haproxy -f /etc/haproxy/haproxy.cfg
#停止 kaproxy 服务
kill -9 `cat /var/run/haproxy.pid`
#重新启动 haproxy 服务
haproxy -f /etc/haproxy/haproxy.cfg -st `cat /var/run/haproxy.pid`
#查看 haproxy 服务状态
ss -nplt | grep haproxy
#启动 keepalived 服务
service keepalived start
#查看 keepalived 服务状态
service keepalived status
#停止 keepalived 服务
service keepalived stop

```

