package io.github.timothyrenner.kstreamex.processor;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import io.github.timothyrenner.kstreamex.processor.Message;

/** Processor that selects a downstream processor based on the message type.
 *  Downstream processors are `type` + "-processor", where `type` is the
 *  message type.
 *
 * @author Timothy Renner
 */
public class SelectorProcessor implements Processor<String, Message> {

    private ProcessorContext context;

    /** {@inheritDoc} */
    @Override
    public void init(ProcessorContext context) {
        
        this.context = context;
    
    } // Close init.

    /** Processes the incoming message and forwards it to the appropriate
     *  downstream processor based on the type.
     *  The downstream processors are `type` + "-processor", where `type` is
     *  the message type.
     *
     * {@inheritDoc}
     */
    @Override
    public void process(String key, Message value) {

        switch(value.getType()) {
            case "FAST":
                context.forward(key, value, "FAST-processor");
                break; 
            case "MEDIUM": 
                context.forward(key, value, "MEDIUM-processor");
                break;
            case "SLOW":   
                context.forward(key, value, "SLOW-processor");
                break;
            default:
                throw new IllegalArgumentException("Invalid message type.");
        } // Close switch on type.

    } // Close process.

    /** {@inheritDoc} */
    @Override
    public void punctuate(long timestamp) {
    
    } // Close punctuate.
    
    /** {@inheritDoc} */
    @Override
    public void close() {

    } // Close close.

} // Close SelectorProcessor.
