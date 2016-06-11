$KAFKA_HOME/bin/kafka-console-consumer.sh \
--zookeeper localhost:2181 \
--topic "long-counts-all" \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
