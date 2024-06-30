package dev.dynamic.studysphere.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Table
@Data
@Getter
public class UserNotecardRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "notecard_id", referencedColumnName = "id")
    private Notecard notecard;

    @Column
    @Enumerated(EnumType.STRING)
    private NotecardRole role;

    @Column
    @Enumerated(EnumType.STRING)
    private NotecardVisibility visibility;
}
