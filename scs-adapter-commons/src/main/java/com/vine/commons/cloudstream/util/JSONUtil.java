package com.vine.commons.cloudstream.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.io.IOException;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 *
 * @author vrustia - 3/13/15.
 */
public final class JSONUtil {

    private static final ObjectMapper jsonMapper = configureResponseSettings(new ObjectMapper());

    private JSONUtil() {
    }

    public static ObjectMapper configureResponseSettings(ObjectMapper objectMapper) {

        objectMapper.registerModule(new JodaModule())
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    public static ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    public static <Z, T, K> Z convert(String jsonString, Class<T> rootClazz,
                                      Class<K> parameterClazz) {
        try {
            return jsonMapper.readValue(jsonString, jsonMapper.getTypeFactory().constructParametricType(rootClazz, parameterClazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convert(String jsonString, Class<T> rootClazz) {
        try {
            return jsonMapper.readValue(jsonString, rootClazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String stringify(Object object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            //FIXME refactor OwensBaseException from error module to a more common module
            throw new RuntimeException(e);
        }
    }
}
