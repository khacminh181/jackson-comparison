package org.example.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.model.UserEvent;

public class JacksonUserEventDeserializer implements Deserializer<UserEvent> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public UserEvent deserialize(String topic, byte[] data) {
        try {
            return mapper.readValue(data, UserEvent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
