package io.debezium.examples.aggregation.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.debezium.examples.aggregation.model.db.EventType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class JsonPojoDeserializer<T> implements Deserializer<T> {


    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);


    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        clazz = (Class<T>)props.get("serializedClass");
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {

        if (bytes == null)
            return null;

        T data;

        // try to directly deserialize expected class
        try {
            data = OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (IOException e1) {
                throw new SerializationException(e1);
        }

        return data;
    }

    @Override
    public void close() { }
}
