package com.grafit.projectGrafit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.grafit.projectGrafit")
@EntityScan("com.grafit.projectGrafit.models")
@EnableJpaRepositories("com.grafit.projectGrafit.repositories")
public class ProjectGrafitApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectGrafitApplication.class, args);
	}

}
