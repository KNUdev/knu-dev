package ua.knu.knudev.education.domain.program;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.MultiLanguageField;
import ua.knu.knudev.education.domain.EducationTaskProxy;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "section", schema = "education")
@Builder
public class ProgramSection {

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

@OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private EducationTaskProxy sectionFinalTask;

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
