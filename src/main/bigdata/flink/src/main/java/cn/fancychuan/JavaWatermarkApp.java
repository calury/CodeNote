package cn.fancychuan;


import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.eventtime.AscendingTimestampsWatermarks;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

/**
 * 演示了Watermark的使用，以及结合Flink的对迟到数据的处理机制
 */
public class JavaWatermarkApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 多并行度时，Watermark以最小的为准
        env.setParallelism(1);
        // 设置使用Event Time这种语义
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // 设置生产Watermark的周期为100毫秒，默认是200毫秒
        env.getConfig().setAutoWatermarkInterval(100);

        DataStream<String> inputStream = env.socketTextStream("hadoop101", 7777);

        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] items = line.split(",");
            return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
        })
                // 升序事件时间的数据，设置事件时间和watermark
//                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<SensorReading>() {
//                    @Override
//                    public long extractAscendingTimestamp(SensorReading element) {
//                        return element.getTimestamp() * 1000L;
//                    }
//                })
                // 乱序事件时间的数据，设置时间戳和watermark
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(2)) {
                    // BoundedOutOfOrdernessTimestampExtractor有界无序时间提取器
                    // 传进来的Time.seconds(2)，表示最大的乱序程度
                    @Override
                    public long extractTimestamp(SensorReading element) {
                        // return的单位是毫秒
                        return element.getTimestamp() * 1000L;
                    }
        });
        // 基于事件时间的开窗聚合，统计5秒内温度的最小值
        OutputTag<SensorReading> outputTag = new OutputTag<SensorReading>("late"){};
        SingleOutputStreamOperator<SensorReading> minTempStream = dataStream.keyBy("id")
                .timeWindow(Time.seconds(5))
                // 设置了watermark之后，还可以设置允许的迟到时间。这里例子中，每5秒一个窗口，1分钟之内这个窗口都不会关闭，每来一个数据更新一次结果
                .allowedLateness(Time.minutes(1))
                // 兜底方案，将超过1分钟都没有到达的数据放到侧输出流中
                .sideOutputLateData(outputTag)
                .minBy("temperature");

        minTempStream.print("minTemp");
        DataStream<SensorReading> sideOutput = minTempStream.getSideOutput(outputTag);
        sideOutput.print("late");

        env.execute("testWatermark");
    }
}
