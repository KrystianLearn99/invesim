package org.kris.invesim.portfolioms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class PortfolioMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioMsApplication.class, args);
    }

}
