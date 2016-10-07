# Kafka Streams Examples

This repository contains examples of use cases (ranging from trivial to somewhat complex) of Kafka Streams.

Each example is in it's own directory.
The repository contains the following examples:

* [Exclamation](https://github.com/timothyrenner/kafka-streams-ex/tree/master/exclamation): Trivial example that reads from the console consumer and appends two exclamation points.
* [Exclamation Advanced](https://github.com/timothyrenner/kafka-streams-ex/tree/master/exclamation-advanced): Slightly more complicated version of Exclamation that "alerts" on highly exclamated messages.
* [Hopping Windows](https://github.com/timothyrenner/kafka-streams-ex/tree/master/hopping-window): Example demonstrating the behavior of hopping windows by counting the elements on a single key.
* [Tumbling Windows](https://github.com/timothyrenner/kafka-streams-ex/tree/master/tumbling-window): Example demonstrating the behavior of tumbling windows by counting the elements on a single key.
* [Processor](https://github.com/timothyrenner/kafka-streams-ex/tree/master/processor): Example demonstrating the processor API, state stores, and custom serializers.
* [Instrumented Processor](https://github.com/timothyrenner/kafka-streams-ex/tree/master/processor-instrumented): A stripped down version of the processor example that logs the values in the state store - designed to run in two nodes (or just two terminals) to show what happens under failover conditions.
* [Not Looking at Facebook](https://github.com/timothyrenner/kafka-streams-ex/tree/master/not-looking-at-facebook): Implementation of a streaming pipeline for notifying users when they aren't looking at Facebook.
* [KTable](https://github.com/timothyrenner/kafka-streams-ex/tree/master/ktable): Literally a KTable.