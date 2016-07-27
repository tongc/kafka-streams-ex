package io.github.timothyrenner.kstreamex.notification;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/** Sends a message to all users every second.
 *  This class writes to the "notifications" topic.
 *
 * @author Timothy Renner
 */
public class TickGenerator implements Runnable {
    
    private String[] users;
    private KafkaProducer producer;

    private String message = "Tick";

    /** Builds an instance of this producer with the provided configuration.
     *
     * @param users The users in the "simulation."
     * @param producerConfig The configuration for the Kafka producer.
     */
    public TickGenerator(String[] users, Properties producerConfig) {
        
        this.users = users;

        producer = new KafkaProducer<String, String>(producerConfig);

    } // Close constructor.

    /** Broadcasts a message to all of the users. 
      * Writes to the "ticks" topic.
      */
    @Override
    public void run() {
        
        while(true) {
            
            for(String user: users) {
                
                ProducerRecord<String, String> record = 
                    new ProducerRecord<>("ticks", user, message);

                producer.send(record);
            } // Close for loop over users (user).

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                ;
            } // Close try/catch on Thread.sleep.
        } // Close data generating while loop.
    } // Close run.
} // Close NotificationGenerator.
