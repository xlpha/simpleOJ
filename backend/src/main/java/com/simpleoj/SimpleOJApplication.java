package com.simpleoj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SimpleOJApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleOJApplication.class, args);
    }

}
