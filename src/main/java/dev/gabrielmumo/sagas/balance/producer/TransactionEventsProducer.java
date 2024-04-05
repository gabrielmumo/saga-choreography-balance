package dev.gabrielmumo.sagas.balance.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gabrielmumo.sagas.balance.dto.TransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class TransactionEventsProducer {
    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransactionEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<SendResult<Integer, String>> sendTransactionEvent(TransactionEvent transactionEvent,
                                                                               String transactionTopic)
            throws JsonProcessingException {
        var key = transactionEvent.transactionId();
        var value = objectMapper.writeValueAsString(transactionEvent);

        return kafkaTemplate.send(transactionTopic, key, value);
    }
}
