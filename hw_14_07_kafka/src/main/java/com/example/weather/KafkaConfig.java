package com.example.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class KafkaConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);
    private static final Properties properties = loadProperties();

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = KafkaConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("Sorry, unable to find application.properties");
                return props;
            }
            props.load(input);
        } catch (IOException ex) {
            logger.error("Error loading application.properties", ex);
        }
        return props;
    }

    public static Properties getProducerProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", getKafkaBroker());
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return properties;
    }

    public static Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", getKafkaBroker());
        properties.put("group.id", getKafkaGroup());
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        return properties;
    }

    public static String getKafkaBroker() {
        String kafkaBroker = System.getenv("KAFKA_BROKER");
        return kafkaBroker != null ? kafkaBroker : properties.getProperty("kafka.broker", "localhost:9092");
    }

    public static String getTopicName() {
        return properties.getProperty("kafka.topic");
    }

    public static String getKafkaGroup() {
        return properties.getProperty("kafka.group");
    }
}
