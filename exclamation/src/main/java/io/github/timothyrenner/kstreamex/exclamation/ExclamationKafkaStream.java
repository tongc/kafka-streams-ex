package io.github.timothyrenner.kstreamex.exclamation;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;

/** An example class for running a KafkaStream topology.
 *  Designed to read from the console producer and write the message to a
 *  topic with two exclamation points appended.
 * 
 * @author Timothy Renner
 */
public class ExclamationKafkaStream {
    /** Connects the topic "console" with the topic "exclaimed", adding two
     *  exclamation points to the input values.
     * 
     * @param args Not used.
     */
    public static void main(String[] args) {
        
        // Configuration stuff.
        Properties config = new Properties();
       
        // For the cluster. Assumes everything is local.
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, 
			"exclamation-kafka-streams");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
        
        // Serde.
		config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG,
			Serdes.ByteArray().getClass().getName());
		config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG,
			Serdes.String().getClass().getName());
        
        KStreamBuilder builder = new KStreamBuilder();
        
        // Read the stream from the topic into a KStream.
        KStream<byte[], String> text = builder.stream("console");
        
        // Apply the transformation.
        KStream<byte[], String> exclamation = 
            text.mapValues(x -> x + "!")
                .mapValues(x -> x + "!");
        
        // Sink it. Uses the configured serializers.
        exclamation.to("exclamated");
        
        // Build and run.
        KafkaStreams streams = new KafkaStreams(builder, config);
        
        streams.start();
    } // Close main.
} // Close ExclamationKafkaStream.
