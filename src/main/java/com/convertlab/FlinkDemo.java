package com.convertlab;

import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.HashMap;
import java.util.Map;

public class FlinkDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000); // 非常关键，一定要设置启动检查点！！
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Map properties= new HashMap();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "test");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("topic", "flink_demo_test");
        // parse user parameters

        ParameterTool parameterTool = ParameterTool.fromMap(properties);

        FlinkKafkaConsumer consumer = new FlinkKafkaConsumer(
                parameterTool.getRequired("topic"), new SimpleStringSchema(), parameterTool.getProperties());
        DataStream<String> messageStream = env.addSource(consumer);

        DataStream<Task> dataStream = messageStream.rebalance().map(new MapFunction<String, Task>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Task map(String value) throws Exception {
                Object message= JSON.parse(value);
                Map<Object,Object> map = (Map)message;
                return new Task(map.get("targetId").toString(),"success",map.get("tenantId").toString(),map.get("type").toString().split("_")[1]);
            }
        });

        DataStream resultStream = dataStream.keyBy("tenantId").keyBy("taskId").timeWindow(Time.seconds(1))
                .aggregate(new AggregateFunction<Task, Integer, Integer>(){
                    public Integer createAccumulator() {
                        return 0;//初始值
                    }

                    public Integer add(Task task, Integer integer) {
                        return integer + 1; //每次来了+1
                    }

                    public Integer getResult(Integer integer) {
                        return integer; //返回的结果
                    }

                    public Integer merge(Integer integer, Integer acc1) {
                        return null; //用在合并操作
                    }
                });


        resultStream.print();

        env.execute();
    }
}
