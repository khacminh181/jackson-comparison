package org.example.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.Codec;

public class JacksonCodec implements Codec<JsonNode> {

    private final ObjectMapper mapper = new ObjectMapper();

    public byte[] serialize(JsonNode data) throws Exception {
        return mapper.writeValueAsBytes(data);
    }

    public JsonNode deserialize(byte[] bytes) throws Exception {
        return mapper.readTree(bytes);
    }
}

