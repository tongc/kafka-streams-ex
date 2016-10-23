# Windowed Delay

This example demonstrates Kafka's and Kafka Streams's time ordering capabilities.
It writes messages, all with the same key and value 1 to a topic, but randomly drops the timestamp back before pushing the message onto Kafka.
These messages are aggregated in a tumbling window and the values are summed (which is basically a count since the values are 1).
The KTable is converted to a KStream and the window start time is embedded in the message so we can get the counts that fell within each window.
This will tease out the time ordering behavior.
This one gave some unexpected results so I'll go ahead and document what I expected vs what I got here.

### What I Expected

Since the windows in Kafka Streams emit a record each time they receive an updated value, I expected to see a message on the output topic every time there was a message written to the input, with the counts changing within the window the timestamp of the input message fell in.
So if my current window count was 6 and my previous window count was 4, if I put a message with an un-altered timestamp on the input topic I'd expect to see a 7, whereas if I put a message with an altered timestamp I'd expect a 5.

```
timestamp: 20000, value: 6
timestamp: 10000, value: 4

# Each output should be printed immediately, based on the behavior
# of the previous Tumbling Window and Hopping Window examples.
Incoming message: "A", 1, 22000 -> timestamp: 20000, value: 7
Incoming message: "A", 1, 12000 -> timestamp: 10000, value: 5
``` 

### What I Got

What I actually saw was a huge latency in the output topic: all of the windows in the table were printed in the console consumer every 30s, regardless of the length of the window.
The fact that this was so consistent makes me believe there's a configuration option somewhere, but I have yet to find it.

## To Start

This use-case assumes Zookeeper and Kafka are running in local mode, with a vanilla Kafka 0.10.1.0 installation downloaded [here](http://kafka.apache.org/downloads.html).
There are helper scripts to get started.

To start Zookeeper and Kafka with the helper scripts, initialize the `KAFKA_HOME` environment variable.

```bash
KAFKA_HOME=/path/to/kafka/installation #~/kafka/kafka_2.11-0.10.1.0 on my system.
```

Once that variable is set you can launch Kafka and Zookeeper with the helper scripts from the project root.
The scripts do assume you're pointing `KAFKA_HOME` to a vanilla local installation (as if you just untarred it).
If you're using a different installation, such as the one bundled in the Confluent Platform, you'll need to modify the scripts or launch this stuff manually.

For Zookeeper,

```bash
dev-resources/start_zookeeper.sh
```

For Kafka,

```bash
dev-resources/start_kafka.sh
```

When you've got Zookeeper and Kafka running, create the topics you'l need: "timed-longs" and "summed-longs".
There's a helper script for that too.

```bash
dev-resources/create_topics.sh
```

Next you need to start the console consumer.

```bash
dev-resources/start_consumer.sh
```

Finally, start the streams program with

```bash
mvn exec:java
```