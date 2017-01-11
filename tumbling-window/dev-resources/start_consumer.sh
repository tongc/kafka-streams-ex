$KAFKA_HOME/bin/kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic "long-counts-all" \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
