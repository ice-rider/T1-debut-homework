package com.example.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WeatherConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WeatherConsumer.class);
    private final KafkaConsumer<String, String> consumer;
    private final String topicName;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Statistics Maps
    private final Map<String, Integer> sunnyDaysByCity = new HashMap<>();
    private final Map<String, Integer> rainyDaysByCity = new HashMap<>();

    public WeatherConsumer() {
        objectMapper.registerModule(new JavaTimeModule());
        Properties properties = KafkaConfig.getConsumerProperties();
        this.consumer = new KafkaConsumer<>(properties);
        this.topicName = KafkaConfig.getTopicName();
        consumer.subscribe(Collections.singletonList(topicName));
    }

    public void consumeMessages() {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100)); // Poll for records every 100ms
                for (ConsumerRecord<String, String> record : records) {
                    String message = record.value();
                    logger.info("Received message: {}", message);

                    try {
                        WeatherRecord weatherRecord = objectMapper.readValue(message, WeatherRecord.class);
                        updateStatistics(weatherRecord);
                        printStatistics(); // Optional: Print statistics after each message
                    } catch (Exception e) {
                        logger.error("Error parsing message", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error consuming messages", e);
        } finally {
            consumer.close();
        }
    }

    private void updateStatistics(WeatherRecord weatherRecord) {
        String city = weatherRecord.getCity();
        String condition = weatherRecord.getCondition();

        if ("Sunny".equalsIgnoreCase(condition)) {
            sunnyDaysByCity.put(city, sunnyDaysByCity.getOrDefault(city, 0) + 1);
        } else if ("Rainy".equalsIgnoreCase(condition)) {
            rainyDaysByCity.put(city, rainyDaysByCity.getOrDefault(city, 0) + 1);
        }
    }

    private void printStatistics() {
        System.out.println("--- Weather Statistics ---");
        System.out.println("Sunny Days by City: " + sunnyDaysByCity);
        System.out.println("Rainy Days by City: " + rainyDaysByCity);
        System.out.println("--------------------------");
    }

    public void close() {
        consumer.close();
    }
}
