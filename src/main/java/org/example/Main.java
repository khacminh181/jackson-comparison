package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Message;
import org.example.service.BenchmarkService;
import org.example.service.impl.BenchmarkServiceImpl;
import org.example.service.impl.JacksonCodec;
import org.example.service.impl.ProtobufCodec;
import org.example.utils.ProtobufUtil;
import redis.clients.jedis.Jedis;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception{
        // 1. Redis connection
        Jedis jedis = new Jedis("localhost", 6379);
        System.out.println(jedis.getConnection());

        BenchmarkService benchmarkService = new BenchmarkServiceImpl();

        // 2. Load REAL payload JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonPayload = mapper.readTree(
                Files.readAllBytes(Paths.get("src/main/resources/payload.json"))
        );
        Message protoPayload = ProtobufUtil.fromJson(jsonPayload.toString());

        System.out.println("=== BENCHMARK Serialize Deserialize===");
        int rounds = 100;

        // 4. Run Jackson
        benchmarkService.runBenchmark(
                "Jackson JSON",
                new JacksonCodec(),
                jsonPayload,
                rounds
        );

        // 5. Run Protobuf
        benchmarkService.runBenchmark(
                "Protobuf Binary",
                new ProtobufCodec(protoPayload),
                protoPayload,
                rounds
        );
//
        System.out.println("=== BENCHMARK REDIS ===");

        // 4. Run Jackson
        benchmarkService.runRedisBenchmark(
                "Jackson JSON",
                new JacksonCodec(),
                jsonPayload,
                jedis,
                rounds
        );

        // 5. Run Protobuf
        benchmarkService.runRedisBenchmark(
                "Protobuf Binary",
                new ProtobufCodec(protoPayload),
                protoPayload,
                jedis,
                rounds
        );

    }
}