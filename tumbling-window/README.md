# Tumbling Windows

This project demonstrates the behavior of tumbling windows, which are `TimeWindows` with non-overlapping window aggregations.
Tumbling windows are much simpler than hopping windows because once the window "fills up" a new one gets created while the old one is cleared.
It's a special case of hopping windows, where the `advanceBy` parameter is equal to the window size, which is why hopping and tumbling windows are both created from the `TimeWindows` class.

This example is an extremely simplistic demonstration of how tumbling windows work.
The input data is generated within the program via a Kafka producer (a very nice property of Kafka streams programs being simple Java applications), and simply writes a random Long to the stream with the key "A".
The output is the count of the elements in the windows.

I wanted to make this as simple as possible to make it explicitly clear how the windowing semantics work.
For tumbling windows (and this is true for hopping windows as well), records are emitted _every_ time new data arrives in the window, regardless of whether or not it is full.
This example will hopefully make this mechanism very plain to see.

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

When you've got Zookeeper and Kafka running, create the topics you'll need:
"longs" (input) and "long-counts-all" (output).
From the project root,

```bash
dev-resources/create_topics.sh
```

Next fire up the consumer.

```bash
dev-resources/start_consumer.sh
```

This consumer listens on the "long-counts-all" topic and outputs the grouped counts for each key in the input data set.
Since there's only one key, this is essentially counting the number of elements in the windows.
What you see with tumbling windows is the current count of elements in the window each time new data arrives, which is every half second.
I purposely made this simplistic example to demonstrate precisely what Kafka streams does for it's windows.

To see this in action, fire up the streams program.

```bash
mvn exec: java
```

