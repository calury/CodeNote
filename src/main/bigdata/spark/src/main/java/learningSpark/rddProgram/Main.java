package learningSpark.rddProgram;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.Partition;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.junit.Test;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;


public class Main implements Serializable { // TODO：如何避免主类也需要序列化？
    /**
     * 实例化一个sc并将集合并行化
     * 使用的是 JavaSparkContext 而不是 SparkContext
     * python、scala使用的是SparkContext
     */

    private static SparkConf conf = new SparkConf().setMaster("local").setAppName("javaSpark");
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    /**
     * 1. word count
     */
    public static void testWordCount() {
        String input = "E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md";
        String output = "E:\\JavaWorkshop\\bigdata-learn\\spark\\target\\out\\wordcount";
        WordCount.run(sc, input, output);
    }

    /**
     * 2. 创建RDD的两种方式
     */
    @Test
    public void testGenRdd() {
        // 使用 sc.parallelize并行化
        List<String> list = Arrays.asList("hello", "spark");
        JavaRDD<String> lines = sc.parallelize(list);
        System.out.println(lines);
        System.out.println(lines.collect());
        // 使用sc.textFile从文件中读取并创建为RDD
        JavaRDD<String> inputRDD = sc.textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md");
        System.out.println(inputRDD.take(5));
    }

    /**
     * 3. 测试转化函数：map() mapPartitions() mapPartitionsWithIndex()
     * mapPartitionsWithIndex
     *  1> 在Java中，只能使用mapPartitionsWithIndex(func, rea) 这个方法 todo:第二个参数的作用是什么
     *  2> 而在Scala中可以使用mapPartitionsWithIndex(func)
     *  TODO：为什么Scala可以有两个方法，而在Java中只能用一个方法，Java和Scala的配合差异在什么地方
     */
    @Test
    public void testTransformation() { // todo: 为什么这个函数的运行会要求Main这个类是可序列化的，否则会报Task not serializable的错误？
        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 2, 4, 6, 10, 30, 60, 90, 100, 400), 3);
        JavaRDD<Integer> mapRdd = rdd.map(item -> item * 2);
        // 不使用匿名函数实现mapPartitions
        JavaRDD<Integer> mapPtsRdd = rdd.mapPartitions(new FlatMapFunction<Iterator<Integer>, Integer>() {
            @Override
            public Iterator<Integer> call(Iterator<Integer> integerIterator) throws Exception {
                ArrayList<Integer> result = new ArrayList<>();
                while (integerIterator.hasNext()) {
                    Integer next = integerIterator.next();
                    result.add(next * 3);
                }
                return result.iterator();
            }
        });
        // 使用匿名函数实现
        JavaRDD<Integer> mapPtsRdd2 = rdd.mapPartitions(iterator -> {
            ArrayList<Integer> result = new ArrayList<>();
            iterator.forEachRemaining(item -> result.add(item * 4));
            return result.iterator();
        });
        // 使用lambda表达式实现mapPartitionsWithIndex
        JavaRDD<String> mapPartIndexRDD1 = rdd.mapPartitionsWithIndex((index, items) -> {
            LinkedList<String> result = new LinkedList<>();
            items.forEachRemaining(item -> result.add(item + ", 分区号为：" + index));
            return result.iterator();
        }, false);
        // 不使用lambda表达式
        JavaRDD<String> mapPartIndexRDD2 = rdd.mapPartitionsWithIndex(new Function2<Integer, Iterator<Integer>, Iterator<String>>() {
            @Override
            public Iterator<String> call(Integer index, Iterator<Integer> items) throws Exception {
                LinkedList<String> result = new LinkedList<>();
                items.forEachRemaining(item -> result.add(item + ", 分区号为：" + index));
                return result.iterator();
            }
        }, true);

        mapPartIndexRDD1.collect().forEach(System.out::println);
        System.out.println("==============");
        mapPartIndexRDD2.collect().forEach(System.out::println);
        // glom算子
        JavaRDD<List<Integer>> glomRDD = rdd.glom();

    }

    /**
     * 4. 向Spark传递函数
     */
    public static void testPassFunction() {
        PassFunction pf = new PassFunction(sc);
        pf.testAnonymousInnerClass();
        pf.testNamedClass();
        pf.testLambda();
    }

    /**
     * 5. 测试采样函数
     *
     * 使用场景： 检查导致数据倾斜的key
     */
    public static void testSample() {
        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 3, 5, 7, 8));
        // 元素不可多次采样，每个元素被抽到的概率是0.5
        JavaRDD<Integer> sample1 = rdd.sample(false, 0.5);
        sample1.foreach(x -> System.out.print(x + "\t"));
        // 元素可以多次采样，每个元素被抽到的期望次数是2
        System.out.println("===========");
        JavaRDD<Integer> sample2 = rdd.sample(true, 2);
        sample2.foreach(x -> System.out.print(x + "\t"));
    }

    /**
     * 6. 测试行动操作
     */
    public static void testAction() {
        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 3, 5, 7, 8));
        // reduce() 用于求RDD中元素的累加
        System.out.println(rdd.reduce((a, b) -> a + b));
        // fold() 每个分区第一次调用时都有个初始值作为第一次调用时的结果
        // setMaster("local[4]") 那么rdd就有4个分区，那么结果会比reduce的多500
        System.out.println("RDD分区数为： "+ rdd.getNumPartitions() + "\t运行结果: " + rdd.fold(100, (a, b) -> a + b));
        // aggregate()
        AvgCount avgCount = rdd.aggregate(new AvgCount(0, 0),
                (acc, value) -> new AvgCount(acc.total + value, acc.cnt + 1),
                (acc1, acc2) -> new AvgCount(acc1.total + acc2.total, acc1.cnt + acc2.cnt)
                );
        System.out.println("aggregate求平均： " + avgCount.avg());
    }

    /**
     * 7. 将IntegerRDD转为DoubleRDD并求平均
     */
    public static void testConvertAndMean() {
        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 3, 5));
        JavaDoubleRDD doubleRDD = rdd.mapToDouble(x -> x*x); // 注意这个不是 JavaRDD<Double>
        doubleRDD.foreach(x -> System.out.println("[doubleRDD]" + x));
        System.out.println("平均值为： " + doubleRDD.mean());
    }

    /**
     * 8. PairRDD相关操作
     */
    public static void testPairRDD() {
        JavaRDD<String> lines = sc.textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md");
        // 以第一个字母作为key创建 PairRDD，注意要用Tuple2创建元素
        JavaPairRDD<String, String> pairRDD1 = lines.mapToPair(line -> new Tuple2<>(line.split(" ")[0], line));
        JavaPairRDD<String, Integer> pairRDD2 = sc.parallelizePairs(
                Arrays.asList(new Tuple2<>("fancy", 24), new Tuple2<>("fancy", 30),
                        new Tuple2<>("chuan", 20), new Tuple2<>("what", 3)));

        // 对具有相同key的value求平均
        JavaPairRDD mean = pairRDD2.mapValues(value -> new Tuple2(value, 1))
                // 把 ("fancy", 24) 转为 ("fancy", (24,1))，再按照相同的key累加 (24, 1) (30, 1)
                .reduceByKey((t1, t2) -> new Tuple2<>((Integer) t1._1 + (Integer) t2._1, (Integer) t1._2 + (Integer) t2._2))
                // 对累加后的 (54, 2) 求 value 的平均值
                .mapValues(valueCnt -> (Integer) valueCnt._1 / (Integer) valueCnt._2);
        mean.foreach(x -> System.out.println(x));

        System.out.println("==== 使用combinerByKey求平均 ====");
        JavaPairRDD avgCounts = pairRDD2.combineByKey(
                (value -> new Tuple2<Integer, Integer>(value, 1)),
                ((kv1, value) -> new Tuple2<>(kv1._1 + value, kv1._2 + 1)),
                ((kv1, kv2) -> new Tuple2<>(kv1._1 + kv2._1, kv1._2 + kv2._2)))
                .mapValues(valueCnt -> (Integer) valueCnt._1 / (Integer) valueCnt._2);
        Map<String, Double> countMap = avgCounts.collectAsMap();
        for (Map.Entry<String, Double> entry: countMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    /**
     * 9. 数据分区相关
     */
    public static void testPartition() {
        // 指定最少5个分区
        JavaRDD<String> lines = sc.textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md", 5);
        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 3, 5, 7, 8, 15, 20, 50), 3);
        System.out.println("分区方式：" + rdd.partitioner());
        System.out.println("分区数量：" + rdd.getNumPartitions());
        System.out.println("==========");
        List<Partition> partitions = rdd.partitions();
        for (Partition p: partitions) {
            System.out.println(p.index());
            System.out.println(p);
        }
        // 对每个分区执行操作：打印内容
        rdd.foreachPartition(iterator -> {
            System.out.println("===============================================");
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        });
        System.out.println("分区1的内容为：");
        System.out.println(StringUtils.join(rdd.collectPartitions(new int[] {1}), "\t"));
        System.out.println("分区0的内容为：");
        List<Integer>[] first = rdd.collectPartitions(new int[] {0}); // 特别注意分区返回的内容的格式为： [[], [], [] ...]
        for (List<Integer> item: first) {
            System.out.println(StringUtils.join(item, "\t"));
        }
    }

    /**
     * 10. 自定义分区器
     */
    public static void testDomainPartitioner() {
        JavaRDD<String> urls = sc.textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\urls.txt");
        JavaPairRDD<String, String> pairURL = urls.mapToPair(url -> new Tuple2<>(url.split(" ")[1], url.split(" ")[0]));
        JavaPairRDD<String, String> partedURL = pairURL.partitionBy(new DomainNamePartitioner(3));
        partedURL.foreachPartition(iterator -> {
            System.out.println("===============================================");
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        });
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.testGenRdd();
    }
}
