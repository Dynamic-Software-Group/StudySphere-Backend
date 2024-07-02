package dev.dynamic.studysphere.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotecardUserRoleRepository extends JpaRepository<UserNotecardRole, Long> {
    UserNotecardRole findByUserAndNotecard(User user, Notecard notecard);
}
