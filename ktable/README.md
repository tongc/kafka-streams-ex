# KTable

This is a trivial example (hello-world style) that demonstrates the fundamentals of a KTable.
It simply writes values to a KTable and streams that table back into a topic, with and without `.toStream()`.
I wrote this example because I honestly wasn't sure whether a KTable would output every value that hits it or just the last _different_ value for each key.
The answer, it turns out, is the former.
Calling `toStream()` on it doesn't make a difference when it goes directly to a topic - it's only for working with other operations.
Note that while this example does show there's no difference between a KStream and a KTable when writing to a topic, the two have vastly different behaviors when they actually ... you know ... do stuff.

## To Start

This use-case assumes Zookeeper and Kafka are running in local mode, with a vanilla Kafka 0.10.0.0 installation downloaded [here](http://kafka.apache.org/downloads.html).
There are helper scripts to get started.

To start Zookeeper and Kafka with the helper scripts, initialize the `KAFKA_HOME` environment variable.

```bash
KAFKA_HOME=/path/to/kafka/installation #~/kafka/kafka_2.11-0.10.0.0 on my system
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

When you've got Zookeeper and Kafka running, create the topics you'll need: "longs-table", "longs-table-out", and "longs-stream-out".
There's a helper script for that too.

```bash
dev-resources/create_topics.sh
```

Next you need to start the console consumers.
There are three - one for the input, one for the output direct from the KTable, and the other for the KTable with the `.toStream()` call.
You'll want these in separate terminals to watch the nothingness of this sample in its full glory.

```bash
# Input.
dev-resources/start_input_consumer.sh

 # Table direct output.
 dev-resources/start_table_consumer.sh

 # Table .toStream output.
 dev-resources/start_stream_consumer.sh
```

Finally, start the streams program with

```bash
mvn exec:java
```

Notice how they're all the same.
This means KTable may _store_ only the updates but it _streams_ everything.
Don't judge - it was kind of bothering me and now I know :).
