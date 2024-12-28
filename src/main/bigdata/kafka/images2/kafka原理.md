
#  一.原理基础知识

kafka 的作用: 系统解耦，异步通信，削峰填谷

kafka 是一个高吞吐量，分布式，分布订阅系统

kafka 的作用: 系统解耦，异步通信，削峰填谷

# 二. 常用概念
1.producer：
消息生产者，发布消息到 kafka 集群的终端或服务。
2.broker：
kafka 集群中包含的服务器。
3.topic：
每条发布到 kafka 集群的消息属于的类别，即 kafka 是面向 topic 的。
4.partition：
partition 是物理上的概念，每个 topic 包含一个或多个 partition。kafka 分配的单位是 partition。
5.consumer：
从 kafka 集群中消费消息的终端或服务。
6.Consumer group：
high-level consumer API 中，每个 consumer 都属于一个 consumer group，每条消息只能被 consumer group 中的一个 Consumer 消费，但可以被多个 consumer group 消费。
7.replica：
partition 的副本，保障 partition 的高可用。
8.leader：
replica 中的一个角色， producer 和 consumer 只跟 leader 交互。
9.follower：
replica 中的一个角色，从 leader 中复制数据。
10.controller：
kafka 集群中的其中一个服务器，用来进行 leader election 以及 各种 failover。
12.zookeeper：
kafka 通过 zookeeper 来存储集群的 meta 信息。

# 三.功能介绍

kafka 集群以topic 作为分类

每个topic 下面会有一组分区，

每个分区下面会有多个broker,1个broker 是leader,多个follower,leader 负责数据的读写，follwer负责同步分区中的数据，

分区数是3，每个topic下面有三个分区

分区副本因子是3 每个分区有三个副本，在不同的机器上，会有一个是leader,其他是follower

leader宕机，其他的follower会选举出新的leader,负责该分区数据的读写，leader和topic的部分的数据在zookeeper中。

消息队列的两种模式：

1.至多一次：消费生产者将数据写入消息队列，然后由消费者负责拉取消息服务器中的消息，一旦消息被确认消费之后，由消息服务器主动删除队列中的消息，这种消费方式一般只允许被一个消费者消费，并且消息队列中的数据不允许被重复消费。

2.没有限制： 生产者发完数据以后，该消息可以被多个消费者消费，并且同一个消费者可以多次消费服务器中的同一个记录，消息服务器一般可以长时间存储海量信息。

* 二. kafka 中的关键的概念：

分区数：3

分区副本因子：3

主题:topic01

消费组:

LEO：

水位线

kafka 会把topic中Record消息持久化到磁盘，持久化的时间通过配置文件制定

log.retention.hours=168

kafka底层会定期检查日志文件，然后将过期的数据从log中移除。

一般情况下存在三种情况：

1. At most once 消息可能会丢，但绝不会重复传输
2. At least one 消息绝不会丢，但可能会重复传输
3. Exactly once 每条消息肯定会被传输一次且仅传输一次
   


   当 producer 向 broker 发送消息时，一旦这条消息被 commit，由于 replication 的存在，它就不会丢。但是如果 producer 发送数据给 broker 后，遇到网络问题而造成通信中断，那 Producer 就无法判断该条消息是否已经 commit。虽然 Kafka 无法确定网络故障期间发生了什么，但是 producer 可以生成一种类似于主键的东西，发生故障时幂等性的重试多次，这样就做到了 Exactly once，但目前还并未实现。所以目前默认情况下一条消息从 producer 到 broker 是确保了 At least once，可通过设置 producer 异步发送实现At most once。

#  三. kafka 日志分区的原因

1.方便扩容。 如果数据太大，直接增加分区就可以了。

2.方便负载均衡。 在不同的机器上，存储的消息数据比较均匀

分区的策略：

1.轮训

2.hash

3.自定义分区

消费组

1.在同一个消费者组中的消费者实例，均匀消费这个组中的所有的数据

2.在不同组中的消费者，每条记录会广播的消费组中。

在同一个消费组中的消费者实例

1.如果新成员的介入，新成员会接管消费组中的其他成员的分区

2.如果有其他消费者实例挂掉，则有消费者组中的其他成员接管宕机的消费者的实例

kafka 同一个topic下，相同分区是有序的，不同分区是无序的。

如果要保证全局有序，可以通过只有一个分区topic 实现

kafka的消息是保存在磁盘上的

硬盘是机械结构，每次写入都会寻址--->写入。寻址是一个机械操作，它是最耗时的，硬盘最讨厌随机io，最喜欢数据io。为了提高读写硬盘的速度，kafka使用顺序io,这样节省了大量的内存开销，以及io寻址时间



现代分页存储利用内存，提高IO效率。利用操作系统的page 实现文件到物理内存内存的映射，完成

MMP的映射后，用户对内存的操作，会自动刷新到磁盘，极大的降低了io使用率。



# 四. kafka 高吞吐率的原因

1.利用多分区

2.顺序写入

3.MMFile(Memory mapped filed)

4. Zero copy

DMA是一种协处理器，是CPU 的一个代理，它负责一部分copy工作，从而减轻了cpu的负担。DMA的有点：中断少，cpu负担低。



























