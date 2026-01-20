package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.UserEvent;

import java.nio.file.Files;
import java.nio.file.Paths;

public class JacksonBenchmark {

    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        // Load real payload
        byte[] jsonBytes = Files.readAllBytes(Paths.get("payload.json"));

        // Deserialize to generic tree (realistic)
        JsonNode event = mapper.readTree(jsonBytes);

        int iterations = 1_000_000;

        // Serialize
        long start = System.nanoTime();
        byte[] serialized = null;
        for (int i = 0; i < iterations; i++) {
            System.out.println(i);
            serialized = mapper.writeValueAsBytes(event);
        }
        long serializeTime = System.nanoTime() - start;

        // Deserialize
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            mapper.readValue(jsonBytes, UserEvent.class);
        }
        long deserializeTime = System.nanoTime() - start;

        System.out.println("=== Jackson JSON ===");
        System.out.println("Payload size (bytes): " + jsonBytes.length);
        System.out.println("Serialize ms: " + serializeTime / 1_000_000);
        System.out.println("Deserialize ms: " + deserializeTime / 1_000_000);
    }
}

