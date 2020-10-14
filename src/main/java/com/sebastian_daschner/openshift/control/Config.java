package com.sebastian_daschner.openshift.control;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@ApplicationScoped
public class Config {

    private final Properties properties = new Properties();

    @PostConstruct
    private void initProperties() {
        try (final InputStream inputStream = new FileInputStream("/tmp/config-data/application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Could not init configuration from file /tmp/config-data/application.properties, cause: " + e.getMessage());
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String getSecret() {
        return System.getenv("TOP_SECRET");
    }

}
