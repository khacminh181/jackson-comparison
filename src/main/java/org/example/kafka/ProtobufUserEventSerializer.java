package org.example.kafka;

import org.apache.kafka.common.serialization.Serializer;
import org.example.proto.UserEventProto.UserEvent;

public class ProtobufUserEventSerializer implements Serializer<UserEvent> {

    @Override
    public byte[] serialize(String topic, UserEvent data) {
        return data.toByteArray();
    }
}
