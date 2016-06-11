$KAFKA_HOME/bin/kafka-topics.sh --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic longs

$KAFKA_HOME/bin/kafka-topics.sh --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic long-counts-all
