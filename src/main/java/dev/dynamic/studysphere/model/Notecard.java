package dev.dynamic.studysphere.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@Table(name = "notecards")
public class Notecard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column
    private String name;
    @Column
    @LastModifiedDate
    private LocalDateTime lastModified;
    @Column
    @CreatedDate
    private LocalDateTime created;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;
    @ManyToMany
    @JoinTable(
            name = "notecard_collaborators",
            joinColumns = @JoinColumn(name = "notecard_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> collaborators = new HashSet<>();
    @Column
    private String content;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private NotecardCategory category;
    @Column
    private boolean deleted = false;

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }
}
