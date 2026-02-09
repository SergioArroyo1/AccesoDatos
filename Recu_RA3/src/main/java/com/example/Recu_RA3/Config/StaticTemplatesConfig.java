package com.example.Recu_RA3.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticTemplatesConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve todo lo que esté bajo src/main/resources/templates/** como recurso estático
        registry.addResourceHandler("/templates/**")
                .addResourceLocations("classpath:/templates/");
    }
}