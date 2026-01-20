package org.example.service;

public interface Codec<T> {
    byte[] serialize(T data) throws Exception;
    T deserialize(byte[] bytes) throws Exception;
}
