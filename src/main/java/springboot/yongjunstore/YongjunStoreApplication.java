package springboot.yongjunstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class YongjunStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(YongjunStoreApplication.class, args);
    }

}
