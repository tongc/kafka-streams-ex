package io.github.timothyrenner.kstreamex.hoppingwindow;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Collections;
import java.util.Properties;

/**
 * Demonstrates hopping windows.
 *
 * @author Timothy Renner
 */
public class HoppingWindowKafkaStreamConsumer {

    /**
     * Runs the streams program, writing to the "long-counts-all" topic.
     *
     * @param args Not used.
     */
    public static void main(String[] args) throws Exception {

        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG,
                "hopping-window-kafka-streams");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,
                "tongConsumer");

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());

        final KafkaConsumer consumer = new KafkaConsumer<String, Long>(config);
        consumer.subscribe(Collections.singletonList("long-counts-all-str"));

        while (true) {
            final ConsumerRecords<String, Long> consumerRecords = consumer.poll(100);

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
            });

            consumer.commitAsync();
        }
    } // Close main.
} // Close HoppingWindowKafkaStream.
