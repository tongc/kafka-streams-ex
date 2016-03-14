# Exclamation

This is a trivial example (hello-world style) that uses Kafka streams to read a topic and append two exclamation points to the messages.
It's designed to read from the console producer and write to a topic with a console consumer listener.

## To Start

This use-case assumes Zookeeper and Kafka are running in local mode.
A pre-release version of Kafka is required since Kafka Streams haven't made it into an official release here.
Download this pre-released version [here](http://www.confluent.io/developer#streamspreview).

***There are plans for including scripts to launch these.***

You'll need to start up Zookeeper and Kafka from that directory before starting.

For Zookeeper (from the root of the install downloaded above):

```bash
bin/zookeeper-server-start etc/kafka/zookeeper.properties
```

For Kafka (from the root of the install):

```bash
bin/kafka-server-start etc/kafka/server.properties
```


When you've got Zookeeper and Kafka running, create the topics you'll need: "console" (input) and "exclamated" (real word).
From wherever the Kafka installation is (again, I'll add scripts for this very soon).

```bash
    kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partition 1 --topic console
    
    kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partition 1 --topic exclamated
```

Next you need to start the console producer so you can feed the input topic.

```bash
kafka-console-producer --broker-list localhost:9092 --topic console
```

Next fire up the consumer.

```bash
kafka-console-consumer --zookeeper localhost:2181 --topic exclamated
```

Finally, in the root of *this* project, fire up the streams.

```bash
mvn exec:java
```

If you can find the terminal with the console producer, you can start typing text to load onto the input topic.
If you can find the terminal with the console consumer, you can see those messages with more excitement.
