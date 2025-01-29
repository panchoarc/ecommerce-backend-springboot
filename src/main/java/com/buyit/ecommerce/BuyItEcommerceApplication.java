package com.buyit.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BuyItEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuyItEcommerceApplication.class, args);
    }

}
