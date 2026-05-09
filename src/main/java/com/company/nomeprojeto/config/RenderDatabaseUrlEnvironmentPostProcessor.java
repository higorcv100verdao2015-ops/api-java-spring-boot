package com.company.nomeprojeto.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RenderDatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isBlank()) {
            databaseUrl = environment.getProperty("JDBC_DATABASE_URL");
        }

        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }

        if (databaseUrl.startsWith("jdbc:postgresql://")) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.url", databaseUrl);
            environment.getPropertySources().addFirst(new MapPropertySource("renderDatabaseUrl", properties));
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

        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.datasource.url", jdbcUrl);

        String userInfo = uri.getUserInfo();

        if (userInfo != null) {
            int separatorIndex = userInfo.indexOf(':');

            if (separatorIndex >= 0) {
                properties.put("spring.datasource.username", userInfo.substring(0, separatorIndex));
                properties.put("spring.datasource.password", userInfo.substring(separatorIndex + 1));
            } else {
                properties.put("spring.datasource.username", userInfo);
            }
        }

        environment.getPropertySources().addFirst(new MapPropertySource("renderDatabaseUrl", properties));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
