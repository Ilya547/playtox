package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AccountStore {

    private final ConfigurationProperties configurationProperties = ConfigurationProperties.getInstance();

    private final List<Account> accounts = new ArrayList<>();

    public void initAccounts() {
        accounts.addAll(createAccounts());
    }

    public List<Account> createAccounts() {
        List<Account> accounts = new ArrayList<>();

        for (int i = 0; i < configurationProperties.getAccountsNumbers(); i++) {
            String accountId = UUID.randomUUID() + "||A" + (i + 1);
            Account account = new Account(accountId, configurationProperties.getInitMoney());
            accounts.add(account);
        }
        return accounts;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }
}