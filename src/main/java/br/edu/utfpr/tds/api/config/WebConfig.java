package br.edu.utfpr.tds.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// Classe utilizada para habilitar os métodos anotados com @Scheduled para agendamento de execução
@Configuration
@EnableScheduling
public class WebConfig {

}
