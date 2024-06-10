package com.tempalych.europredictor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EuroPredictorApplication {

    public static void main(String[] args) {
        SpringApplication.run(EuroPredictorApplication.class, args);
    }

}
