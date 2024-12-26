
拉链表的两种闭链方式：
- enddate=新链数据的业务日期-1
    - 这种情况，startdate可能等于enddate
    - 数据的有效周期为[startdate, enddate]
    - 取某天的有效数据：startdate<=${datadate} and ${datadate} =< enddate
```
acct_no     card_no    startdate    enddate
A001        11134       20201022    20201023    # 这里的20201023为20201024-1，也就是在20201025这一天跑批的时候闭链的
A001        11134       20201024    20201027
A001        11134       20201028    99991231
```
- enddate=新链数据的业务日期
    - 这种情况，startdate一定小于enddate
    - 数据的有效周期为[startdate, enddate)
    - 取某天的有效数据：startdate<=${datadate} and ${datadate} < enddate
```
acct_no     card_no    startdate    enddate
A001        11134       20201022    20201024    # 这里的20201024就是业务时间，也就是在20201025这一天跑批的时候闭链的
A001        11134       20201024    20201028
A001        11134       20201028    99991231
```
> 业务时间=数据在业务系统产生的时间，一遍为跑批时间-1，也就是T+1时效

**注意！**使用拉链表的时候，要优先判断是采用的哪一种闭链方式！

拉链表
- end_date='99991231' 表示该条记录处于最新有效状态
- 用拉链表的时候，尤其需要知道哪些是主键！比如acct_no，card_no是主键。也就意味着一个账户下，每换一张卡就会有一条新的链
```
acct_no     card_no    startdate    enddate
A001        11134       20201022    20201023
A001        11134       20201024    20201027
A001        11134       20201028    99991231
A001        11139       20201029    20201112   # 开了一条新的链
A001        11139       20201112    99991231
```

拉链表的更新：
- 当天的数据为增量数据，即包括：新增 + 变动
- 当天的数据为全量数据，即包括：新增 + 变动 + 不变的数据


### 参考资料
1. [一文搞定数据仓库之拉链表，流水表，全量表，增量表](https://blog.csdn.net/mtj66/article/details/78019370)
2. [数据仓库中的拉链表（hive实现) - 大鹰的天空 - CSDN博客](https://blog.csdn.net/u014770372/article/details/77069518)


#### 几个by的区别对比
- ORDER BY 会对输入做全局排序，因此只有一个 Reduce（多个 Reduce 无法保证全局有序）会导致当输入规模较大时，需要较长的计算时间，
- Hive中指定了sort by，那么在每个reducer端都会做排序，也就是说保证了局部有序好处是：执行了局部排序之后可以为接下去的全局排序提高不少的效率（其实就是做一次归并排序就可以做到全局排序了。
- ditribute by
  - DISTRIBUTE BY是控制map的输出在reducer是如何划分的
  - DISTRIBUTE BY是控制在map端如何拆分数据给reduce端的。
  - DISTRIBUTE BY可以控制某个特定行应该到哪个reducer。
- cluster by的功能就是distribute by和sort by相结合（根据指定字段输出到不同文件里，然后对各个文件里的数据排序）
```
distribute by a sort by a
等价于
cluster by a  -- 不支持asc desc
```