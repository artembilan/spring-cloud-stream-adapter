package com.vine.commons.cloudstream.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vine.commons.cloudstream.MessageInfo;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.integration.config.IntegrationConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vrustia on 3/19/18.
 */
@Component
@IntegrationConverter
public class JsonUnmarshallConverter implements GenericConverter {

    private final ObjectMapper objectMapper;

    public JsonUnmarshallConverter(ObjectMapper integrationObjectMapper) {
        this.objectMapper = integrationObjectMapper;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        HashSet<ConvertiblePair> convertiblePairs = new HashSet<>();
        convertiblePairs.add(new ConvertiblePair(String.class, MessageInfo.class));
        return convertiblePairs;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        try {
            String jsonString = (String) source;
            return objectMapper.readValue(jsonString, targetType.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
