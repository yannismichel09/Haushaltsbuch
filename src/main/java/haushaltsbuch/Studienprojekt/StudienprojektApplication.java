package haushaltsbuch.Studienprojekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


// Hauptklasse der Spring Boot-Anwendung, die die Anwendung startet
@SpringBootApplication
@EntityScan(basePackages = "model")
@EnableJpaRepositories(basePackages = "model")
public class StudienprojektApplication {

	// Main-Methode, die die Spring Boot-Anwendung startet
	public static void main(String[] args) {
		SpringApplication.run(StudienprojektApplication.class, args);
	}

}
