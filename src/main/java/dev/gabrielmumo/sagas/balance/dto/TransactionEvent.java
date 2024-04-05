package dev.gabrielmumo.sagas.balance.dto;

public record TransactionEvent(Integer transactionId,
                               TransactionStatus transactionEventType,
                               String from,
                               String to,
                               Double amount,
                               String message) {

    public TransactionEvent build(TransactionStatus transactionEventType) {
        return new TransactionEvent(
                this.transactionId,
                transactionEventType,
                this.from,
                this.to,
                this.amount,
                this.message
        );
    }
}
