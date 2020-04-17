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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/** Demonstrates hopping windows.
 *
 * @author Timothy Renner
 */
public class HoppingWindowKafkaStream {
    
    /** Runs the streams program, writing to the "long-counts-all" topic.
     *
     * @param args Not used.
     */
    public static void main(String[] args) throws Exception{
        
        Properties config = new Properties();
        
        config.put(StreamsConfig.APPLICATION_ID_CONFIG,
            "hopping-window-kafka-streams");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
        config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG,
            Serdes.String().getClass().getName());
        config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG,
            Serdes.String().getClass().getName());
        
        KStreamBuilder builder = new KStreamBuilder();
        
        KStream<String, Long> longs = builder.stream(
            Serdes.String(), Serdes.Long(), "longs");

        long windowSizeMs = TimeUnit.MINUTES.toMillis(2); // 5 * 60 * 1000L
        long advanceMs =    TimeUnit.SECONDS.toMillis(30); // 1 * 60 * 1000L

        // The hopping windows will count the last second, two seconds,
        // three seconds, etc until the last ten seconds of data are in the
        // windows.
        KTable<Windowed<String>, Long> longCounts =
            longs.groupByKey()
                  .count(TimeWindows.of(windowSizeMs)
                                    .advanceBy(advanceMs)
                                    .until(advanceMs),
                         "long-counts-str");
                                        
        // Write to output topic.
        longCounts.toStream((k,v) -> k.key())
                  .map((k,v) -> KeyValue.pair(k, v))
                  .to(Serdes.String(),
                      Serdes.Long(),
                      "long-counts-all-str");
        
        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();
        
        // Now generate the data and write to the topic.
        Properties producerConfig = new Properties();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer",
                           "org.apache.kafka.common" +
                           ".serialization.StringSerializer");
        producerConfig.put("value.serializer",
                           "org.apache.kafka.common" +
                           ".serialization.LongSerializer");
        KafkaProducer producer = 
            new KafkaProducer<String, Long>(producerConfig);
        
        Random rng = new Random(12345L);

        List<String> marketIds = new ArrayList<>();
        marketIds.add("marketId1");
        marketIds.add("marketId2");
        marketIds.add("marketId3");
        marketIds.add("marketId4");
        marketIds.add("marketId5");
        marketIds.add("marketId6");

        Random r = new Random(11111L);

        while(true) {
            int i = r.nextInt(3);
            producer.send(new ProducerRecord<String, Long>(
                "longs", marketIds.get(i), 1L));
            Thread.sleep(1000L);
        } // Close infinite loop generating data.
    } // Close main.
} // Close HoppingWindowKafkaStream.
