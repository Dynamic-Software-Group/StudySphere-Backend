package dev.dynamic.studysphere.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;

@Document
public class User {
    @Id
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
