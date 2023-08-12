package com.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * @author u2cc
 */

@SpringBootApplication
@EnableR2dbcRepositories
public class RESTfulStartup {
    public static void main(String[] args) {
        SpringApplication.run(RESTfulStartup.class, args);
    }
}
