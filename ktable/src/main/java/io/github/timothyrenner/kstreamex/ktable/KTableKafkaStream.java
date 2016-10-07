package io.github.timothyrenner.kstreamex.ktable;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;
import java.util.Random;

/** Demonstrates a KTable by ... writing to it. Pretty simple.
 *
 * @author Timothy Renner
 */
public class KTableKafkaStream {

    /** Runs the streams program (which produces its own data), writing
     *  to the "longs-table", "longs-table-out", "longs-stream-out" topics.
     *
     * @param args Not used.
     */
    public static void main(String[] args) throws Exception {
        
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG,
            "ktable-kafka-stream");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");

        config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG,
            Serdes.String().getClass().getName());
        config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG,
            Serdes.Long().getClass().getName());

        KStreamBuilder builder = new KStreamBuilder();
        
        KTable<String, Long> longs_table = builder.table("longs-table");

        longs_table.to("longs-table-out");

        // Convert to a stream and output to see what happens.
        longs_table.toStream().to("longs-stream-out");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();

        Properties producerConfig = new Properties();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer", 
            "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("value.serializer",
            "org.apache.kafka.common.serialization.LongSerializer");

        KafkaProducer<String, Long> producer = 
            new KafkaProducer<String, Long>(producerConfig);

        Random rng = new Random(12345L);
        String[] keys = {"A"}; // Can change to make a more complicated example.
        Long[] values = {1L, 2L, 3L};

        try {

            while(true) {
                String key = keys[rng.nextInt(keys.length)];
                Long value = values[rng.nextInt(values.length)];
                producer.send(
                    new ProducerRecord<String, Long>("longs-table", 
                                                     key, 
                                                     value));
                Thread.sleep(1000L);
            } // Close while loop for generating the data. 
        
        } catch(InterruptedException e) {
            producer.close();
        } // Close try/catch around data production.
    } // Close main.
} // Close KTableKafkaStream.
