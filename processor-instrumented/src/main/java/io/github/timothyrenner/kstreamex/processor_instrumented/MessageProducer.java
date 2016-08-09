package io.github.timothyrenner.kstreamex.processor_instrumented;

import java.util.Properties;
import java.util.Random;
import java.util.HashMap;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.Serdes;

public class MessageProducer {

    public static void main(String[] args) throws Exception {
        
        Properties producerConfig = new Properties();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer",
                           "org.apache.kafka.common" +
                           ".serialization.StringSerializer");
        producerConfig.put("value.serializer",
                           "org.apache.kafka.common" +
                           ".serialization.DoubleSerializer");

        KafkaProducer producer = 
            new KafkaProducer<String, Double>(producerConfig);

        Random rng = new Random(12345L);

        // These will be the keys used in the stream.
        String[] keys = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
                         "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                         "S", "T", "U", "V", "W", "X", "Y", "Z"};
        
        // Initialize a hash map with the last value. We need this for the
        // random walk.
        HashMap<String, Double> lastValues = new HashMap<String, Double>();
        for(String key: keys) {
            lastValues.put(key, 0.0);
        } // Close for loop over keys (key).

        System.out.println("Starting data generation.");

        while(true) {
            
            // Select one of the keys at random.
            String key = keys[rng.nextInt(keys.length)];
            
            // Advance the random walk.
            double nextValue = lastValues.get(key) + rng.nextGaussian();
            

            producer.send(new ProducerRecord<>("messages-instrumented", 
                key, nextValue));

            // Update the lastValues hash map.
            lastValues.put(key, nextValue);

            Thread.sleep(100L);

        } // Close infinite data generating loop.
    } // Close main.
} // Close MessageProducer.
