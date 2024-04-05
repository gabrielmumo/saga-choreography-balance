package dev.gabrielmumo.sagas.balance.repository;

import dev.gabrielmumo.sagas.balance.dto.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class TransactionRepository {
    private final Map<Integer, String> transactionTbl = new HashMap<>();

    public void upsertTransaction(Integer id, String status) {
        transactionTbl.put(id, status);
    }

    public Optional<String> findTransactionStatus(Integer id) {
        return Optional.ofNullable(transactionTbl.get(id));
    }
}
