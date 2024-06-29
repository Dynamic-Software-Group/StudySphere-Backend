package dev.dynamic.studysphere;

import dev.dynamic.studysphere.model.NotecardRepository;
import dev.dynamic.studysphere.realtime.WebsocketServer;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudysphereApplication {

	public static Dotenv dotenv;

	public void main(String[] args) {
		String envFile = System.getProperty("env.file.path") != null ? System.getProperty("env.file.path") : ".env";
		dotenv = Dotenv.configure().filename(envFile).load();
		SpringApplication.run(StudysphereApplication.class, args);
	}
}
