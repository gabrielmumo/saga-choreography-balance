package dev.gabrielmumo.sagas.balance.service;

import dev.gabrielmumo.sagas.balance.domain.Account;
import dev.gabrielmumo.sagas.balance.dto.TransactionEvent;
import dev.gabrielmumo.sagas.balance.dto.TransactionStatus;
import dev.gabrielmumo.sagas.balance.exception.AccountNotFoundException;
import dev.gabrielmumo.sagas.balance.exception.NotEnoughBalanceException;
import dev.gabrielmumo.sagas.balance.repository.BalanceRepository;
import dev.gabrielmumo.sagas.balance.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {
    private static final Logger log = LoggerFactory.getLogger(BalanceService.class);
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;

    public BalanceService(BalanceRepository balanceRepository, TransactionRepository transactionRepository) {
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
    }

    public void processTransaction(TransactionEvent transactionEvent) {
        try {
            updateBalances(transactionEvent);
            String message = String.format(
                    "Status: %s | Transfer transaction was successfully completed.",
                    transactionEvent.transactionEventType()
            );
            transactionRepository.upsertTransaction(
                    transactionEvent.transactionId(),
                    String.format(message)
            );
            log.info(message);
        } catch (AccountNotFoundException e) {
            var message = String.format(
                    "Account was not found. Unable to continue transaction %d. Reason %s",
                    transactionEvent.transactionId(),
                    e.getMessage()

            );
            handleException(message, transactionEvent.transactionId(), TransactionStatus.FAILED);
        } catch (NotEnoughBalanceException e) {
            var message = String.format(
                    "Not enough balance in account %s to transfer %.5f. Unable to continue transaction %d. Reason %s",
                    transactionEvent.from(),
                    transactionEvent.amount(),
                    transactionEvent.transactionId(),
                    e.getMessage()
            );
            handleException(message, transactionEvent.transactionId(), TransactionStatus.REJECTED);
        } catch (Exception e) {
            var message = String.format(
                    "Error updating balances. Unable to continue transaction %d. Reason %s",
                    transactionEvent.transactionId(),
                    e.getMessage()
            );
            handleException(message, transactionEvent.transactionId(), TransactionStatus.FAILED);
        }
    }

    private void updateBalances(TransactionEvent transactionEvent)
            throws AccountNotFoundException, NotEnoughBalanceException {
        Account from = findAccount(transactionEvent.from());
        Account to = findAccount(transactionEvent.to());
        if (hasBalance(from, transactionEvent.amount())) {
            balanceRepository.balanceTransaction(from, to, transactionEvent.amount());
        } else {
            throw new NotEnoughBalanceException("Issuer does not have enough balance: " + from.getBalance());
        }
    }

    private Account findAccount(String number) throws AccountNotFoundException {
        var account = balanceRepository.findAccount(number);
        return account.orElseThrow(() -> new AccountNotFoundException("Account does not exists: " + number));
    }

    private boolean hasBalance(Account from, Double txnAmount) {
        return from.getBalance() > txnAmount;
    }

    private void handleException(String message, Integer id, TransactionStatus status) {
        log.error(message);
        transactionRepository.upsertTransaction(
                id,
                String.format("Status: %s | %s", status, message)
        );
    }
}
