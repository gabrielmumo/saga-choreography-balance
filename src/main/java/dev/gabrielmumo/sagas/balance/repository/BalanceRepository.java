package dev.gabrielmumo.sagas.balance.repository;

import dev.gabrielmumo.sagas.balance.domain.Account;
import dev.gabrielmumo.sagas.balance.dto.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BalanceRepository {
    private final Map<String, Account> accountTbl = new HashMap<>();

    public BalanceRepository() {
        Account pepito = new Account(
                "Pepito Perez",
                "954-609-22",
                1000.0
        );
        accountTbl.put(pepito.getNumber(), pepito);

        Account maria = new Account(
                "Maria Lazo",
                "278-965-15",
                24.99
        );
        accountTbl.put(maria.getNumber(), maria);
    }

    public void upsertAccount(Account account) {
        accountTbl.put(account.getNumber(), account);
    }

    public void balanceTransaction(Account from, Account to, Double txnAmount) {
        from.setBalance(from.getBalance() - txnAmount);
        upsertAccount(from);
        to.setBalance(to.getBalance() + txnAmount);
        upsertAccount(to);
    }

    public Optional<Account> findAccount(String number) {
        return Optional.ofNullable(accountTbl.get(number));
    }
}
