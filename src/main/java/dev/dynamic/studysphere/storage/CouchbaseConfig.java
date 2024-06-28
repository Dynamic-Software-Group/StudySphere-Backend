package dev.dynamic.studysphere.storage;

import dev.dynamic.studysphere.StudysphereApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.CouchbaseRepositoriesRegistrar;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@Configuration
@EnableCouchbaseRepositories(basePackages = {"dev.dynamic.studysphere.entities"})
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Override
    public String getConnectionString() {
        return "couchbase://185.240.134.120:11210";
    }

    @Override
    public String getUserName() {
        return StudysphereApplication.dotenv.get("COUCHBASE_USERNAME");
    }

    @Override
    public String getPassword() {
        return StudysphereApplication.dotenv.get("COUCHBASE_PASSWORD");
    }

    @Override
    public String getBucketName() {
        return "study_sphere";
    }

    @Bean
    public CustomConversions customConversions() {
        return super.customConversions();
    }

    @Bean
    public CustomConversions couchbaseCustomConversions() {
        return super.customConversions();
    }

    @Bean
    public CouchbaseRepositoriesRegistrar couchbaseRepositoryRegistrar() {
        return new CouchbaseRepositoriesRegistrar();
    }

}