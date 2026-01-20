package org.example.service;

import redis.clients.jedis.Jedis;

public interface BenchmarkService {
    <T> void runRedisBenchmark(
            String name,
            Codec<T> codec,
            T payload,
            Jedis jedis,
            int rounds
    );

    <T> void runBenchmark(
            String name,
            Codec<T> codec,
            T payload,
            int rounds
    );
}
