package pers.zp.shorturlsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@EnableCaching
@EnableR2dbcAuditing
@SpringBootApplication
public class ShortUrlSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortUrlSystemApplication.class, args);
    }

}
