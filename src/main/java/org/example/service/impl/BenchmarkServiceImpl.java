package org.example.service.impl;

import org.example.model.UserEvent;
import org.example.service.BenchmarkService;
import org.example.service.Codec;
import redis.clients.jedis.Jedis;

public class BenchmarkServiceImpl implements BenchmarkService {
    @Override
    public  <T> void runRedisBenchmark(
            String name,
            Codec<T> codec,
            T payload,
            Jedis jedis,
            int rounds
    ) {
        try {
            // Warm-up (quan trọng)
            for (int i = 0; i < 10; i++) {
                byte[] warm = codec.serialize(payload);
                codec.deserialize(warm);
            }

            long start = System.nanoTime();
            byte[] bytes = null;

            for (int i = 0; i < rounds; i++) {
                bytes = codec.serialize(payload);
                jedis.set("event".getBytes(), bytes);
                byte[] received = jedis.get("event".getBytes());
                codec.deserialize(received);
            }

            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            System.out.println("=== " + name + " ===");
            System.out.println("Rounds: " + rounds);
            System.out.println("Payload size: " + bytes.length + " bytes");
            System.out.println("Total time: " + elapsedMs + " ms");
            System.out.println("Avg per round: " +
                    (elapsedMs * 1.0 / rounds) + " ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void runBenchmark(String name, Codec<T> codec, T payload, int rounds) {
        try {
            // Warm-up (quan trọng)
            for (int i = 0; i < 10; i++) {
                byte[] warm = codec.serialize(payload);
                codec.deserialize(warm);
            }

            int iterations = 1_000_000;

            // Serialize
            long start = System.nanoTime();
            byte[] serialized = null;
            for (int i = 0; i < iterations; i++) {
                serialized = codec.serialize(payload);
            }
            long serializeTime = System.nanoTime() - start;

            // Deserialize
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                codec.deserialize(serialized);
            }
            long deserializeTime = System.nanoTime() - start;

            System.out.println("=== " + name + " ===");
            System.out.println("Payload size (bytes): " + serialized.length);
            System.out.println("Serialize ms: " + serializeTime / 1_000_000);
            System.out.println("Deserialize ms: " + deserializeTime / 1_000_000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
