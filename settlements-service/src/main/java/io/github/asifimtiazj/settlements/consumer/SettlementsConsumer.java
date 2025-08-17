package io.github.asifimtiazj.settlements.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Configuration
@EnableKafka
@Component
public class SettlementsConsumer {

    private static final Logger log = LoggerFactory.getLogger(SettlementsConsumer.class);

    @KafkaListener(
            topics = "${app.topics.orders}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(
            String payload,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            ConsumerRecord<String, String> record
    ) {
        log.info("settlements-consumer received: key={}, payload={}, partition={}, offset={}",
                key, payload, record.partition(), record.offset());
        // TODO: settlements logic, persist, notify
    }
}