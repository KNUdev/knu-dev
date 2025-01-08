package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(schema = "team_management", name = "profile_account")
public class AccountProfile {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String middleName;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;
    private String avatarFilename;

    @Enumerated(EnumType.STRING)
    @Column(name = "technical_role", nullable = false)
    private AccountTechnicalRole technicalRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Expertise expertise;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationDate;
    private LocalDateTime lastRoleUpdateDate;
    private LocalDateTime registrationEndDate;

    @Column(nullable = false)
    private Integer studyYearsOnRegistration;

    @Column(nullable = false)
    private KNUdevUnit knudevUnit;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "specialty_code_name", referencedColumnName = "code_name", nullable = false)
    private Specialty specialty;

    @PrePersist
    @PreUpdate
    private void updateKNUdevUnit() {
        this.knudevUnit = this.technicalRole == AccountTechnicalRole.INTERN ? KNUdevUnit.PRECAMPUS : KNUdevUnit.CAMPUS;
    }

    public Integer calculateCurrentStudyYears() {
        Integer baseStudyYears = this.studyYearsOnRegistration;
        LocalDate registrationYearEndDate = determineAcademicYearEndDate(this.registrationDate);
        LocalDate currentYearEndDate = determineAcademicYearEndDate(LocalDateTime.now());
        int additionalStudyYears = (int) ChronoUnit.YEARS.between(registrationYearEndDate, currentYearEndDate);
        return baseStudyYears + additionalStudyYears;
    }

    private LocalDate determineAcademicYearEndDate(LocalDateTime dateTime) {
        if (dateTime.getMonthValue() > 6) {
            return LocalDate.of(dateTime.getYear() + 1, 6, 30);
        } else {
            return LocalDate.of(dateTime.getYear(), 6, 30);
        }
    }

}
