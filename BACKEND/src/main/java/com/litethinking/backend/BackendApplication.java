package com.litethinking.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Punto de entrada de la aplicación LiteThinking Backend.
 *
 * <p>La anotación {@code @ConfigurationPropertiesScan} habilita el escaneo automático
 * de todas las clases anotadas con {@code @ConfigurationProperties} en el paquete raíz,
 * evitando la necesidad de registrarlas manualmente con {@code @EnableConfigurationProperties}.</p>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
