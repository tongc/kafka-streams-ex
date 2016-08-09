$KAFKA_HOME/bin/kafka-topics.sh --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 2 \
--topic messages-instrumented

$KAFKA_HOME/bin/kafka-topics.sh --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic fast-avgs-instrumented

