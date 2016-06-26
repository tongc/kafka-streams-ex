package io.github.timothyrenner.kstreamex.processor;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import io.github.timothyrenner.kstreamex.processor.MessageSerializer;
import io.github.timothyrenner.kstreamex.processor.MessageDeserializer;
import io.github.timothyrenner.kstreamex.processor.Message;

import static org.junit.Assert.assertEquals;

/** Unit tests the MessageSerializer class.
 * 
 * @author Timothy Renner
 */
public class MessageSerializer_UnitTest {
    
    private MessageSerializer serializer;
    private MessageDeserializer deserializer;

    /** Initializes the helpers.
     */
    @Before
    public void setUp() {
        
        serializer = new MessageSerializer();
        deserializer = new MessageDeserializer();

    } // Close setUp.

    /** Tears down after the tests.
     */
    @After
    public void tearDown() {

        serializer = null;
        deserializer = null;

    } // Close tearDown.

    /** Tests that the serialize method functions correctly.
     */
    @Test
    public void serialize_test1() { 
        
        String topic = "why-is-this-an-arg";
        Message data = new Message("FAST", 0.1);

        // Serialize the message.
        byte[] serializedMessage = serializer.serialize(topic, data);

        // Use the deserializer to deserialize the message.
        Message deserializedMessage = 
            deserializer.deserialize(topic, serializedMessage);

        // Assert that the serialization was correct.
        assertEquals(deserializedMessage.getType(), data.getType());
        assertEquals(deserializedMessage.getValue(), data.getValue(), 1e-14);
    } // Close serialize_test1.

} // Close MessageSerializer_UnitTest.
