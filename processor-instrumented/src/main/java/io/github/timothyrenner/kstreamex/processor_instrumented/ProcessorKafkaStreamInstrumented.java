package io.github.timothyrenner.kstreamex.processor_instrumented;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.KafkaStreams;

import io.github.timothyrenner.kstreamex.processor_instrumented
    .MovingAverageProcessor;

/** Demonstrates the processor API with a topology that is not easily built
 *  with the high level DSL, with the state store instrumented to log to
 *  STDOUT.
 *  See the README for details on this sub-project.
 *
 * @author Timothy Renner
 */
public class ProcessorKafkaStreamInstrumented {

    /** Runs the streams program, writing to the "fast-avgs-instrumented", 
     * "medium-avgs-instrumented", and "slow-avgs-instrumented" topics.
     *
     * @param args Not used.
     */
	public static void main(String[] args) throws Exception { 
        
        // Configuration for Kafka Streams.
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG,
                   "processor-kafka-streams-instrumented");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                   "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG,
                   "localhost:2181");
        config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG,
                   Serdes.String().getClass().getName());
        config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG,
                   Serdes.Double().getClass().getName());

        // Start at latest message.
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        // Create the state stores. We need one for each of the
        // MessageProcessor's in the topology.
        StateStoreSupplier fastStore = 
            Stores.create("FAST-store")
                  .withStringKeys()
                  .withDoubleValues()
                  .inMemory()
                  .build();

        // Build the topology.
        TopologyBuilder builder = new TopologyBuilder();
        builder.addSource("messages-source",
                          Serdes.String().deserializer(),
                          Serdes.Double().deserializer(),
                          "messages-instrumented")
               .addProcessor("FAST-processor",
                             () -> new MovingAverageProcessor(0.1),
                             "messages-source")
               .addStateStore(fastStore, "FAST-processor")
               .addSink("FAST-sink", 
                        "fast-avgs-instrumented", 
                        Serdes.String().serializer(),
                        Serdes.Double().serializer(),
                        "FAST-processor");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();

	} // Close main.

} // Close ProcessorKafkaStream.
