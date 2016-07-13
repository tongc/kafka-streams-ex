# Not Looking at Facebook

This is an example inspired by [this article](http://www.theonion.com/article/new-facebook-notifications-alert-users-when-they-n-37795).

![](images/not_looking_at_facebook.png)

The example reads the logon information into a KTable, and joins that with a KStream of ticks to write to the "notifications" topic to inform users they are not, in fact, viewing Facebook right now.
It also collects totally useful metrics on the number of notifications sent per user in the last minute.

## To Start

This use-case assumes Zookeeper and Kafka are running in local mode as launched from a vanilla Kafka installation, which can be downloaded [here](http://kafka.apache.org/downloads.html).
There are helper scripts in this project to start up Zookeeper and Kafka as well as the consumers.

You'll need to start up Zookeeper and Kafka before running the streams program.
First, set the `KAFKA_HOME` environment variable.

```bash
KAFKA_HOME=/path/to/kafka/installation #~/kafka/kafka_2.11-0.10.0.0 on my system.
```

The rest you can launch from the project root.
The provided scripts assume `KAFKA_HOME` points to a _vanilla_ local installation (as if you just untarred it).
If that doesn't apply you can modify the scripts or just launch from the Kafka installation with their instructions.

For Zookeeper,

```bash
dev-resources/start_zookeeper.sh
```

For Kafka,

```bash
dev-resources/start_kafka.sh
```

When you've got Zookeeper and Kafka running, create the topics you'll need: "logons" (input), "ticks" (input), "notifications" (output), and "metrics" (output).
From the project root,

```bash
dev-resources/create_topics.sh
```

Next, fire up the consumers.

Notification consumer:

```bash
dev-resources/start_notification_consumer.sh
```

Metric consumer:

```bash
dev-resources/start_metric_consumer.sh
```

There are also scripts for the input topics as well which I wrote because of a debugging ... situation.
They're in the `dev-resources/` directory with the others.

Finally, fire up the streams program and take a look at how helpful these notifications will be to people who aren't currently viewing Facebook.

```bash
mvn exec:java
```
