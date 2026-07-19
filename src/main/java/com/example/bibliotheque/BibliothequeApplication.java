package com.example.bibliotheque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Point d'entrée de l'application Spring Boot Bibliothèque. */
@SpringBootApplication
public class BibliothequeApplication {

    /** Démarre le contexte Spring et l'application. */
    public static void main(String[] args) {
        SpringApplication.run(BibliothequeApplication.class, args);
    }

}
