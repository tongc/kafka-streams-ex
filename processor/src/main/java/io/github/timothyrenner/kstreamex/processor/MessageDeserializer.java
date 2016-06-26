package io.github.timothyrenner.kstreamex.processor;

import java.util.Map;
import java.util.Arrays;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.DoubleDeserializer;
import org.apache.kafka.common.serialization.Deserializer;

import io.github.timothyrenner.kstreamex.processor.Message;

/** Deserializer for the Message class.
 * 
 * @author Timothy Renner
 */
public class MessageDeserializer implements Deserializer<Message> {

	/** Deserializer for the type field. */
	private StringDeserializer type = new StringDeserializer();

	/** Deserializer for the value field. */
	private DoubleDeserializer value = new DoubleDeserializer();

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
	public Message deserialize(String topic, byte[] data) {
		
		// Remember we serialized the value first since it's fixed length.
		double deserializedValue = value.deserialize(topic, 
			Arrays.copyOfRange(data, 0, Double.BYTES));
		String deserializedType = type.deserialize(topic, 
			Arrays.copyOfRange(data, Double.BYTES, data.length));

		return new Message(deserializedType, deserializedValue);
	} // Close deserialize.
} // Close MessageDeserializer.
