package org.example;

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

    public synchronized void debit(int amount) {
        if (money >= amount) {
            money -= amount;
        }
    }

    public synchronized void credit(int amount) {
        money += amount;
    }

    public void setMoney(int money) {
        this.money = money;
    }

}
