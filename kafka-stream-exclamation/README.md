# Exclamation

This is a trivial example (hello-world style) that uses Kafka streams to read a topic and append two exclamation points to the messages.
It's designed to read from the console producer and write to a topic with a console consumer listener.

## To Start

This use-case assumes Zookeeper and Kafka are running in local mode.
A pre-release version of Kafka is required since Kafka Streams haven't made it into an official release here.
Download this pre-released version [here](http://www.confluent.io/developer#streamspreview).

You'll need to start up Zookeeper and Kafka before starting.
First, set the `KAFKA_HOME` environment variable.

```bash
KAFKA_HOME=/path/to/kafka/installation #~/kafka/confluent-2.1.0-alpha1 on my system
```

The rest you can execute from the project root.
The scripts provided assume `KAFKA_HOME` points to a _vanilla_ local installation (as if you just untarred it).
If that doesn't apply you can modify the scripts or just launch from the Kafka installation with their instructions.
The scripts are just to save typing.

For Zookeeper,

```bash
dev-resources/start_zookeeper.sh
```

For Kafka,

```bash
dev-resources/start_kafka.sh
```

When you've got Zookeeper and Kafka running, create the topics you'll need: "console" (input) and "exclamated" (real word).
From the project root,

```bash
dev-resources/create_topics.sh
```

Next you need to start the console producer so you can feed the input topic.

```bash
dev-resources/start_console_producer.sh
```

Next fire up the consumer.

```bash
dev-resources/start_console_consumer.sh
```

Finally, fire up the streams.

```bash
mvn exec:java
```

If you can find the terminal with the console producer, you can start typing text to load onto the input topic.
If you can find the terminal with the console consumer, you can see those messages with more excitement.
