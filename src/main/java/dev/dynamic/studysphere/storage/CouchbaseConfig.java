package dev.dynamic.studysphere.storage;

import dev.dynamic.studysphere.StudysphereApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@Configuration
@EnableCouchbaseRepositories(basePackages = {"dev.dynamic.studysphere.entities"})
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Override
    public String getConnectionString() {
        return "data.polarix.host";
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
}
