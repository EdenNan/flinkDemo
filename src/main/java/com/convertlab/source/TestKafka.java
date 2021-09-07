package com.convertlab.source;

import com.alibaba.fastjson.JSON;
import com.convertlab.config.ConfigLoad;
import com.convertlab.domain.Task;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Map;
import java.util.Properties;

public class TestKafka {
    public void process(StreamExecutionEnvironment env){
        Properties kafkaProperties = ConfigLoad.getPropertiesByName("kafka");
        FlinkKafkaConsumer consumer = new FlinkKafkaConsumer((String) kafkaProperties.get("topic"), new SimpleStringSchema(), kafkaProperties);
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
    }
}
