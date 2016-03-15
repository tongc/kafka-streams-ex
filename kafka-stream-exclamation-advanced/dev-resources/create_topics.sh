$KAFKA_HOME/bin/kafka-topics --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic console

$KAFKA_HOME/bin/kafka-topics --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic exclamated

$KAFKA_HOME/bin/kafka-topics --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic much-exclamated