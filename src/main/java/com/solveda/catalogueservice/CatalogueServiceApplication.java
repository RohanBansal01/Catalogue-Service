package com.solveda.catalogueservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Catalogue Service Spring Boot application.
 * <p>
 * This class bootstraps the Spring application context and starts the embedded
 * web server. All Spring-managed beans, controllers, services, and repositories
 * will be initialized automatically.
 * </p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * java -jar catalogue-service.jar
 * }</pre>
 *
 * <p>This class is annotated with {@link SpringBootApplication}, which includes:</p>
 * <ul>
 *     <li>{@link org.springframework.context.annotation.Configuration}</li>
 *     <li>{@link org.springframework.boot.autoconfigure.EnableAutoConfiguration}</li>
 *     <li>{@link org.springframework.context.annotation.ComponentScan}</li>
 * </ul>
 *
 * @see org.springframework.boot.SpringApplication
 * @see SpringBootApplication
 */
@SpringBootApplication
public class CatalogueServiceApplication {

    /**
     * Main method that serves as the entry point of the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(CatalogueServiceApplication.class, args);
    }
}
