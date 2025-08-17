package io.github.asifimtiazj.ingest.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderIngestController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String ordersTopic;

    public OrderIngestController(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.topics.orders}") String ordersTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.ordersTopic = ordersTopic;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> ingest(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) throws JsonProcessingException {
        String key = (idempotencyKey != null && !idempotencyKey.isBlank())
                ? idempotencyKey
                : UUID.randomUUID().toString();

        String value = objectMapper.writeValueAsString(payload);

        kafkaTemplate.send(ordersTopic, key, value);

        return ResponseEntity.accepted().body(Map.of(
                "status", "enqueued",
                "key", key,
                "topic", ordersTopic
        ));
    }
}