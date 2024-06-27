package dev.dynamic.studysphere.storage;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@Configuration
@EnableCouchbaseRepositories(basePackages = {"dev.dynamic.studysphere.entities"})
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Override
    public String getConnectionString() {
        return "https://data.polarix.host/";
    }

    @Override
    public String getUserName() {
        return "";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getBucketName() {
        return "study_sphere";
    }
}
