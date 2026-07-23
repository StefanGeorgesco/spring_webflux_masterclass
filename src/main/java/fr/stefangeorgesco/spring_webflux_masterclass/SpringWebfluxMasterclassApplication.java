package fr.stefangeorgesco.spring_webflux_masterclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = "fr.stefangeorgesco.spring_webflux_masterclass.${section}")
@EnableR2dbcRepositories(basePackages = "fr.stefangeorgesco.spring_webflux_masterclass.${section}.repository")
public class SpringWebfluxMasterclassApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxMasterclassApplication.class, args);
	}

}
