package dev.gabrielmumo.sagas.balance.domain;

public class Account {
    private String holder;
    private String number;
    private Double balance;

    public Account(String holder, String number, Double balance) {
        this.holder = holder;
        this.number = number;
        this.balance = balance;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
