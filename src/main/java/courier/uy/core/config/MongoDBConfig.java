package courier.uy.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories("courier.uy.core.repository")
@Configuration
public class MongoDBConfig {
}
