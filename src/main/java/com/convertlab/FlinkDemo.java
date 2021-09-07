package com.convertlab;

import com.convertlab.config.ConfigLoad;
import com.convertlab.source.TestKafka;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        ConfigLoad configLoad = new ConfigLoad();
        configLoad.loadYml();
        TestKafka testKafka = new TestKafka();
        testKafka.process(env);

        env.execute();
    }
}
