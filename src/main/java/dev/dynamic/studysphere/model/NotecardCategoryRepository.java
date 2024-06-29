package dev.dynamic.studysphere.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface NotecardCategoryRepository extends JpaRepository<NotecardCategory, Long> {
    NotecardCategory findByName(String name);
    Set<NotecardCategory> findByOwner(User owner);
    NotecardCategory findByNameAndOwner(String name, User owner);
}
