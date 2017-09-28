package com.intelligrated.generic.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * This is a Jackson Helper class to assist in the mapping of Hibernate entities
 * to JSON
 *
 * @author Christopher.eichel
 */

public class HibernateAwareObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = -1544788730210065088L;

    public HibernateAwareObjectMapper() {

        setVisibility(getSerializationConfig().getDefaultVisibilityChecker()
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE).withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

        registerModule(new Hibernate5Module().enable(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS));

        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {

            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                return LocalDateTime.ofInstant(Instant.ofEpochSecond(p.getValueAsLong()), ZoneId.systemDefault());
            }
        });
        timeModule.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {

            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException,
                    JsonProcessingException {
                gen.writeString(Long.toString(value.atZone(ZoneId.systemDefault()).toEpochSecond()));
            }

        });

        registerModule(timeModule);


        disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Adding a custom deserializer for Strings which will convert all empty strings "" into null values
        SimpleModule module = new SimpleModule().addDeserializer(String.class, new StdDeserializer<String>(String.class) {

            private static final long serialVersionUID = -1634980968665757066L;

            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                String result = StringDeserializer.instance.deserialize(p, ctxt);
                if (StringUtils.isEmpty(result)) {
                    return null;
                }
                return result;
            }
        });
        registerModule(module);

    }
}