package dev.dynamic.studysphere.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Getter
@Table(name = "users")
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToMany
    @JoinTable(
            name = "favorite_notecards",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "notecard_id")
    )
    private Set<Notecard> favoriteNotecards = new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "user_categories",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<NotecardCategory> categories = new HashSet<>();
    @Column
    private String base64Avatar;
    @Column
    private int apiQuota = 0;
    @Column
    private boolean emailVerified = false;
    @Column
    private UUID emailVerificationToken;
}