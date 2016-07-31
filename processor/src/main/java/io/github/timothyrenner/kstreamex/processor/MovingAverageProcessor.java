package io.github.timothyrenner.kstreamex.processor;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import io.github.timothyrenner.kstreamex.processor.Message;

/** Processor that calculates and forwards the exponential moving average
 *  of a message.
 *  
 *  Downstream processors are `type` + "-sink", and state stores are 
 *  `type` + "-store", where `type` is the message type.
 *
 * @author Timothy Renner
 */
public class MovingAverageProcessor implements Processor<String, Message> {
    
    private ProcessorContext context;
    private KeyValueStore<String, Double> state;
    
    /** The type for the processor, used to retrieve the state store. */
    private String type;
    
    /** The alpha value for the moving average. */
    private double alpha;

    /** Set containing the available valid messages. */
    private static HashSet<String> VALID_TYPES = 
        new HashSet<String>(Arrays.asList("FAST", "MEDIUM", "SLOW"));

    /** Creates an instance of the processor for the specified message
     *  type with the provided alpha value.
     *
     * @param type The type of message. 
     *             Can only be "FAST", "MEDIUM", or "SLOW".
     * @param alpha The alpha value in the moving average equation.
     *              Must be between zero and one.
     *
     * @throws IllegalArgumentException If the type is invalid.
     * @throws IllegalArgumentException If alpha is not between zero and one.
     */
    public MovingAverageProcessor(String type, double alpha) {
        
        if(!VALID_TYPES.contains(type)) {
            throw new IllegalArgumentException(
                type + " is not a valid message type.\n" +
                "Valid message types are \"FAST\", \"MEDIUM\", and \"SLOW\".");
        } // Close if statement validating message type.

        if((alpha < 0.0) || (alpha > 1.0)) {
            throw new IllegalArgumentException(
                "Alpha must be between zero and one.");
        } // Close if statement validating alpha.

        this.type = type;
        this.alpha = alpha;
    } // Close constructor.

    /** Initializes the state store with the name `type` + "_store", where
     * `type` is the type specified in the constructor.
     *
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        
        this.context = context;

        // Schedules the `punctuate` method for every second.
        this.context.schedule(1000L);

        state = (KeyValueStore) context.getStateStore(type + "-store");
    
    } // Close init.

    /** Processes the incoming message, calculating the exponential moving 
     *  average of the value of the message and saving the value in the
     *  state store.
     *
     * {@inheritDoc}
     */
    @Override
    public void process(String key, Message value) {

        Double oldValue = state.get(key);
        
        // Verify that the value is present.
        if(oldValue == null) {
            oldValue = 0.0;
        }
        
        // Now calculate the new moving average.
        double newAvg = alpha * value.getValue() + 
                        (1 - alpha) * oldValue;

        // Update the state store.
        state.put(key, newAvg);


    } // Close process.

    /** Forwards the values in the state store downstream.
     *
     * {@inheritDoc}
     */
    @Override
    public void punctuate(long timestamp) {

        state.all().forEachRemaining(
            kv -> context.forward(kv.key, kv.value, type + "-sink"));
    
    } // Close punctuate.
    
    /** {@inheritDoc} */
    @Override
    public void close() {

        state.close();

    } // Close close.

} // Close MovingAverageProcessor.
