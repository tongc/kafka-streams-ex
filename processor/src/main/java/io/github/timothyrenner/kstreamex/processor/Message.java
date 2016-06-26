package io.github.timothyrenner.kstreamex.processor;

import java.util.HashSet;
import java.util.Arrays;

/** Simple class containing a "type" for the message and a 
 * 
 * @author Timothy Renner
 */
public class Message {
	
	/** The type of message.
	 *  Can only be "FAST", "MEDIUM", or "SLOW"
	 */
	private String type;

	/** The value for the message, used in the moving average. */
	private double value;

	/** Set containing the available valid messages. */
	private static HashSet<String> VALID_TYPES = 
		new HashSet<String>(Arrays.asList("FAST", "MEDIUM", "SLOW"));

	/** Creates a new message from the type and the value.
	 * 
	 * @param type The type of message. Can be \"FAST\", \"MEDIUM\", 
	 * 	or \"SLOW\".
	 * @param value The value for the message.
	 *
	 * @throws IllegalArgumentException If the type is not \"FAST\",
	 * 	\"MEDIUM\", or \"SLOW\".
	 */
	public Message(String type, double value) {
		
		// Validate the message type.
		if(!VALID_TYPES.contains(type)) {
			throw new IllegalArgumentException(
				type + " is not a valid message type.\n" + 
				"Valid message types are \"FAST\", \"MEDIUM\", and \"SLOW\".");
		} // Close if statement validating message type.

		this.type = type;
		this.value = value;
	
	} // Close constructor.

	/** Returns the type. */
	public String getType() { return type; }

	/** Returns the value. */
	public double getValue()  { return value; }
} // Close Message.
