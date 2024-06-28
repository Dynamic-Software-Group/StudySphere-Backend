package dev.dynamic.studysphere.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;

@Data
@Document
public class User {
    @Id
    @Field
    private Long id;
    @Field
    private String username;
    @Field
    private String password;
    @Field
    private String email;
    @Field
    private Role role;
}
