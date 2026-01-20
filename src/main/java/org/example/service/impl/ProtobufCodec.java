package org.example.service.impl;

import com.google.protobuf.Message;
import org.example.service.Codec;
import com.google.protobuf.Parser;

public class ProtobufCodec implements Codec<Message> {
    Parser<? extends Message> parser;

    public ProtobufCodec (Message message){
        this.parser = message.getParserForType();
    }

    @Override
    public byte[] serialize(Message data) throws Exception {
        return data.toByteArray();
    }

    @Override
    public Message deserialize(byte[] bytes) throws Exception {
        return this.parser.parseFrom(bytes);
    }
}
