package com.example.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final int MESSAGE_LIMIT = 20;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        WeatherProducer producer = new WeatherProducer();
        WeatherConsumer consumer = new WeatherConsumer();

        Future<?> consumerTask = executorService.submit(consumer::consumeMessages);

        Future<?> producerTask = executorService.submit(() -> {
            try {
                for (int i = 0; i < MESSAGE_LIMIT; i++) {
                    producer.sendMessage();
                    TimeUnit.SECONDS.sleep(2);
                }
                logger.info("Producer sent {} messages, stopping.", MESSAGE_LIMIT);
            } catch (InterruptedException e) {
                logger.warn("Producer interrupted", e);
                Thread.currentThread().interrupt(); // Restore interrupt status. Important!
            } finally {
                producer.close();
            }
        });


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");
            executorService.shutdownNow();
            try {
                consumerTask.cancel(true); // Cancel Consumer first since it may be blocked on queue.take().


                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    logger.warn("Executor did not terminate in the specified time.");
                    // Optionally add code to handle tasks that did not terminate.
                }
            } catch (InterruptedException e) {
                logger.error("Error waiting for executor to shutdown", e);
                Thread.currentThread().interrupt();
            } finally {
                producer.close();
                consumer.close();
                logger.info("Shutdown complete");
            }
        }));
    }
}
