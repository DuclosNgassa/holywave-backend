package org.softsystem.holywave.config;

import com.clerk.backend_api.Clerk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClerkClientConfig {

    @Bean
    Clerk clerk(@Value("${CLERK_SECRET_KEY}") String secretKey){
       return Clerk.builder()
               .bearerAuth(secretKey)
               .build();
    }
}