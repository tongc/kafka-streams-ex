package io.github.timothyrenner.kstreamex.windowdelay;

import org.apache.kafka.common.serialization.Serdes;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Random;
import java.util.Properties;

/** Demonstrates time-ordered windowing.
 *
 * @author TimothyRenner
 */
public class KafkaStreamWindowDelay {
    
    /** Runs the streams program, generating data onto the "timed-longs"
     *  topic and writing results to the "summed-longs" topic.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        
        /**** Streams configuration. ******/
        Properties streamsConfig = new Properties();

        streamsConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, 
            "window-delay");
        streamsConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, 
            "localhost:9092");
        streamsConfig.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, 
            "localhost:2181");

        streamsConfig.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG,
            Serdes.String().getClass().getName());
        streamsConfig.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG,
            Serdes.Long().getClass().getName());
        
        /***** Producer configuration. *****/
        Properties producerConfig = new Properties();

        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
            "localhost:9092");
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            Serdes.String().serializer().getClass().getName());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            Serdes.Long().serializer().getClass().getName());
        
        // Build the topology.
        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, Long> longs = builder.stream("timed-longs");

        // NOTE: For some odd reason there's a thirty second latency
        // when pushing these windows out to a topic. This might be
        // a mailing list item. It's exactly thirty seconds and doesn't
        // change when the window size or window wait period changes.
        KTable<Windowed<String>, Long> longSums = 
            longs.groupByKey().reduce(
                (v1, v2) -> v1 + v2,
                TimeWindows.of(10000L) // Ten seconds per window.
                           .until(60000L), // Save six windows.
                "longSums");
        
        // We're going to encode the window time stamp in the new message.
        // This is going to tell us what happens when we get a late message.
        longSums.toStream()
                .map((k,v) -> 
                    KeyValue.pair(k.key(), 
                                  Long.toString(k.window().start()) + 
                                  "\t" + v.toString()))
                .to(Serdes.String(),
                    Serdes.String(),
                    "summed-longs");

        // Start the streams job.
        KafkaStreams streams = new KafkaStreams(builder, streamsConfig);
        streams.start();

        // Set up the simulator.
        Random rng = new Random(12345L);
        String[] keys = {"A"}; // Can change for a more complicated example.
        
        // Now set up the producer.
        KafkaProducer<String, Long> producer = 
            new KafkaProducer<String, Long>(producerConfig);
        
        try {
            while(true) {
                String key = keys[rng.nextInt(keys.length)];
                Long value = 1L; // You can make this more complex if you want.
                Long time = System.currentTimeMillis();
                
                // In each ten second period (approximately), we'll generate a 
                // late timestamp. Otherwise we'll use the wall clock.
                if(Math.abs(rng.nextLong()) % 10 == 1) {
                    time -= (Math.abs(rng.nextLong()) % 60)*1000;
                } // Close if statement on late time stamp.

                System.out.println("key: " + key);
                System.out.println("time: " + time.toString());
                System.out.println(); 

                producer.send(
                    new ProducerRecord<String, Long>(
                        "timed-longs",
                        null, // auto-assign partition
                        time, // timestamp
                        key, 
                        value));

                // Throttle data generation at one message per second.
                Thread.sleep(1000L);
            } // Close while loop for data generation.
        } catch(InterruptedException e) { 
            System.err.println("Execution interrupted, closing producer.");
            producer.close();
        } // Close try/catch around the data production.

    } // Close main.
} // Close KafkaStreamWindowDelay.