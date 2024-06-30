package dev.dynamic.studysphere.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.checkerframework.checker.units.qual.C;

import java.time.LocalDateTime;
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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "favorite_notecards",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "notecard_id")
    )
    private Set<Notecard> favoriteNotecards = new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
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
    private boolean emailVerified = false; //todo
    @Column
    private UUID emailVerificationToken;
    @Column
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime emailVerificationTokenExpiration = LocalDateTime.now().plusHours(1);
}