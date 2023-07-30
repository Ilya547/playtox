package org.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class ConfigurationProperties {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationProperties.class);
    private static final String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
            .getResource("")).getPath();
    private static final String appConfigPath = rootPath + "application.properties";

    private static ConfigurationProperties INSTANCE;

    private final int transactionsNumbers;
    private final int accountsNumbers;
    private final int threadsNumbers;
    private final int initMoney;

    public ConfigurationProperties(int transactionsNumbers,
                                   int accountsNumbers,
                                   int threadsNumbers,
                                   int initMoney) {
        this.transactionsNumbers = transactionsNumbers;
        this.accountsNumbers = accountsNumbers;
        this.threadsNumbers = threadsNumbers;
        this.initMoney = initMoney;
    }

    public int getTransactionsNumbers() {
        return transactionsNumbers;
    }

    public int getAccountsNumbers() {
        return accountsNumbers;
    }

    public int getThreadsNumbers() {
        return threadsNumbers;
    }

    public int getInitMoney() {
        return initMoney;
    }


    public synchronized static ConfigurationProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = ConfigurationProperties.load();
        }
        return INSTANCE;
    }

    public static ConfigurationProperties load() {
        Properties appProps = getAppProps();
        return new ConfigurationProperties(
                getIntValue(appProps, "transactions.numbers"),
                getIntValue(appProps, "accounts.numbers"),
                getIntValue(appProps, "threads.numbers"),
                getIntValue(appProps, "money.defaultValue")
        );
    }

    private static int getIntValue(Properties properties, String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    private static Properties getAppProps() {
        Properties appProps = new Properties();
        try {
            appProps.load(Files.newInputStream(Paths.get(appConfigPath)));
        } catch (IOException e) {
            log.error("Application settings file is not found");
        }
        return appProps;
    }
}
