package ru.iakovlysenko.contest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.iakovlysenko.contest.repository")
public class AvitoTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvitoTestApplication.class, args);
    }
}

