package io.github.timothyrenner.kstreamex.processor;

import java.util.Map;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.Serializer;

import io.github.timothyrenner.kstreamex.processor.Message;

/** Serializer for the Message class.
 * 
 * @author Timothy Renner
 */
public class MessageSerializer implements Serializer<Message> {

    /** Serializer for the type field. */
    private StringSerializer type = new StringSerializer();

    /** Serializer for the value field. */
    private DoubleSerializer value = new DoubleSerializer();

    /** {@inheritDoc} */
    @Override
    public void close() {
        
        type.close();
        value.close();
    } // Close close.

    /** {@inheritDoc} */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        
        type.configure(configs, isKey);
        value.configure(configs, isKey);
    } // Close configure.

    /** {@inheritDoc} */
    @Override
    public byte[] serialize(String topic, Message data) {
        
        // There is a possible opportunity to optimize the allocations here 
        // since the "type" string is a particular length.
        byte[] serializedType = type.serialize(topic, data.getType());
        byte[] serializedValue = value.serialize(topic, data.getValue());

        byte[] serializedMessage = 
            new byte[serializedType.length + serializedValue.length];

        // We're going to serialize the value first since it's fixed length.
        System.arraycopy(serializedValue, 0, 
                         serializedMessage, 0,
                         serializedValue.length);
        
        System.arraycopy(serializedType, 0, 
                         serializedMessage, serializedValue.length,
                         serializedType.length);

        return serializedMessage;
    } // Close serialize.
} // Close MessageSerializer.
