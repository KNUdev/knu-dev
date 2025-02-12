package ua.knu.knudev.education.domain.program;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.MultiLanguageField;
import ua.knu.knudev.education.domain.EducationTaskProxy;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "topic", schema = "education")
@Builder
public class ProgramTopic {

    @Id
    @UuidGenerator
    private UUID id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "en_name")),
            @AttributeOverride(name = "uk", column = @Column(name = "uk_name"))
    })
    private MultiLanguageField name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "en_description")),
            @AttributeOverride(name = "uk", column = @Column(name = "uk_description"))
    })
    private MultiLanguageField description;

    @CreationTimestamp
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @ElementCollection
    @CollectionTable(
            name = "topic_learning_resources",
            joinColumns = @JoinColumn(name = "topic_id")
    )
    @Column(name = "learning_resources", nullable = false)
    private Set<String> learningResources = new HashSet<>();

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private EducationTaskProxy task;

    //TODO TESTS

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
