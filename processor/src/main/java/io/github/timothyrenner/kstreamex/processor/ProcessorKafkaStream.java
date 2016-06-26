package io.github.timothyrenner.kstreamex.processor;

import java.util.Properties;
import java.util.Random;
import java.util.HashMap;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.KafkaStreams;

import io.github.timothyrenner.kstreamex.processor.Message;
import io.github.timothyrenner.kstreamex.processor.MessageSerde;
import io.github.timothyrenner.kstreamex.processor.SelectorProcessor;
import io.github.timothyrenner.kstreamex.processor.MovingAverageProcessor;

/** Demonstrates the processor API with a topology that is not easily built
 *  with the high level DSL.
 *  See the README for details on this sub-project.
 *
 * @author Timothy Renner
 */
public class ProcessorKafkaStream {

    /** Runs the streams program, writing to the "fast-avgs", "medium-avgs",
     *  and "slow-avgs" topics.
     *
     * @param args Not used.
     */
	public static void main(String[] args) throws Exception { 
        
        // Configuration for Kafka Streams.
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG,
                   "processor-kafka-streams");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                   "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG,
                   "localhost:2181");
        config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG,
                   Serdes.String().getClass().getName());
        config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG,
                   MessageSerde.class);

        // Create the state stores. We need one for each of the
        // MessageProcessor's in the topology.
        StateStoreSupplier fastStore = 
            Stores.create("FAST-store")
                  .withStringKeys()
                  .withDoubleValues()
                  .inMemory()
                  .build();
        
        StateStoreSupplier mediumStore = 
            Stores.create("MEDIUM-store")
                  .withStringKeys()
                  .withDoubleValues()
                  .inMemory()
                  .build();

        StateStoreSupplier slowStore = 
            Stores.create("SLOW-store")
                  .withStringKeys()
                  .withDoubleValues()
                  .inMemory()
                  .build();

        // Build the topology.
        TopologyBuilder builder = new TopologyBuilder();
        builder.addSource("messages-source",
                          Serdes.String().deserializer(),
                          new MessageSerde().deserializer(),
                          "messages")
               .addProcessor("selector-processor",
                             () -> new SelectorProcessor(),
                             "messages-source")
               .addProcessor("FAST-processor",
                             () -> new MovingAverageProcessor("FAST", 0.1),
                             "selector-processor")
               .addProcessor("MEDIUM-processor",
                             () -> new MovingAverageProcessor("MEDIUM", 0.05),
                             "selector-processor")
               .addProcessor("SLOW-processor",
                             () -> new MovingAverageProcessor("SLOW", 0.01),
                             "selector-processor")
               .addStateStore(fastStore, "FAST-processor")
               .addStateStore(mediumStore, "MEDIUM-processor")
               .addStateStore(slowStore, "SLOW-processor")
               .addSink("FAST-sink", 
                        "fast-avgs", 
                        Serdes.String().serializer(),
                        Serdes.Double().serializer(),
                        "FAST-processor")
               .addSink("MEDIUM-sink", 
                        "medium-avgs", 
                        Serdes.String().serializer(),
                        Serdes.Double().serializer(),
                        "MEDIUM-processor")
               .addSink("SLOW-sink", 
                        "slow-avgs", 
                        Serdes.String().serializer(),
                        Serdes.Double().serializer(),
                        "SLOW-processor");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();

        // Now generate the data and write to the input topic.
        Properties producerConfig = new Properties();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer",
                           "org.apache.kafka.common" +
                           ".serialization.StringSerializer");
        producerConfig.put("value.serializer",
                           "io.github.timothyrenner.kstreamex.processor" +
                           ".MessageSerializer");

        KafkaProducer producer = 
            new KafkaProducer<String, Message>(producerConfig);

        Random rng = new Random(12345L);

        // These will be the keys used in the stream.
        String[] keys = {"A", "B", "C"};
        String[] types = {"FAST", "MEDIUM", "SLOW"};
        
        // Initialize a hash map with the last value. We need this for the
        // random walk.
        HashMap<String, Double> lastValues = new HashMap<String, Double>();
        for(String key: keys) {
            lastValues.put(key, 0.0);
        } // Close for loop over keys (key).

        while(true) {
            
            // Select one of the keys at random.
            String key = keys[rng.nextInt(keys.length)];
            
            // Advance the random walk.
            double nextValue = lastValues.get(key) + rng.nextGaussian();
            
            // Bundle into message and send.
            Message value = new Message(types[rng.nextInt(types.length)],
                                        nextValue);

            producer.send(new ProducerRecord<>("messages", key, value));

            // Update the lastValues hash map.
            lastValues.put(key, nextValue);

            Thread.sleep(100L);

        } // Close infinite data generating loop.
	} // Close main.

} // Close ProcessorKafkaStream.
