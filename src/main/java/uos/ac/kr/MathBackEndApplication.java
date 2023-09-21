package uos.ac.kr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;
import javax.servlet.Filter;

@SpringBootApplication
public class MathBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(MathBackEndApplication.class, args);
	}

}
