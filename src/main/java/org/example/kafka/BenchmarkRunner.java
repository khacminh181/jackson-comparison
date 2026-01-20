package org.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.example.kafka.JacksonUserEventSerializer;
import org.example.kafka.ProtobufUserEventSerializer;
import org.example.model.UserEvent;
import org.example.proto.UserEventProto;

import java.util.Properties;

public class BenchmarkRunner {

    private static final String BOOTSTRAP = "localhost:9092";

    public static void main(String[] args) throws Exception {

        int messages = 100_000;
        String topic = "user-events-benchmark";

        // Jackson event
        UserEvent jsonEvent =
                new UserEvent("u123", "LOGIN",
                        System.currentTimeMillis(), 42);

        // Protobuf event
        UserEventProto.UserEvent protoEvent =
                UserEventProto.UserEvent.newBuilder()
                        .setUserId("u123")
                        .setAction("LOGIN")
                        .setTimestamp(System.currentTimeMillis())
                        .setScore(42)
                        .build();

        // Run Jackson
        KafkaProducerBenchmark.run(
                "Jackson",
                jacksonProducer(),
                topic,
                jsonEvent,
                messages
        );

        // Run Protobuf
        KafkaProducerBenchmark.run(
                "Protobuf",
                protobufProducer(),
                topic,
                protoEvent,
                messages
        );
    }

    // ---------- PRODUCERS ----------

    private static Producer<String, UserEvent> jacksonProducer() {
        Properties props = baseProducerProps();
        props.put("value.serializer",
                JacksonUserEventSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    private static Producer<String, UserEventProto.UserEvent> protobufProducer() {
        Properties props = baseProducerProps();
        props.put("value.serializer",
                ProtobufUserEventSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    private static Properties baseProducerProps() {
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "1");
        props.put("linger.ms", "5");
        props.put("batch.size", "16384");
        return props;
    }
}
