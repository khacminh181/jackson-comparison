package org.example.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;

public class KafkaConsumerBenchmark {

    public static <T> void run(
            String label,
            Consumer<String, T> consumer,
            int expected
    ) {

        int count = 0;
        long start = System.nanoTime();

        while (count < expected) {
            ConsumerRecords<String, T> records =
                    consumer.poll(Duration.ofMillis(100));

            count += records.count();
        }

        long elapsed = System.nanoTime() - start;

        System.out.println("=== " + label + " Consumer ===");
        System.out.println("Messages: " + count);
        System.out.println("Time ms: " + elapsed / 1_000_000);
        System.out.println("Throughput msg/s: " +
                (count * 1_000_000_000L / elapsed));
    }
}
