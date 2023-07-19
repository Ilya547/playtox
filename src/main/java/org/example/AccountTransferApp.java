package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AccountTransferApp {
    static final Logger log = LoggerFactory.getLogger(AccountTransferApp.class);
    private static final int TRANSACTIONS_LIMIT = Integer.parseInt(PropertiesConnection.getAppProps()
            .getProperty("transactions.numbers"));
    private static final int ACCOUNTS_NUMBERS = Integer.parseInt(PropertiesConnection.getAppProps()
            .getProperty("accounts.numbers"));
    private static final int THREADS_NUMBERS = Integer.parseInt(PropertiesConnection.getAppProps()
            .getProperty("threads.numbers"));

    public static void main(String[] args) {
        log.info("App running");
        List<Account> accounts = createAccounts(ACCOUNTS_NUMBERS);
        startTransferThreads(accounts, THREADS_NUMBERS);
    }

    public static List<Account> createAccounts(int numAccounts) {
        List<Account> accounts = new ArrayList<>();

        for (int i = 0; i < numAccounts; i++) {
            String accountId = "A" + (i + 1);
            int initialMoney = 10000;
            Account account = new Account(accountId, initialMoney);
            accounts.add(account);
        }
        return accounts;
    }

    public static void startTransferThreads(List<Account> accounts, int numThreads) {
        for (int i = 0; i < numThreads; i++) {
            Thread transferThread = new Thread(() -> {
                Random random = new Random();
                int numTransactions = 0;

                while (numTransactions < TRANSACTIONS_LIMIT) {
                    try {
                        Thread.sleep(random.nextInt(1001) + 1000); // Sleep for 1000-2000 ms
                        transferFunds(accounts);
                        numTransactions++;
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
        int fromIndex = random.nextInt(accounts.size());
        int toIndex = random.nextInt(accounts.size());

        if (fromIndex == toIndex) {
            return;
        }

        Account fromAccount = accounts.get(fromIndex);
        Account toAccount = accounts.get(toIndex);

        synchronized (fromAccount) {
            synchronized (toAccount) {
                int amount = random.nextInt(fromAccount.getMoney() + 1);
                if (amount > 0) {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                    log.info("Transferred " + amount + " from " + fromAccount.getId() + " to " + toAccount.getId());
                }
            }
        }
    }
}