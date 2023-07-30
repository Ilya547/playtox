package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


public class AccountMoneyTransfer {
    private static final Logger log = LoggerFactory.getLogger(AccountMoneyTransfer.class);

    private final ConfigurationProperties configurationProperties = ConfigurationProperties.getInstance();

    private final ReentrantLock lock = new ReentrantLock();

    private final AtomicInteger numTransactions = new AtomicInteger(0);

    private final AccountStore accountStore;

    public AccountMoneyTransfer(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public void run() {
        log.info("App running");
        startTransferThreads();
    }

    public void startTransferThreads() {
        int numThreads = configurationProperties.getThreadsNumbers();
        int transactionsLimit = configurationProperties.getTransactionsNumbers();
        Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                while (numTransactions.get() < transactionsLimit) {
                    try {
                        transferFunds(accountStore.getAccounts());
                        Thread.sleep(random.nextInt(1001) + 1000);
                    } catch (InterruptedException e) {
                        log.error("Thread interrupted: " + e.getMessage());
                    }
                }
                log.info("Thread completed: " + Thread.currentThread().getName());
            });
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void transferFunds(List<Account> accounts) {
        Random random = new Random();
        int transferAmount = random.nextInt(1000);

        MoneyTransferInfo moneyTransferInfo = getRandomAccount(accounts);
        Account fromAccount = moneyTransferInfo.fromAccount;
        Account toAccount = moneyTransferInfo.toAccount;

        lock.lock();
        try {
            if (fromAccount.getMoney() >= transferAmount) {
                fromAccount.setMoney(fromAccount.getMoney() - transferAmount);
                toAccount.setMoney(toAccount.getMoney() + transferAmount);
                int numTransaction = numTransactions.incrementAndGet();
                log.info("Transferred " + transferAmount + " money from " + fromAccount.getId() + " to "
                        + toAccount.getId() + ". Number of transactions : " + numTransaction + ". Thread name: "
                        + Thread.currentThread().getName());
            } else {
                log.info("Insufficient funds: " + fromAccount.getId());
            }
        } finally {
            lock.unlock();
        }
    }

    private MoneyTransferInfo getRandomAccount(List<Account> accounts) {
        Random random = new Random();

        int fromIndex;
        int toIndex;

        do {
            fromIndex = random.nextInt(accounts.size());
            toIndex = random.nextInt(accounts.size());
        } while (fromIndex == toIndex);

        return new MoneyTransferInfo(
                accounts.get(fromIndex),
                accounts.get(toIndex));
    }

    private static class MoneyTransferInfo {
        private final Account fromAccount;
        private final Account toAccount;

        public MoneyTransferInfo(Account fromAccount, Account toAccount) {
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
        }
    }
}