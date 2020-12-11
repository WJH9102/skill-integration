# skill-integration
用于整合学过的技能知识
<br>
当前版本：v1.0.1
包含技能点：
1. kafka数据消费
2. es和mysql增量数据同步
3. 阿里巴巴canal的使用（读取mysql binlog并将增量数据同步至kafka）
4. es增删改查
# canal 相关配置
canal [下载地址](https://github.com/alibaba/canal) ，及相关文档说明
canal.properties配置
```properties
# 选择推送的消息队列类型
canal.serverMode = kafka
# kafka集群地址
canal.mq.servers = 192.168.2.128:9092,192.168.2.130:9092,192.168.2.132:9092
```
instance.properties配置
```properties
# 数据库地址
canal.instance.master.address=192.168.1.33:3306
# 数据库用户名密码
canal.instance.dbUsername=root
canal.instance.dbPassword=123456
# 指定过滤的数据库及数据表可配置多个 , 隔开，这里指定的是mydb下的所有表 \\ 为转义符
canal.instance.filter.regex=mydb\\..*
# kafka队列主题，可默认
canal.mq.topic=example                                                                                                                                                                        
# 可以根据数据库名和表名动态创建 topic
#canal.mq.dynamicTopic=mytest1.user,mytest2\\..*,.*\\..*
```
# mysql配置
[mysqld]下添加如下配置，用于产生binlog
```lombok.config
log-bin=mysql-bin
binlog-format=ROW
server_id=1
```
