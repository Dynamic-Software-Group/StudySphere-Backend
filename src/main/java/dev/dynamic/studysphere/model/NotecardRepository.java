package dev.dynamic.studysphere.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface NotecardRepository extends JpaRepository<Notecard, Long> {
    Set<Notecard> findByOwnerAndDeletedFalse(User owner);
    Set<Notecard> findByCollaboratorsAndDeletedFalse(Set<User> collaborators);
    Set<Notecard> findByCollaboratorsContainsAndDeletedFalse(User collaborator);
    Set<Notecard> findByCategoryAndDeletedFalse(NotecardCategory category);
    Set<Notecard> findByCategoryAndOwnerAndDeletedFalse(NotecardCategory category, User owner);
    Set<Notecard> findByOwnerAndDeletedTrue(User owner);
    Set<Notecard> findByOwnerAndCategoryIsNullAndDeletedFalse(User owner);
    Optional<Notecard> findById(UUID id);
    Set<Notecard> findByDeletedTrue();
}
