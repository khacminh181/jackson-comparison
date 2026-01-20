# Jackson vs Protobuf – Round-trip Benchmark Documentation

## 1. Purpose

This benchmark compares **Jackson JSON** and **Protobuf Binary encoding** in a realistic, end-to-end scenario that simulates Kafka-style event transmission using Redis.

```
Object → serialize → send over network (Redis) → receive → deserialize
```

The objective is **not micro-benchmarking serialization in isolation**, but understanding:
- End-to-end latency
- Network impact of payload size
- Codec behavior in realistic producer → broker → consumer flows

---

## 2. Benchmark Scope

Two benchmark phases are executed.

### 2.1 In-memory Serialize / Deserialize

Measures:
- Serialization time
- Deserialization time
- Encoded payload size

Purpose:
- Compare raw CPU cost of codecs
- Exclude network influence

---

### 2.2 Redis Round-trip Benchmark (Network Simulation)

Measures:
- Total end-to-end latency
- Average latency per message

Flow:
```
Object
 → serialize
 → Redis SET
 → Redis GET
 → deserialize
```

Redis is intentionally used as a **Kafka stand-in** to model network I/O and broker overhead without performing Kafka throughput benchmarking.

---

## 3. Payload

- Source: `src/main/resources/payload.json`
- Represents realistic business data (ratings / resolver-style payload)
- Same semantic payload is used for both Jackson and Protobuf

### 3.1 Payload Handling Strategy

- Jackson operates directly on `JsonNode`
- Protobuf uses a generated schema
- JSON → Protobuf conversion is performed **once**, outside benchmark loops

This ensures **functional equivalence and fair comparison**.

---

## 4. Architecture Overview

```
┌──────────┐
│  Object  │
└────┬─────┘
     │ serialize
     ▼
┌──────────┐
│  bytes   │
└────┬─────┘
     │ Redis SET / GET
     ▼
┌──────────┐
│  bytes   │
└────┬─────┘
     │ deserialize
     ▼
┌──────────┐
│  Object  │
└──────────┘
```

This mirrors a real-world Kafka producer → broker → consumer lifecycle.

---

## 5. Key Components

### 5.1 Main Entry Point

`org.example.Main`

Responsibilities:
- Load payload from file
- Initialize Redis connection
- Prepare Jackson and Protobuf payloads
- Execute all benchmark scenarios

---

### 5.2 Codec Abstraction

```java
public interface Codec<T> {
    byte[] serialize(T data) throws Exception;
    T deserialize(byte[] bytes) throws Exception;
}
```

This abstraction decouples benchmark logic from encoding implementations and allows easy extension to other formats (Avro, Kryo).

---

### 5.3 JacksonCodec

Characteristics:
- UTF-8 JSON encoding
- Schema-less
- Human-readable

Trade-offs:
- Larger payload
- Higher parsing and GC cost

---

### 5.4 ProtobufCodec

Characteristics:
- Binary encoding
- Requires schema at runtime
- Strong contract enforcement

Trade-offs:
- Less flexible payload evolution
- Schema management overhead

---

## 6. Execution Flow

1. Application startup
2. Redis connection established
3. Payload loaded from disk
4. JSON converted to Protobuf (one-time)
5. In-memory benchmarks executed
6. Redis round-trip benchmarks executed
7. Results printed

---

## 7. Benchmark Results

### 7.1 In-memory Serialize / Deserialize

```
Jackson JSON
Payload size: 3068 bytes
Serialize: 6531 ms
Deserialize: 8969 ms

Protobuf Binary
Payload size: 3372 bytes
Serialize: 8615 ms
Deserialize: 16989 ms
```

Observations:
- Jackson is faster for pure in-memory operations
- Protobuf incurs higher CPU cost due to schema-based binary processing
- In-memory results alone do not reflect real message flow performance

---

### 7.2 Redis Round-trip (End-to-End)

```
Rounds: 100

Jackson JSON
Payload size: 3068 bytes
Total time: 85 ms
Avg per round: 0.85 ms

Protobuf Binary
Payload size: 3372 bytes
Total time: 31 ms
Avg per round: 0.31 ms
```

Observations:
- Protobuf is approximately **2.7× faster** end-to-end
- Network and I/O dominate performance characteristics
- Binary encoding significantly reduces round-trip latency despite higher CPU cost

---

## 8. Interpretation

### Why Jackson Wins In-Memory
- No schema enforcement
- Simpler data model
- Lower encoding overhead

### Why Protobuf Wins End-to-End
- Compact binary representation
- Faster transmission over network
- Reduced GC pressure during transport

This demonstrates that **network boundaries amplify the advantages of binary encoding**.

---

## 9. Limitations

- Redis is used instead of Kafka
- Single-threaded execution
- No batching
- No compression
- No schema evolution benchmark

These limitations are acceptable for **feasibility and architectural evaluation**, not throughput stress testing.

---

## 10. Conclusion

Although Jackson performs better in CPU-bound, in-memory scenarios, **Protobuf provides substantial gains in realistic, networked message flows**.

The benchmark confirms that:

> Performance improvements from binary encoding materialize primarily at the network boundary rather than during local serialization.

---
