package com.convertlab.config;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class ConfigLoad {
    static Properties properties;

    public synchronized void loadProperties() {
        if (null != properties) {
            return ;
        }

        Properties properties = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("/flink.properties");
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConfigLoad.properties = properties;
    }

    public synchronized void loadYml() {
        if (null != properties) {
            return ;
        }

        try {
            InputStream in = this.getClass().getResourceAsStream("/flink.yml");
            properties = org.ho.yaml.Yaml.loadType(in, Properties.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Properties getPropertiesByName(String name){
        Properties propertiesByName = new Properties();
        propertiesByName.putAll((Map<?, ?>) ConfigLoad.properties.get(name));
        return propertiesByName;
    }
}
