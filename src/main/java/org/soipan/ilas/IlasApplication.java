package org.soipan.ilas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IlasApplication {

    public static void main(String[] args) {
        SpringApplication.run(IlasApplication.class, args);
    }

}
