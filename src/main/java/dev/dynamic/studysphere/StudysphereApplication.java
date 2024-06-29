package dev.dynamic.studysphere;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudysphereApplication {

	public static Dotenv dotenv;

	public static void main(String[] args) {
		String envFile = System.getProperty("env.file.path") != null ? System.getProperty("env.file.path") : ".env";
		dotenv = Dotenv.configure().filename(envFile).load();
		SpringApplication.run(StudysphereApplication.class, args);
	}
}
