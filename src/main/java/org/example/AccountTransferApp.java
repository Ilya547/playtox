package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class AccountTransferApp {
    static final Logger log = LoggerFactory.getLogger(AccountTransferApp.class);
    private static final int TRANSACTIONS_LIMIT = Integer.parseInt(PropertiesConnection.getAppProps()
            .getProperty("transactions.numbers"));
    private static final int ACCOUNTS_NUMBERS = Integer.parseInt(PropertiesConnection.getAppProps()
            .getProperty("accounts.numbers"));
    private static final int THREADS_NUMBERS = Integer.parseInt(PropertiesConnection.getAppProps()
            .getProperty("threads.numbers"));
    private static final int INIT_MONEY = Integer.parseInt(PropertiesConnection.getAppProps()
            .getProperty("money.defaultValue"));

    private static final Object LOCK = new Object();
    static int numTransactions;

    public static void main(String[] args) {
        log.info("App running");
        List<Account> accounts = createAccounts(ACCOUNTS_NUMBERS);
        startTransferThreads(accounts, THREADS_NUMBERS);
    }

    public static List<Account> createAccounts(int numAccounts) {
        List<Account> accounts = new ArrayList<>();

        for (int i = 0; i < numAccounts; i++) {
            String accountId = String.valueOf(new StringBuilder("A||" + UUID.randomUUID() + "||" + (i + 1)));
            Account account = new Account(accountId, INIT_MONEY);
            accounts.add(account);
        }
        return accounts;
    }

    public static void startTransferThreads(List<Account> accounts, int numThreads) {
        for (int i = 0; i < numThreads; i++) {
            Thread transferThread = new Thread(() -> {
                Random random = new Random();
                while (numTransactions < TRANSACTIONS_LIMIT) {
                    try {
                        transferFunds(accounts);
                        Thread.sleep(random.nextInt(1001) + 1000);
                    } catch (InterruptedException e) {
                        log.error("Thread interrupted: " + e.getMessage());
                    }
                }
                log.info("Thread completed: " + Thread.currentThread().getName());
            });
            transferThread.start();
        }
    }

    private static void transferFunds(List<Account> accounts) {
        Random random = new Random();
        int transferAmount = random.nextInt(1000);

        List<Account> twoRandomAccount = Account.getRandomAccount(accounts);
        Account fromAccount = twoRandomAccount.get(0);
        Account toAccount = twoRandomAccount.get(1);

        synchronized (LOCK) {
            if (fromAccount.getMoney() >= transferAmount) {
                fromAccount.setMoney(fromAccount.getMoney() - transferAmount);
                toAccount.setMoney(toAccount.getMoney() + transferAmount);
                numTransactions++;
                log.info("Transferred " + transferAmount + " money from " + fromAccount.getId() + " to "
                        + toAccount.getId() + ". Number of transactions : " + numTransactions);
            } else {
                log.info("Insufficient funds: " + fromAccount.getId());
            }
        }
    }
}
