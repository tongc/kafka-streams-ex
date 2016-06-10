# Tumbling Windows

This project demonstrates the behavior of tumbling windows, which are `TimeWindows` with an additional `advance` parameter to determine the size of the "hops".
Tumbling windows produce multiple windows per `size` value which overlap, and when the latest window falls out of the windowed range a new empty window appears in the front.

This example is an extremely simplistic demonstration of how tumbling windows work.
The input data is generated within the program via a Kafka producer (a very nice property of Kafka streams programs being simple Java applications), and simply writes a random Long to the stream with the key, "A".
The output is simply the counts of the elements in each of the windows.

I wanted to make this as simple as possible because Kafka streams does some different things with it's window semantics.
For one thing, _all_ of the windows within the hopping window period are emitted.
Moreover, they're emitted (since Kafka Streams is a record-at-a-time stream processor) every time new data arrives into the windows.
This example will hopefully make this mechanism very plain to see.

## To Start

This use-case assumes Zookeeper and Kafka are running in local mode,
with a vanilla Kafka 0.10.0.0 installation downloaded [here](http://kafka.apache.org/downloads.html).

You'll need to start up Zookeeper and Kafka before starting.
First, set the `KAFKA_HOME` environment variable.

```bash
KAFKA_HOME=/path/to/kafka/installation #~/kafka/kafka_2.11-0.10.0.0 on my system.
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
Since there's only one key, this is essentially counting the number of elements in each of the windows.
What you see with tumbling windows is _all_ of window's counts in the window size range as a separate record, emitted _every time new data enters the KTable_, which is every half second.
I purposely made this simplistic example to demonstrate precisely what Kafka streams does for it's windows.

To see this in action, fire up the streams program.

```bash
mvn exec:java
```

And watch the consumer to see what the windows do.
