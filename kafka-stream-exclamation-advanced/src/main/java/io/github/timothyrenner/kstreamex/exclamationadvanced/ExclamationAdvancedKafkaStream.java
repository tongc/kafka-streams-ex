package io.github.timothyrenner.kstreamex.exclamationadvanced;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/** An example class for running a KafkaStream topology.
 *  Designed to read from the console producer and append a random (ish) number
 *  of exclamation points to the input message. It writes all messages to one
 *  topic ("exclamated") and outputs the most exclamated (real word!) messages
 *  to the "most-exclamated" topic.
 * 
 * @author Timothy Renner
 */
public class ExclamationAdvancedKafkaStream {
    
    /** Random number generator for the exclamation points. */
    private static Random rng = new Random();
    
    /** Generates 1 or 2 exclamation points.
     * 
     * @returns A string containing 1 or 2 exclamation points.
     */
    private static String getExclamations() {
        
        // Thanks, StackOverflow 
        /* http://stackoverflow.com/questions/1235179/
           simple-way-to-repeat-a-string-in-java
        */
        
        return Stream.generate(() -> "!")
                     .limit(rng.nextInt(2) + 1)
                     .collect(joining());
    } // Close getExclamation.
    
    /** Connects the topic "console" to two topics, adds 2-4 exclamation points,
     *  writing all messages to the "exclamated" topic and the messages with
     *  four exclamation points to the "much-exclamated" topic.
     * 
     * @param args Not used. 
     */
    public static void main(String[] args) {
        
        // Configuration stuff.
        Properties config = new Properties();
        
        // For the cluster. Assumes everything is local.
        config.put(StreamsConfig.JOB_ID_CONFIG, 
            "exclamation-advanced-kafka-streams");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
        
        // Serde.
        config.put(StreamsConfig.KEY_SERIALIZER_CLASS_CONFIG,
            ByteArraySerializer.class);
        config.put(StreamsConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            ByteArrayDeserializer.class);
        config.put(StreamsConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        config.put(StreamsConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class);
        
        final Deserializer<byte[]> byteArrayDeserializer = 
            new ByteArrayDeserializer();
        final Deserializer<String> stringDeserializer = 
            new StringDeserializer();
        
        KStreamBuilder builder = new KStreamBuilder();
        
        // Read the stream from the topic into a KStream.
        KStream<byte[], String> text = builder.stream(
            byteArrayDeserializer,
            stringDeserializer,
            "console");
        
        // Apply the transformations.
        KStream<byte[], String> exclamation = 
            text.mapValues(x -> x + getExclamations())
                .mapValues(x -> x + getExclamations());
        
        KStream<byte[], String> muchExclamation = 
            exclamation.filter((k,v) -> v.endsWith("!!!!"));
        
        // Sink them both.
        exclamation.to("exclamated");
        muchExclamation.to("much-exclamated");
        
        // Build and run.
        KafkaStreams streams = new KafkaStreams(builder, config);
        
        streams.start();
        
    } // Close main
} // Close ExclamationAdvancedKafkaStream