import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableEurekaClient
@ComponentScan(value = "com.gushici.**")
@MapperScan(value = "com.gushici.mapper")
public class GushiciServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GushiciServerApplication.class, args);
	}

}
