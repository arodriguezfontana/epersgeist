package ar.edu.unq.epersgeist.dao.Utils;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Neo4jDatabaseCleaner {

    private final Driver neo4jDriver;

    public Neo4jDatabaseCleaner(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    @Transactional
    public void cleanDatabase() {
        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        }
    }
}
