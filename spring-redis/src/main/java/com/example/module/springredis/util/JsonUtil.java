package com.example.module.springredis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public String objectToString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    public <T> T stringToObject(String string, Class<T> clazz) {
        return objectMapper.readValue(string, clazz);
    }

    public <T> List<T> stringToList(String string, Class<T> clazz) {
        JavaType javaType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, clazz);

        return objectMapper.readValue(string, javaType);
    }

    public <K, V> Map<K, V> stringToMap(String string, Class<K> keyClass, Class<V> valueClass) {
        JavaType javaType = objectMapper.getTypeFactory()
                .constructMapType(HashMap.class, keyClass, valueClass);

        return objectMapper.readValue(string, javaType);
    }
}
