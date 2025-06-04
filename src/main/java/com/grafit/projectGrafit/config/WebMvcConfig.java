package com.grafit.projectGrafit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuración para recursos estáticos (CSS, JavaScript, imágenes)
        registry.addResourceHandler("/static/**", "/css/**", "/js/**", "/images/**")
                .addResourceLocations("classpath:/static/", "classpath:/static/css/", 
                                    "classpath:/static/js/", "classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS) // Cache por 7 días
                        .mustRevalidate()  // Debe revalidar después de expirar
                        .cachePublic());   // Permitir caché público

        // Configuración para favicon y otros recursos de la raíz
        registry.addResourceHandler("/favicon.ico", "/robots.txt")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
    }
} 