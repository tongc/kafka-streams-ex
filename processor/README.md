# Processor API

This project demonstrates several aspects of the processor API.
The low-level processor API is very different from the high-level DSL, and requires substantially more code to implement.
The tradeoff is that it's significantly more flexible, allowing for optimizations and processing steps that are either very difficult or not possible with the high-level DSL.
This example also demonstrates custom serializers and state stores.

## Example Details

The example is contrived, but it is (hopefully) easy to understand.
The input topic consists of `Messages` (yes, that is literally the name of the class ... sometimes I'm not creative), which contain a type and a value.
The type of message can be either `FAST`, `MEDIUM`, or `SLOW` and contains a double value with it.
Firstly, all messages go through a `SelectorProcessor`, which reads from the "messages" topic and forwards the message to one of three downstream processors based on the message type.
The downstream processors from this node compute the [exponential moving average](https://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average) of the values.
There is one downstream processor for each type, each with a different alpha parameter for the moving average.
Each processor then writes the corresponding moving average to the topics "fast-avgs", "medium-avgs", and "slow-avgs".

There are two things this topology does that the hig-level DSL cannot easily do.
The first is a multi-way selection for downstream processors.
It's possible to emulate this one with `filter`s in the high-level DSL, but it's pretty cumbersome to push the predicates to the receivers.
Probably not as cumbersome as using the low-level API, but I did mention my creativity.
The other thing this topology does that the high-level DSL can't do is the exponential moving average.
This involves maintaining custom state - any state not in a `Window` or `Table` can't be done with the high-level DSL, but in the processor API anything can be put in the state store.
For this example, it's the last moving average value (for each key) that was calculated.
These moving averages are substantially more efficient than a windowed calculation, and they are _independent_ of time - they only depend on the _order_ of the elements received, not the time.
Depending on the use case, this may or may not be the desired behavior.

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

When you've got Zookeeper and Kafka running, create the topics you'll need: "messages", "fast-avgs", "medium-avgs", and "slow-avgs".
From the project root you can just execute

```bash
dev-resources/create_topics.sh
```

Finally, fire up the consumer.
The output's going on the "fast-avgs", "medium-avgs", and "slow-avgs" topics.
You can listen to any or all of them via the console consumer.
There's a startup script pre-loaded for the "fast-avgs" topic, but it's pretty easy to modify, as the data types are the same for all output topics.

```bash
dev-resources/start_consumer.sh
```

This consumer listens to the output topic, which prints either the fast, medium, or slow moving averages (fast is a large alpha, slow is a small alpha) depending on which topic is being listened to.
This example generates its own data, which is a random walk of three keys: "A", "B" and "C".
Told you I was creative.

## Deeper Details

This example is pretty involved.
In addition to the processor API itself, I also wanted to show custom serialization and a custom state store.

### State Stores and Punctuation

The `MovingAverageProcessor` class uses a state store to hold the last moving average values calculated for each node.
The values are held in a local `HashMap` instance (which is their primary interaction point in the `process` method), which is copied to the state store every one second.
The scheduling is done in the `init` method via `context.schedule( ... )`, and the action itself is defined in the `punctuate` method.
This does mean we won't be _exactly_ up to date with if we restart from a failover (we could lose at most a second's worth of updated averages for each key), but the state store interaction is _not_ in the `process` method, which gets called for every message received.

I don't know how fast interacting with the state store is.
It might be unnecessarily complex to have a local member _and_ a state store interaction; the state store may be fast enough.

It's also worth noting that even though the topology definition in the `ProcessorKafkaStream` class specifies an in memory state store, it's still durable.
State stores are backed by an internal Kafka topic that stores the changelog, so it can be restored on failover.

### Custom Serializer

Finally, I use a custom class with it's own serializer for the primary input message, which is a `String` and a `Double`.
This is probably not a great idea for an "external"-facing topic, as it means everyone has to use the same deserialization, which is not at all robust to changing the schema of the messages.
I did it in this example because I wanted to see what it was like.
I think custom serializer/deserializers have a place when throwing messages through internal topics to the job; these aren't "external" topics and the serialization can be blazing fast by keeping tight control over the schema.
Since the only consumer of these internal topics is the streaming job itself, there's no risk of upsetting your coworkers by changing the schema and breaking their deserializers.
