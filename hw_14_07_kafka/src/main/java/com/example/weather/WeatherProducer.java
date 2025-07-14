package com.example.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class WeatherProducer {

    private static final Logger logger = LoggerFactory.getLogger(WeatherProducer.class);
    private final KafkaProducer<String, String> producer;
    private final String topicName;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    private final List<String> cities = Arrays.asList("Moscow", "Saint Petersburg", "Kazan", "Sochi", "Vladivostok");
    private final List<String> conditions = Arrays.asList("Sunny", "Cloudy", "Rainy", "Snowy");


    public WeatherProducer() {
        objectMapper.registerModule(new JavaTimeModule());
        Properties properties = KafkaConfig.getProducerProperties();
        this.producer = new KafkaProducer<>(properties);
        this.topicName = KafkaConfig.getTopicName();
    }

    public void sendMessage() {
        try {
            WeatherRecord weatherRecord = generateRandomWeather();
            String weatherJson = objectMapper.writeValueAsString(weatherRecord); // Convert WeatherRecord to JSON
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, weatherJson); // Send JSON to Kafka

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("Error sending message", exception);
                } else {
                    logger.info("Message sent to topic: {}, partition: {}, offset: {}", metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
            logger.info("Sent message: {}", weatherJson);

        } catch (Exception e) {
            logger.error("Error sending message", e);
        }
    }

    private WeatherRecord generateRandomWeather() {
        String city = cities.get(random.nextInt(cities.size()));
        double temperature = -20 + (35 - (-20)) * random.nextDouble(); // Temperature between -20 and 35
        String condition = conditions.get(random.nextInt(conditions.size()));
        LocalDate date = LocalDate.now();

        return new WeatherRecord(city, temperature, condition, date);
    }

    public void close() {
        producer.close();
    }
}
