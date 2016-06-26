package io.github.timothyrenner.kstreamex.processor;

import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import io.github.timothyrenner.kstreamex.processor.Message;

/** Serde class for the Messages.
 *
 * @author Timothy Renner
 */
public class MessageSerde implements Serde<Message> {

	/** Serializer for the Message class. */
	private MessageSerializer serializer = new MessageSerializer();
	
	/** Deserializer for the Message class. */
	private MessageDeserializer deserializer = new MessageDeserializer();

	/** {@inheritDoc} */
	@Override
	public void close() {
		serializer.close();
		deserializer.close();
	} // Close close. 

	/** {@inheritDoc} */
	@Override
	public void configure(Map<String,?> configs, boolean isKey) {
		;
	} // Close configure.

	/** {@inheritDoc} */
	@Override
	public Serializer<Message> serializer() {
		return serializer;
	} // Close serializer.

	/** {@inheritDoc} */
	@Override
	public Deserializer<Message> deserializer() { 
		return deserializer;
	} // Close deserializer.

} // Close MessageSerde.
