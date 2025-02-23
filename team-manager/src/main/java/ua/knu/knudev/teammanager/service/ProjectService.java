package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.SubprojectAccount;
import ua.knu.knudev.teammanager.domain.Release;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.mapper.MultiLanguageFieldMapper;
//import ua.knu.knudev.teammanager.mapper.ProjectMapper;
import ua.knu.knudev.teammanager.repository.ProjectRepository;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;
import ua.knu.knudev.teammanagerapi.exception.ProjectException;
import ua.knu.knudev.teammanagerapi.request.AddProjectDeveloperRequest;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectApi {

//    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final ImageServiceApi imageServiceApi;
    private final AccountProfileService accountProfileService;
    private final MultiLanguageFieldMapper multiLanguageFieldMapper;

    @Override
    public FullProjectDto create(ProjectCreationRequest projectCreationRequest) {
        return null;
    }

    @Override
    public FullProjectDto addDeveloper(AddProjectDeveloperRequest addProjectDeveloperRequest) {
        return null;
    }

    @Override
    public FullProjectDto updateStatus(UUID projectId, ProjectStatus newProjectStatus) {
        return null;
    }

    @Override
    public FullProjectDto getById(UUID projectId) {
        return null;
    }

    @Override
    public Page<ShortProjectDto> getAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public FullProjectDto release(UUID projectId, String projectDomain) {
        return null;
    }

//    @Override
//    @Transactional
//    public FullProjectDto create(ProjectCreationRequest projectCreationRequest) {
//        MultiLanguageField name = multiLanguageFieldMapper.toDomain(projectCreationRequest.name());
//        MultiLanguageField description = multiLanguageFieldMapper.toDomain(projectCreationRequest.description());
//
//        String filename = imageServiceApi.uploadFile(
//                projectCreationRequest.avatarFile(),
//                name.getEn(),
//                ImageSubfolder.PROJECTS_AVATARS
//        );
//
//        Project project = Project.builder()
//                .name(name)
//                .description(description)
//                .avatarFilename(filename)
//                .tags(projectCreationRequest.tags())
//                .startedAt(null)
//                .githubRepoLinks(projectCreationRequest.githubRepoUrls())
//                .status(ProjectStatus.PLANNED)
//                .build();
//
//        Project savedProject = projectRepository.save(project);
//        log.info("Project was created: {}", project.getId());
//        return projectMapper.toDto(savedProject);
//    }
//
//    @Override
//    @SneakyThrows
//    @Transactional
//    public FullProjectDto addDeveloper(AddProjectDeveloperRequest addProjectDeveloperRequest) {
//        UUID projectId = addProjectDeveloperRequest.projectId();
//        UUID accountProfileId = addProjectDeveloperRequest.accountProfileId();
//        Project project = getProjectById(projectId);
//        AccountProfile accountProfile = accountProfileService.getDomainById(accountProfileId);
//
//        Set<SubprojectAccount> subprojectAccounts = project.getProjectAccounts();
//        SubprojectAccount subprojectAccount = createProjectAccount(
//                project,
//                accountProfile,
//                projectId,
//                accountProfileId
//        );
//
//        List<UUID> accountProfileIds = subprojectAccounts.stream()
//                .map(account -> account.getAccountProfile().getId())
//                .toList();
//
//        if (accountProfileIds.stream().anyMatch(id -> accountProfile.getId().equals(id))) {
//            throw new ProjectException("Account with ID: " + accountProfileId +
//                    " is already assigned to project: " + projectId);
//        }
//
//        subprojectAccounts.add(subprojectAccount);
//        Project savedProject = projectRepository.save(project);
//        log.info("Added developer: {}, to project: {}", subprojectAccount.getId().getAccountId(), projectId);
//        return projectMapper.toDto(savedProject);
//    }
//
//    @Override
//    @Transactional
//    public FullProjectDto updateStatus(UUID projectId, ProjectStatus newProjectStatus) {
//        if (newProjectStatus == null) {
//            throw new ProjectException("Project status can't be null!");
//        }
//
//        Project project = getProjectById(projectId);
//
//        if (project.getStatus() == ProjectStatus.PLANNED &&
//                newProjectStatus == ProjectStatus.UNDER_DEVELOPMENT) {
//            project.setStartedAt(LocalDate.now());
//        }
//
//        project.setStatus(newProjectStatus);
//        Project savedProject = projectRepository.save(project);
//        log.info("Project status updated: {}, in project: {}", newProjectStatus, projectId);
//        return projectMapper.toDto(savedProject);
//    }
//
//    @Override
//    @Transactional
//    public FullProjectDto getById(UUID projectId) {
//        Project project = getProjectById(projectId);
//        return projectMapper.toDto(project);
//    }
//
//    @Override
//    @Transactional
//    public Page<ShortProjectDto> getAll(Integer pageNumber, Integer pageSize) {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        Page<Project> allProjectsPage = projectRepository.findAll(pageable);
//
//        return allProjectsPage.map(project -> new ShortProjectDto(
//                multiLanguageFieldMapper.toDto(project.getName()),
//                multiLanguageFieldMapper.toDto(project.getDescription()),
//                project.getStatus(),
//                project.getAvatarFilename(),
//                project.getTags()
//        ));
//    }
//
//    @Override
//    @Transactional
//    public FullProjectDto release(UUID projectId, String projectDomain) {
//        Project project = getProjectById(projectId);
//
//        if (project.getReleaseInfo() != null) {
//            throw new ProjectException("Project already has a release!");
//        }
//        Release release = Release.builder()
//                .releaseDate(LocalDate.now())
//                .projectDomain(projectDomain)
//                .project(project)
//                .build();
//
//        project.setReleaseInfo(release);
//
//        Project savedProject = projectRepository.save(project);
//        log.info("Project released: {}", projectId);
//        return projectMapper.toDto(savedProject);
//    }
//
//    private Project getProjectById(UUID projectId) {
//        return projectRepository.findById(projectId).orElseThrow(
//                () -> new ProjectException("Project with id " + projectId + " not found"));
//    }
//
//    private SubprojectAccount createProjectAccount(Project project, AccountProfile accountProfile, UUID projectId, UUID accountProfileId) {
//        ProjectAccountId projectAccountId = new ProjectAccountId(projectId, accountProfileId);
//        return SubprojectAccount.builder()
//                .id(projectAccountId)
//                .accountProfile(accountProfile)
//                .project(project)
//                .dateJoined(LocalDate.now())
//                .build();
//    }

}
