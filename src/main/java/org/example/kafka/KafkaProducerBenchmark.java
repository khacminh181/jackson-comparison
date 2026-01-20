package org.example.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class KafkaProducerBenchmark {

    public static <T> void run(
            String label,
            Producer<String, T> producer,
            String topic,
            T event,
            int messages
    ) throws Exception {

        long start = System.nanoTime();
        int payloadSize = 0;

        for (int i = 0; i < messages; i++) {
            ProducerRecord<String, T> record =
                    new ProducerRecord<>(topic, "key", event);

            RecordMetadata meta = producer.send(record).get();
            payloadSize = record.value().toString().length();
        }

        long elapsed = System.nanoTime() - start;

        System.out.println("=== " + label + " Producer ===");
        System.out.println("Messages: " + messages);
        System.out.println("Time ms: " + elapsed / 1_000_000);
        System.out.println("Throughput msg/s: " +
                (messages * 1_000_000_000L / elapsed));
    }
}

