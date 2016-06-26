$KAFKA_HOME/bin/kafka-topics.sh --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic messages

$KAFKA_HOME/bin/kafka-topics.sh --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic fast-avgs

$KAFKA_HOME/bin/kafka-topics.sh --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic medium-avgs

$KAFKA_HOME/bin/kafka-topics.sh --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partition 1 \
--topic slow-avgs
