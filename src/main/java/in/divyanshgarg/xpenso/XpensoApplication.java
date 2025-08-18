package in.divyanshgarg.xpenso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XpensoApplication {

	public static void main(String[] args) {
		SpringApplication.run(XpensoApplication.class, args);
	}

}
