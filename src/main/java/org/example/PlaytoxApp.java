package org.example;

public class PlaytoxApp {

    public static void main(String[] args) {
        AccountStore accountStore = new AccountStore();
        accountStore.initAccounts();

        new AccountMoneyTransfer(accountStore).run();
    }
}
