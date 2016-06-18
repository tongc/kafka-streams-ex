package io.github.timothyrenner.kstreamex.hoppingwindow;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;
import java.util.Random;

public class HoppingWindowKafkaStream {
    
    public static void main(String[] args) throws Exception{
        
        Properties config = new Properties();
        
        config.put(StreamsConfig.APPLICATION_ID_CONFIG,
            "hopping-window-kafka-streams");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
		config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG,
			Serdes.ByteArray().getClass().getName());
		config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG,
			Serdes.String().getClass().getName());
        
        KStreamBuilder builder = new KStreamBuilder();
        
        KStream<byte[], Long> longs = builder.stream(
			Serdes.ByteArray(), Serdes.Long(), "longs");
        
        // The hopping windows will count the last second, two seconds,
        // three seconds, etc until the last ten seconds of data are in the
        // windows.
		KTable<Windowed<byte[]>, Long> longCounts = 
			longs.countByKey(TimeWindows.of("longCounts", 10000L)
										.advanceBy(1000L),
							Serdes.ByteArray());
										
        // Write to output topic.
        longCounts.toStream((k,v) -> k.key())
				  .map((k,v) -> KeyValue.pair(k, v))
				  .to(Serdes.ByteArray(),
				  	  Serdes.Long(),
					  "long-counts-all");
        
		// I am at a total loss as to how to select the "oldest" window.
		// Options are: aggregate to a tumbling window - bad idea, 
		//	latency could cause periodic misses.
		// groupby - can't use the key in the aggregation function?
        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();
        
        // Now generate the data and write to the topic.
        Properties producerConfig = new Properties();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer",
                           "org.apache.kafka.common" +
                           ".serialization.ByteArraySerializer");
        producerConfig.put("value.serializer",
                           "org.apache.kafka.common" +
                           ".serialization.LongSerializer");
        KafkaProducer producer = 
            new KafkaProducer<byte[], Long>(producerConfig);
        
        Random rng = new Random(12345L);
        
        while(true) {
            producer.send(new ProducerRecord<byte[], Long>(
                "longs", "A".getBytes(), rng.nextLong()%10));
            Thread.sleep(500L);
        } // Close infinite loop generating data.
    } // Close main.
} // Close HoppingWindowKafkaStream.
