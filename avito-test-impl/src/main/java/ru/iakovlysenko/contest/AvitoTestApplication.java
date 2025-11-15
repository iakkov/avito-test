package ru.iakovlysenko.contest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "ru.iakovlysenko.contest.entity")
@EnableJpaRepositories(basePackages = "ru.iakovlysenko.contest.repository")
public class AvitoTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvitoTestApplication.class, args);
    }
}

