package com.evgenltd.ledgerserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
public class Application extends SpringBootServletInitializer {

    // todo LogAspect нацелить на аннотированные классы (Service, Controller, Repository)
    // todo TickerSymbol перенести в common пространство
    // todo Заполнить проводки для двух документов
    // todo скрипты миграции для бд
    // todo генерализировать контролеры
    // todo реализация сеттингов
    // todo реимплементация отчетов

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(final CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedOrigins("http://localhost:4200/");
            }
        };
    }

}
