package dev.gabrielmumo.sagas.balance.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gabrielmumo.sagas.balance.dto.TransactionEvent;
import dev.gabrielmumo.sagas.balance.service.BalanceConsumerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventsConsumer {
    private static final Logger log = LoggerFactory.getLogger(TransactionEventsConsumer.class);
    private final BalanceConsumerService balanceConsumerService;
    private final ObjectMapper objectMapper;

    public TransactionEventsConsumer(BalanceConsumerService balanceConsumerService, ObjectMapper objectMapper) {
        this.balanceConsumerService = balanceConsumerService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"${spring.kafka.transaction-created-topic}"})
    public void onTransactionCreatedEvent(ConsumerRecord<Integer, String> consumerRecord) {
        try {
            TransactionEvent transactionEvent = objectMapper.readValue(consumerRecord.value(), TransactionEvent.class);
            balanceConsumerService.processTransaction(transactionEvent);
        } catch (JsonProcessingException e) {
            log.error("Unable to map event: ", e);
        }
    }
}
