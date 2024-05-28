package com.tempalych.europredictor.config;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import spock.lang.Shared;

import javax.sql.DataSource;

@TestConfiguration
public class TestDataSourceConfig {

    @Shared
    private final static PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testpredictor")
                    .withUsername("test")
                    .withPassword("test")
                    .waitingFor(Wait.forListeningPort());

    static {
        POSTGRES.start();
    }

    @Bean
    public DataSource dataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUser(POSTGRES.getUsername());
        ds.setURL(POSTGRES.getJdbcUrl());
        ds.setPassword(POSTGRES.getPassword());
        return ds;
    }
}
