package ua.knu.knudev.intergrationtests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;
import ua.knu.knudev.teammanager.domain.ProjectReleaseInfo;

import java.util.UUID;

@ActiveProfiles("test")
public interface ProjectReleaseInfoRepository extends JpaRepository<ProjectReleaseInfo, UUID> {
}