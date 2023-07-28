package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Account {
    private final String id;
    private int money;

    public Account(String id, int money) {
        this.id = id;
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public static List<Account> getRandomAccount(List<Account> accounts) {
        Random random = new Random();

        int fromIndex;
        int toIndex;

        do {
            fromIndex = random.nextInt(accounts.size());
            toIndex = random.nextInt(accounts.size());
        } while (fromIndex == toIndex);

        Account fromAccount = accounts.get(fromIndex);
        Account toAccount = accounts.get(toIndex);

        List<Account> accountsFromTo = new ArrayList<>();
        accountsFromTo.add(fromAccount);
        accountsFromTo.add(toAccount);

        return accountsFromTo;
    }
}
