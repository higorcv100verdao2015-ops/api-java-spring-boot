package com.company.nomeprojeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;

@SpringBootApplication
public class ApplicationTutorial {

    public static void main(String[] args) {
        configureRenderDatabaseUrl();
        SpringApplication.run(ApplicationTutorial.class, args);
    }

    private static void configureRenderDatabaseUrl() {
        if (System.getProperty("spring.datasource.url") != null) {
            return;
        }

        String databaseUrl = firstNonBlank(System.getenv("JDBC_DATABASE_URL"), System.getenv("DATABASE_URL"));

        if (databaseUrl == null) {
            return;
        }

        if (databaseUrl.startsWith("jdbc:postgresql://")) {
            System.setProperty("spring.datasource.url", databaseUrl);
            return;
        }

        if (!databaseUrl.startsWith("postgres://") && !databaseUrl.startsWith("postgresql://")) {
            return;
        }

        URI uri = URI.create(databaseUrl);
        String database = uri.getPath() == null ? "" : uri.getPath();
        String query = uri.getRawQuery();
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + database;

        if (query != null && !query.isBlank()) {
            jdbcUrl += "?" + query;
        }

        System.setProperty("spring.datasource.url", jdbcUrl);

        String userInfo = uri.getUserInfo();

        if (userInfo == null) {
            return;
        }

        int separatorIndex = userInfo.indexOf(':');

        if (separatorIndex >= 0) {
            System.setProperty("spring.datasource.username", userInfo.substring(0, separatorIndex));
            System.setProperty("spring.datasource.password", userInfo.substring(separatorIndex + 1));
        } else {
            System.setProperty("spring.datasource.username", userInfo);
        }
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }

        if (second != null && !second.isBlank()) {
            return second;
        }

        return null;
    }
}
