package org.example;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountTransferTest {

    private static final Pattern TRANSACTIONAL_PATTERN =
            Pattern.compile("Transferred\\s(?<money>\\d+)\\smoney\\sfrom\\s(?<fromId>[\\w|-]+)\\sto\\s(?<toId>[\\w|-]+)");

    @Test
    public void defaultProperties() {
        doTest(new ConfigurationProperties(30, 4, 4, 10000));
    }

    @Test
    public void overrideProperties() {
        doTest(new ConfigurationProperties(60, 8, 8, 20000));
    }

    private void doTest(ConfigurationProperties configurationProperties) {
        try (MockedStatic<ConfigurationProperties> stub = Mockito.mockStatic(ConfigurationProperties.class)) {
            stub.when(ConfigurationProperties::getInstance)
                    .thenReturn(configurationProperties);

            AccountStore accountStore = new AccountStore();
            accountStore.initAccounts();

            AccountMoneyTransfer accountMoneyTransfer = new AccountMoneyTransfer(accountStore);

            Logger transferLogger = (Logger) LoggerFactory.getLogger(AccountMoneyTransfer.class);
            ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
            listAppender.start();


            transferLogger.addAppender(listAppender);

            accountMoneyTransfer.run();

            List<Account> accounts = accountStore.getAccounts();

            //check total money
            int accountMoneySum = accounts.stream().mapToInt(Account::getMoney).sum();
            int expectedSum = configurationProperties.getInitMoney() * configurationProperties.getAccountsNumbers();
            assertEquals(expectedSum, accountMoneySum);


            List<TransactionInfo> transactionInfos = convertLogsToTransactionInfo(listAppender);
            for (Account account : accounts) {
                int accountMoney = account.getMoney();
                assertTrue(accountMoney >= 0);
                int accountMoneyFromLog = 0;
                for (TransactionInfo transactionInfo : transactionInfos) {
                    if (Objects.equals(transactionInfo.fromId, account.getId())) {
                        accountMoneyFromLog -= transactionInfo.money;
                    } else if (Objects.equals(transactionInfo.toId, account.getId())) {
                        accountMoneyFromLog += transactionInfo.money;
                    }

                 }
                assertEquals(configurationProperties.getInitMoney() + accountMoneyFromLog, accountMoney);
            }
        }
    }

    private List<TransactionInfo> convertLogsToTransactionInfo(ListAppender<ILoggingEvent> listAppender) {
        return listAppender.list.stream().map(iLoggingEvent -> {
            String message = iLoggingEvent.getMessage();
            if (message == null) {
                return null;
            }

            Matcher matcher = TRANSACTIONAL_PATTERN.matcher(message);
            if (!matcher.find()) {
                return null;
            }

            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.money = Integer.parseInt(matcher.group("money"));
            transactionInfo.fromId = matcher.group("fromId");
            transactionInfo.toId = matcher.group("toId");
            return transactionInfo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public class TransactionInfo {
        private int money;
        private String fromId;
        private String toId;
    }
}