# Instrumented Processor

This project demonstrates how the state stores in the Processor API migrate from failed nodes by logging the values in the state stores to STDOUT.
It's built from a modified version of the topology in the Processor API.

## Example Details

The input topic consists of `Messages`, which contain just a double value.
The messages are then sent to a `MovingAverageProcessor`, which computes the [exponential moving average](https://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average) of the values for each key.
This `MovingAverageProcessor` is slightly different from the one in the other Processor API example in that it doesn't take a type.
It also logs the values in the state store when the `init` and `punctuate` methods are called, making it easy to track the values in a multi-instance scenario.

## Running the Example

This example assumes Zookeeper and Kafka are running in local mode, with a vanilla Kafka 0.10.0.0 installation downloaded [here](http://kafka.apache.org/downloads.html).

You'll need to start up Zookeeper and Kafka before running the example.
There are helper scripts to do this.
To use them, set `KAFKA_HOME` environment variable.

```bash
KAFKA_HOME=/path/to/kafka/installation #~/kafka/kafka_2.11-0.10.0.0 on my system.
```

You can execute the startup scripts from the project root.
Start them in their own terminals or send them to the background.
The scripts provided assume `KAFKA_HOME` points to a _vanilla_ local installation (as if you just untarred it).
If you're using the Confluent Platform you'll need to modify the startup scripts or start Zookeeper and Kafka manually.

For Zookeeper,

```bash
dev-resources/start_zookeeper.sh
```

For Kafka,

```bash
dev-resources/start-kafka.sh
```

When you've got Zookeeper and Kafka running, create the topics you'll need: "messages-instrumented" and "fast-avgs-instrumented".
It's important to note the "messages-instrumented" topic has two partitions.
From the project root you can just execute

```bash
dev-resources/create_topics.sh
```

Next, start one or more processors, possibly piping the output to a log file to compare the values.

```bash
dev-resources/start_processor.sh | tee -a processor_1.txt
```

In a different terminal,

```bash
dev-resources/start_processor.sh | tee -a processor_2.txt
```

Finally, start the producer.

```bash
dev-resources/start_producer.sh
```

What you'll then want to do is kill one of the jobs and watch what happens to the state store in the other node.
You should (after a time) see it initialize.
Take a look at the log files and compare the last punctuated value from the killed process to the value that's initialized in the live node.
I haven't fully explored this scenario myself yet, so I honestly don't know what to expect.
