package ar.edu.unq.epersgeist.dao.Utils;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class MongoDBDatabaseCleaner {

    private final MongoTemplate mongoTemplate;

    public MongoDBDatabaseCleaner(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void cleanDatabase() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            if (!collectionName.startsWith("system.")) {
                mongoTemplate.remove(new Query(), collectionName);
            }
        }
    }
}
