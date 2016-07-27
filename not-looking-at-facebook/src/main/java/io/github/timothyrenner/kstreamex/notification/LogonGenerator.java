package io.github.timothyrenner.kstreamex.notification;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/** Simulates logon events.
 *  This class writes to the "longs" topic in Kafka.
 *
 * @author Timothy Renner
 *
 */
public class LogonGenerator implements Runnable {

    private String[] users;
    private KafkaProducer producer;

    private String[] events = {"LOGON", "LOGOFF"};

    private HashMap<String, String> loggedOn = new HashMap<String, String>();
    
    /** Builds an instance of this producer with the provided configuration.
     *
     * @param users The users in the "simulation".
     * @param producerConfig The configuration for the Kafka producer.
     */
    public LogonGenerator(String[] users, Properties producerConfig) {
        
        this.users = users;

        producer = new KafkaProducer<String, String>(producerConfig);
    } // Close constructor.
    
    /** Simulates logon events. Writes to the "logons" topic. */
    @Override
    public void run() {
        
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        
        while(true) {
            
            // Select a user.
            String user = users[rng.nextInt(users.length)];

            // Select an event.
            String event = events[rng.nextInt(events.length)];

            // Check the state of the user.
            String userState = loggedOn.get(user);

            // Emit the event if it's a new state.
            if((userState == null) || (userState != event)) {

                // Update the state.
                loggedOn.put(user, event);
                
                ProducerRecord<String, String> record = 
                    new ProducerRecord<>("logons", user, event);

                producer.send(record);
            } // Close if statement on userState.

            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                ;
            } // Close try/catch on Thread.sleep.

        } // Close infinite loop.
    } // Close run.
} // Close LogonGenerator.
