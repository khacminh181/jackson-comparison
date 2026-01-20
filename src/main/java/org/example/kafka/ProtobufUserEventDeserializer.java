package org.example.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.example.proto.UserEventProto;

public class ProtobufUserEventDeserializer
        implements Deserializer<UserEventProto.UserEvent> {

    @Override
    public UserEventProto.UserEvent deserialize(String topic, byte[] data) {
        try {
            return UserEventProto.UserEvent.parseFrom(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
