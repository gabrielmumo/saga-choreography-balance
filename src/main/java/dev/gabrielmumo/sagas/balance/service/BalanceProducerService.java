package dev.gabrielmumo.sagas.balance.service;

import dev.gabrielmumo.sagas.balance.dto.TransactionEvent;
import dev.gabrielmumo.sagas.balance.producer.TransactionEventsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BalanceProducerService {
    private static final Logger log = LoggerFactory.getLogger(BalanceProducerService.class);
    @Value("${spring.kafka.transaction-completed-topic}")
    public String transactionCompletedTopic;
    @Value("${spring.kafka.transaction-rejected-topic}")
    public String transactionRejectedTopic;
    private final TransactionEventsProducer transactionEventsProducer;

    public BalanceProducerService(TransactionEventsProducer transactionEventsProducer) {
        this.transactionEventsProducer = transactionEventsProducer;
    }

    public void produceCompletedTransactionEvent(TransactionEvent transactionEvent) {
        produceTransactionEvent(transactionEvent, transactionCompletedTopic);
    }

    public void produceRejectedTransactionEvent(TransactionEvent transactionEvent) {
        produceTransactionEvent(transactionEvent, transactionRejectedTopic);
    }

    private void produceTransactionEvent(TransactionEvent transactionEvent, String topic) {
        try {
            var completableFuture = transactionEventsProducer.sendTransactionEvent(
                    transactionEvent,
                    topic
            );
            completableFuture.whenComplete((sendResult, throwable) -> {
                if(throwable != null) {
                    log.error("Error sending transaction: {} with key: {}",
                            transactionEvent,
                            transactionEvent.transactionId(),
                            throwable
                    );
                } else {
                    log.info("Transaction: {} was sent successfully. Key: {} & partition: {}",
                            transactionEvent,
                            transactionEvent.transactionId(),
                            sendResult.getRecordMetadata().partition()
                    );
                }
            });
        } catch (Exception e) {
            log.error("Error sending transaction: {} with key: {}",
                    transactionEvent,
                    transactionEvent.transactionId(),
                    e
            );
        }
    }
}
