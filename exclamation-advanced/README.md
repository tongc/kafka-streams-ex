# Exclamation

This is a slightly more complicated version of the exclamation stream example that appends a random (ish) number of exclamation points to an input string and splits the stream into a topic that receives all messages, and another that only receives messages with four (max) exclamation points.
It's designed to read from the console producer and write to a topic with a console consumer listener.

## To Start

This use-case assumes Zookeeper and Kafka are running in local mode,
with a vanilla Kafka 0.10.1.1 installation downloaded [here](http://kafka.apache.org/downloads.html).

You'll need to start up Zookeeper and Kafka before starting.
First, set the `KAFKA_HOME` environment variable.

```bash
KAFKA_HOME=/path/to/kafka/installation #~/kafka/kafka_2.11-0.10.1.1 on my system.
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
This consumer listens on the "alert" topic, not the one that receives all of the messages.
You can launch a consumer on the other manually or copy/modify this script to listen to the "exclamated" topic.

Finally, fire up the streams.

```bash
mvn exec:java
```

If you can find the terminal with the console producer, you can start typing text to load onto the input topic.
If you can find the terminal with the console consumer, you can see only those messages with the most excitement.
