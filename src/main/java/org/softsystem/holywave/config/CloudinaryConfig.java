package org.softsystem.holywave.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(@Value("${CLOUDINARY_CLOUD_NAME}") String cloudinaryCloudName,
                                 @Value("${CLOUDINARY_API_KEY}") String cloudinaryApiKey,
                                 @Value("${CLOUDINARY_API_SECRET}") String cloudinaryApiSecret){
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudinaryCloudName,
                "api_key", cloudinaryApiKey,
                "api_secret", cloudinaryApiSecret,
                "secure", true
        ));
    }
}
