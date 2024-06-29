package dev.dynamic.studysphere.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface NotecardRepository extends JpaRepository<Notecard, Long> {
    Set<Notecard> findByOwner(User owner);
    Set<Notecard> findByCollaborators(Set<User> collaborators);
    Set<Notecard> findByCollaboratorsContains(User collaborator);
    Set<Notecard> findByCategory(NotecardCategory category);
}
