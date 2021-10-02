package com.wirebarley.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.wirebarley.core.*",
        "com.wirebarley.front.*"
})
public class FrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontApplication.class, args);
    }

}
