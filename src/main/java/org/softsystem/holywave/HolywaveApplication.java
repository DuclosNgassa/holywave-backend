package org.softsystem.holywave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Enables JPA auditing
public class HolywaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolywaveApplication.class, args);
    }

}
