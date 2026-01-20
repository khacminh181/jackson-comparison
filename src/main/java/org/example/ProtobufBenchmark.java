package org.example;

import org.example.proto.UserEventProto.UserEvent;

public class ProtobufBenchmark {

    public static void main(String[] args) throws Exception {

        UserEvent event = UserEvent.newBuilder()
                .setUserId("u123")
                .setAction("LOGIN")
                .setTimestamp(System.currentTimeMillis())
                .setScore(42)
                .build();

        int iterations = 1_000_000;

        // Warm-up JVM
        for (int i = 0; i < 100_000; i++) {
            event.toByteArray();
        }

        // Serialize
        long start = System.nanoTime();
        byte[] protoBytes = null;
        for (int i = 0; i < iterations; i++) {
            protoBytes = event.toByteArray();
        }
        long serializeTime = System.nanoTime() - start;

        // Deserialize
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            UserEvent.parseFrom(protoBytes);
        }
        long deserializeTime = System.nanoTime() - start;

        System.out.println("=== Protobuf ===");
        System.out.println("Payload size (bytes): " + protoBytes.length);
        System.out.println("Serialize ms: " + serializeTime / 1_000_000);
        System.out.println("Deserialize ms: " + deserializeTime / 1_000_000);
    }
}
