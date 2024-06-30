package dev.dynamic.studysphere.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Getter
@Setter
@Table(name = "notecards")
@EntityListeners(AuditingEntityListener.class)
public class Notecard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column
    private String name;
    @Column
    @LastModifiedDate
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime lastModified;
    @Column
    @CreatedDate
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime created;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "notecard_user_roles",
            joinColumns = @JoinColumn(name = "notecard_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserNotecardRole> userRoles = new HashSet<>();
    @Column
    private String content;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private NotecardCategory category;
    @Column
    private boolean deleted = false;
    @Column
    private LocalDateTime scheduledDeletionTime;
    @Column
    @Enumerated(EnumType.STRING)
    private NotecardVisibility visibility = NotecardVisibility.PRIVATE;
    @ElementCollection
    @CollectionTable(name = "notecard_past_summaries", joinColumns = @JoinColumn(name = "notecard_id"))
    @Column(name = "summary")
    private List<String> pastSummaries = new ArrayList<>();

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());
        objectMapper.registerModule(module);

        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }
}
