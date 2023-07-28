package org.example;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import static org.example.AccountTransferApp.log;

public class PropertiesConnection {
    private static final String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
            .getResource("")).getPath();
    private static final String appConfigPath = rootPath + "application.properties";

    public static Properties getAppProps() {
        Properties appProps = new Properties();
        try {
            appProps.load(Files.newInputStream(Paths.get(appConfigPath)));
        } catch (IOException e) {
            log.error("Application settings file is not found");
        }
        return appProps;
    }
}
