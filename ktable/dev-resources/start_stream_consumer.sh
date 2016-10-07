$KAFKA_HOME/bin/kafka-console-consumer.sh \
--zookeeper localhost:2181 \
--topic "longs-stream-out" \
--property print.key=TRUE \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer