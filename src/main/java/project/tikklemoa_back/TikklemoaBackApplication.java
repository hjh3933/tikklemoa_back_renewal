package project.tikklemoa_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TikklemoaBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TikklemoaBackApplication.class, args);
	}

}
