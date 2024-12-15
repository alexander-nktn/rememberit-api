package rememberit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "rememberit")
public class Rememberit {

	public static void main(String[] args) {
		SpringApplication.run(Rememberit.class, args);
	}
}
