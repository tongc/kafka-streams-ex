package io.github.timothyrenner.kstreamex.notification;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.util.Properties;

import io.github.timothyrenner.kstreamex.notification.LogonGenerator;
import io.github.timothyrenner.kstreamex.notification.TickGenerator;

/** Implementation of Facebook notifications for when you're not looking at 
 *  Facebook.
 *
 *  http://www.theonion.com/article/
 *      new-facebook-notifications-alert-users-when-they-n-37795
 *
 * @author Timothy Renner
 */
public class NotLookingAtFacebook {

    /** Runs the streams program, writing to the "notifications" and 
     *  "metrics" topics.
     *
     * @param args Not used.
     */
    public static void main(String[] args) throws Exception {
        
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG,
            "not-looking-at-facebook");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");

        config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, 
            Serdes.String().getClass().getName());
        config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, 
            Serdes.String().getClass().getName());

        Properties producerConfig = new Properties();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("value.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");

        String[] users = {"Doyin", "George", "Mark"};
        
        // Build the topology.
        KStreamBuilder builder = new KStreamBuilder();

        KTable<String, String> logons = builder.table("logons");
        KStream<String, String> ticks = builder.stream("ticks");

        KStream<String, String> notifications = 
            ticks.leftJoin(logons, (nv, lv) -> new String[] {nv, lv})
                 // Filter out any nulls.
                 .filter((k,v) -> v[1] != null)
                 // Filter out anyone who's logged on.
                 .filter((k,v) -> v[1] != "LOGON")
                 // Now set the message.
                 .mapValues(v -> "You are not currently viewing Facebook.");

        // Implement the metrics.
        KTable<Windowed<String>, Long> notificationCounts = 
            notifications.countByKey(
                // Create a one minute window.
                TimeWindows.of("notificationCounts", 60000L)
                            // Hop by ten seconds.
                           .advanceBy(10000L)
                           // Don't hang on to old values.
                           .until(60000L));

        // Convert notificationCounts to a stream, extract the key (ignore
        // the embedded time information), and sink to the "metrics" topic.
        notificationCounts.toStream((k,v) -> k.key())
                          .to(Serdes.String(),
                              Serdes.Long(),
                              "metrics");

        // Nuisance delivered. You're welcome.
        notifications.to("notifications");

        // Start producing logon messages.
        new Thread(new LogonGenerator(users, producerConfig)).start();

        // Start producing notifications.
        new Thread(new TickGenerator(users, producerConfig)).start();

        // Start the streams.
        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();

    } // Close main.
} // Close StreamTableJoin.
