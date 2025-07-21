package ar.edu.unq.epersgeist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableNeo4jRepositories
public class EpersgeistApp {

    public static void main(String[] args) {
        SpringApplication.run(EpersgeistApp.class, args);
    }
}