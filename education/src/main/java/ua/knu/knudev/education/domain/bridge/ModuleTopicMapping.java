package ua.knu.knudev.education.domain.bridge;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramTopic;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "module_topic_mapping",
        schema = "education",
        uniqueConstraints = @UniqueConstraint(columnNames = {"module_id", "topic_id"})
)
public class ModuleTopicMapping {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id")
    private ProgramModule module;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private ProgramTopic topic;

    @Column(nullable = false)
    private int orderIndex;

}
