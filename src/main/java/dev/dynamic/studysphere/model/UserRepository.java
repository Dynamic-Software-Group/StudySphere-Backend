package dev.dynamic.studysphere.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
