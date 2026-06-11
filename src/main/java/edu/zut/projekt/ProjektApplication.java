package edu.zut.projekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import edu.zut.projekt.config.AppMailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppMailProperties.class)
@SpringBootApplication
public class ProjektApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjektApplication.class, args);
    }

}
