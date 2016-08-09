package io.github.timothyrenner.kstreamex.processor_instrumented;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

/** Processor that calculates and forwards the exponential moving average
 *  of a message.
 *  The state (current moving averages for each key) are checkpointed to a
 *  state store every one second.
 *  Downstream processor is "FAST-avgs", state store is "FAST-store".
 *
 * @author Timothy Renner
 */
public class MovingAverageProcessor implements Processor<String, Double> {
    
    private ProcessorContext context;
    private KeyValueStore<String, Double> state;
    
    /** The alpha value for the moving average. */
    private double alpha;

    /** Creates an instance of the processor with the provided alpha value.
     *
     * @param alpha The alpha value in the moving average equation.
     *              Must be between zero and one.
     *
     * @throws IllegalArgumentException If alpha is not between zero and one.
     */
    public MovingAverageProcessor(double alpha) {

        if((alpha < 0.0) || (alpha > 1.0)) {
            throw new IllegalArgumentException(
                "Alpha must be between zero and one.");
        } // Close if statement validating alpha.

        this.alpha = alpha;
    } // Close constructor.

    /** Initializes the state store with the name "FAST-store", where
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

        state = (KeyValueStore) context.getStateStore("FAST-store");

        // Print the contents of the state store.
        state.all().forEachRemaining( 
            kv -> System.out.println(System.currentTimeMillis() + 
                                     " INIT: " + kv.key + 
                                     ", " + kv.value));
    
    } // Close init.

    /** Processes the incoming message, calculating the exponential moving 
     *  average of the value of the message.
     *  The value of the average is checkpointed to the state store with each
     *  message. The message is forwarded to the sink "fast-sink".
     *
     * {@inheritDoc}
     */
    @Override
    public void process(String key, Double value) {

        // If it's in the state store, use that value. Otherwise, use 0.0.
        // This is particularly important when restoring from a failover.
        Double oldValue = state.get(key);
        
        if(oldValue == null) {
            oldValue = 0.0;
        }

        // Now calculate the new moving average.
        double newAvg = alpha * value + 
                        (1 - alpha) * oldValue;

        // Update the state store.
        state.put(key, newAvg);

    } // Close process.

    /** Saves the `lastAvgs` map to the state store.
     *
     * {@inheritDoc}
     */
    @Override
    public void punctuate(long timestamp) {

        // Print the contents of the state store.
        state.all().forEachRemaining( 
            kv -> System.out.println(System.currentTimeMillis() + 
                                     " PUNCTUATE: " + kv.key + 
                                     ", " + kv.value));
        // Foward the new average.
        state.all().forEachRemaining(
            kv -> context.forward(kv.key, kv.value, "FAST-sink"));
    
    } // Close punctuate.
    
    /** {@inheritDoc} */
    @Override
    public void close() {

        state.close();

    } // Close close.

} // Close MovingAverageProcessor.
