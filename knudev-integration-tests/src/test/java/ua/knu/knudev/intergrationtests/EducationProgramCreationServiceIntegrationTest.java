package ua.knu.knudev.intergrationtests;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.education.repository.EducationProgramRepository;
import ua.knu.knudev.education.repository.ModuleRepository;
import ua.knu.knudev.education.repository.SectionRepository;
import ua.knu.knudev.education.repository.TopicRepository;
import ua.knu.knudev.education.repository.bridge.ModuleTopicMappingRepository;
import ua.knu.knudev.education.repository.bridge.ProgramSectionMappingRepository;
import ua.knu.knudev.education.repository.bridge.SectionModuleMappingRepository;
import ua.knu.knudev.education.service.EducationProgramCreationService;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.dto.ProgramSectionDto;
import ua.knu.knudev.educationapi.dto.ProgramTopicDto;
import ua.knu.knudev.educationapi.exception.EducationProgramException;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
class EducationProgramCreationServiceIntegrationTest {

    @Autowired
    private EducationProgramCreationService creationService;

    @Autowired
    private EducationProgramRepository programRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ProgramSectionMappingRepository programSectionMappingRepository;

    @Autowired
    private SectionModuleMappingRepository sectionModuleMappingRepository;

    @Autowired
    private ModuleTopicMappingRepository moduleTopicMappingRepository;

    private MultipartFile mockPdfFile(String baseName) {
        return new MockMultipartFile(
                baseName,
                baseName + ".pdf",
                "application/pdf",
                "dummy content".getBytes()
        );
    }

    private MultiLanguageFieldDto multiLang(String en, String uk) {
        return MultiLanguageFieldDto.builder()
                .en(en)
                .uk(uk)
                .build();
    }

    @AfterEach
    public void tearDown() {
        moduleTopicMappingRepository.deleteAll();
        sectionModuleMappingRepository.deleteAll();
        programSectionMappingRepository.deleteAll();

        topicRepository.deleteAll();
        moduleRepository.deleteAll();
        sectionRepository.deleteAll();
        programRepository.deleteAll();
    }

    @Test
    @DisplayName("1) Create minimal program (no sections) => success")
    void testCreateMinimalProgram_noSections() {
        EducationProgramCreationRequest request = EducationProgramCreationRequest.builder()
                .name(multiLang("Program EN", "Program UK"))
                .description(multiLang("Desc EN", "Desc UK"))
                .expertise(Expertise.BACKEND)
                .finalTask(mockPdfFile("ProgramFinal"))
                .build();

        EducationProgramDto result = creationService.save(request);

        assertThat(result).isNotNull();
        assertThat(result.getName().getEn()).isEqualTo("Program EN");
        assertThat(result.getFinalTaskUrl()).isNotNull();
        assertThat(programRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("2) Create program with 1 new section => success")
    void testCreateProgram_oneSection_noModules() {
        SectionCreationRequest sectionReq = SectionCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Section1 EN", "Секція1 УК"))
                .description(multiLang("SectionDesc EN", "СекціяОпис УК"))
                .finalTask(mockPdfFile("SectionFinal"))
                .finalTestId(UUID.randomUUID())
                .build();

        EducationProgramCreationRequest request = EducationProgramCreationRequest.builder()
                .name(multiLang("ProgramEN", "ПрограмаUK"))
                .description(multiLang("DescEN", "ОписUK"))
                .expertise(Expertise.BACKEND)
                .finalTask(mockPdfFile("ProgramFinalTask"))
                .sections(List.of(sectionReq))
                .build();

        EducationProgramDto result = creationService.save(request);

        assertThat(result.getSections()).hasSize(1);
        ProgramSectionDto sectionDto = result.getSections().get(0);
        assertThat(sectionDto.getName().getEn()).isEqualTo("Section1 EN");
        assertThat(sectionDto.getFinalTaskUrl()).isNotNull();
        assertThat(sectionDto.getModules()).isEmpty();
        assertThat(sectionRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("3) Create program with 1 section => 1 module => no topics => success")
    void testCreateProgram_sectionWithOneModuleNoTopics() {
        ModuleCreationRequest moduleReq = ModuleCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Module1 EN", "Модуль1 УК"))
                .description(multiLang("ModDesc EN", "МодОпис УК"))
                .finalTask(mockPdfFile("ModuleFinal"))
                .testId(UUID.randomUUID()) // required by your validator
                .build();

        SectionCreationRequest sectionReq = SectionCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Section1 EN", "Секція1 УК"))
                .description(multiLang("SecDesc EN", "СекціяОпис УК"))
                .finalTask(mockPdfFile("SectionFinal"))
                .finalTestId(UUID.randomUUID())
                .modules(List.of(moduleReq))
                .build();

        EducationProgramCreationRequest request = EducationProgramCreationRequest.builder()
                .name(multiLang("ProgramEN", "ПрограмаУК"))
                .description(multiLang("ProgDescEN", "ПрограмаОписУК"))
                .expertise(Expertise.BACKEND)
                .finalTask(mockPdfFile("ProgramFinal"))
                .sections(List.of(sectionReq))
                .build();

        EducationProgramDto result = creationService.save(request);

        assertThat(result.getSections()).hasSize(1);
        assertThat(result.getSections().get(0).getModules()).hasSize(1);
        assertThat(result.getSections().get(0).getModules().get(0).getTopics()).isEmpty();
        assertThat(moduleRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("4) Create program with multiple sections, modules, topics => success")
    void testCreateProgram_multiSectionModuleTopic() {
        TopicCreationRequest topic1 = TopicCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Topic1 EN", "Тема1 УК"))
                .description(multiLang("Topic1 Desc EN", "Опис Тема1 УК"))
                .task(mockPdfFile("Topic1Task"))
                .learningMaterials(Set.of("https://example1.com"))
                .testIds(Set.of(UUID.randomUUID()))
                .build();

        TopicCreationRequest topic2 = TopicCreationRequest.builder()
                .orderIndex(2)
                .name(multiLang("Topic2 EN", "Тема2 УК"))
                .description(multiLang("Topic2 Desc EN", "Опис Тема2 УК"))
                .task(mockPdfFile("Topic2Task"))
                .learningMaterials(Set.of("https://example2.com"))
                .testIds(Set.of(UUID.randomUUID()))
                .build();

        ModuleCreationRequest module1 = ModuleCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Module1 EN", "Модуль1 УК"))
                .description(multiLang("Module1 Desc EN", "Модуль1 Опис УК"))
                .finalTask(mockPdfFile("Module1Final"))
                .testId(UUID.randomUUID())
                .topics(List.of(topic1, topic2))
                .build();

        ModuleCreationRequest module2 = ModuleCreationRequest.builder()
                .orderIndex(2)
                .name(multiLang("Module2 EN", "Модуль2 УК"))
                .description(multiLang("Module2 Desc EN", "Модуль2 Опис УК"))
                .finalTask(mockPdfFile("Module2Final"))
                .testId(UUID.randomUUID())
                .topics(Collections.emptyList())  // no topics
                .build();

        SectionCreationRequest section1 = SectionCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Section1 EN", "Секція1 УК"))
                .description(multiLang("Sec1Desc EN", "Секція1 Опис УК"))
                .finalTask(mockPdfFile("Section1Final"))
                .finalTestId(UUID.randomUUID())
                .modules(List.of(module1, module2))
                .build();

        SectionCreationRequest section2 = SectionCreationRequest.builder()
                .orderIndex(2)
                .name(multiLang("Section2 EN", "Секція2 УК"))
                .description(multiLang("Sec2Desc EN", "Секція2 Опис УК"))
                .finalTask(mockPdfFile("Section2Final"))
                .finalTestId(UUID.randomUUID())
                .modules(Collections.emptyList())
                .build();

        EducationProgramCreationRequest request = EducationProgramCreationRequest.builder()
                .name(multiLang("ProgramMulti EN", "Програма Мульти УК"))
                .description(multiLang("Full desc EN", "Повний опис УК"))
                .expertise(Expertise.BACKEND)
                .finalTask(mockPdfFile("ProgramFinal"))
                .sections(List.of(section1, section2))
                .build();

        EducationProgramDto result = creationService.save(request);

        assertThat(result.getSections()).hasSize(2);
        assertThat(result.getSections().get(0).getModules()).hasSize(2);
        assertThat(result.getSections().get(0).getModules().get(0).getTopics()).hasSize(2);
    }

    @Test
    @DisplayName("5) Fail if order indexes are invalid (skipping numbers)")
    void testInvalidOrderIndexes() {
        SectionCreationRequest section1 = SectionCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Sec1 EN", "Sec1 UK"))
                .description(multiLang("Desc1 EN", "Desc1 UK"))
                .finalTask(mockPdfFile("Section1"))
                .finalTestId(UUID.randomUUID())
                .build();

        SectionCreationRequest section2 = SectionCreationRequest.builder()
                .orderIndex(3)  // skipping 2
                .name(multiLang("Sec3 EN", "Sec3 UK"))
                .description(multiLang("Desc3 EN", "Desc3 UK"))
                .finalTask(mockPdfFile("Section3"))
                .finalTestId(UUID.randomUUID())
                .build();

        EducationProgramCreationRequest request = EducationProgramCreationRequest.builder()
                .name(multiLang("ProgramEN", "ProgramUK"))
                .description(multiLang("DescEN", "DescUK"))
                .expertise(Expertise.BACKEND)
                .finalTask(mockPdfFile("ProgFinal"))
                .sections(List.of(section1, section2))
                .build();

        assertThatThrownBy(() -> creationService.save(request))
                .isInstanceOf(EducationProgramException.class)
                .hasMessageContaining("Invalid order indexes");
    }

    @Test
    @DisplayName("6) Fail if modules in the same section have duplicate order indexes")
    void testDuplicateOrderIndexes() {
        ModuleCreationRequest mod1 = ModuleCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Module1", "Модуль1"))
                .description(multiLang("Desc1", "Опис1"))
                .finalTask(mockPdfFile("Mod1Final"))
                .testId(UUID.randomUUID())
                .build();

        ModuleCreationRequest mod2 = ModuleCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Module2", "Модуль2"))
                .description(multiLang("Desc2", "Опис2"))
                .finalTask(mockPdfFile("Mod2Final"))
                .testId(UUID.randomUUID())
                .build();

        SectionCreationRequest section1 = SectionCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Sec1", "Секція1"))
                .description(multiLang("Desc Sec1", "Опис Сек1"))
                .finalTask(mockPdfFile("Sec1Final"))
                .finalTestId(UUID.randomUUID())
                .modules(List.of(mod1, mod2))
                .build();

        EducationProgramCreationRequest request = EducationProgramCreationRequest.builder()
                .name(multiLang("Prog EN", "Prog UK"))
                .description(multiLang("ProgDesc EN", "ProgDesc UK"))
                .expertise(Expertise.BACKEND)
                .finalTask(mockPdfFile("ProgFinal"))
                .sections(List.of(section1))
                .build();

        assertThatThrownBy(() -> creationService.save(request))
                .isInstanceOf(EducationProgramException.class)
                .hasMessageContaining("Duplicate orderIndex found");
    }

    @Test
    @DisplayName("7) Update existing program => only existingProgramId is non-null, everything else is null")
    void testUpdateExistingProgram_addSectionsNotAllowed() {
        EducationProgramCreationRequest createReq = EducationProgramCreationRequest.builder()
                .name(multiLang("Old Program EN", "Стара Програма"))
                .description(multiLang("Old Desc EN", "Старий Опис"))
                .expertise(Expertise.BACKEND)
                .finalTask(mockPdfFile("InitialProgFinal"))
                .build();
        EducationProgramDto initialDto = creationService.save(createReq);
        UUID existingId = initialDto.getId();

        SectionCreationRequest newSection = SectionCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Should fail", "Повинно впасти"))
                .description(multiLang("desc", "опис"))
                .finalTask(mockPdfFile("SecFinal"))
                .finalTestId(UUID.randomUUID())
                .build();

        EducationProgramCreationRequest updateReq = EducationProgramCreationRequest.builder()
                .existingProgramId(existingId)
                .sections(List.of(newSection))
                .build();

        assertThatThrownBy(() -> creationService.save(updateReq))
                .isInstanceOf(ConstraintViolationException.class)
        ;
    }

    @Test
    @DisplayName("8) Missing mandatory fields => fail with ConstraintViolationException")
    void testMissingMandatoryFields() {
        EducationProgramCreationRequest invalidRequest = EducationProgramCreationRequest.builder()
                .name(multiLang("Missing Task EN", "Відсутній Файл УК"))
                .description(multiLang("SomeDesc EN", "Деякий Опис УК"))
                .expertise(Expertise.BACKEND)
                .build();

        assertThatThrownBy(() -> creationService.save(invalidRequest))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("All fields except existingProgramId must be provided");
    }

    @Test
    @DisplayName("9) Create program with topic that has learning resources => success")
    void testCreateProgram_withTopicLearningResources() {
        TopicCreationRequest topic = TopicCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Topic LR EN", "Тема LR УК"))
                .description(multiLang("Desc LR EN", "Опис LR УК"))
                .task(mockPdfFile("TopicTask"))
                .learningMaterials(Set.of("https://res1.com", "https://res2.com"))
                .testIds(Set.of(UUID.randomUUID()))
                .build();

        ModuleCreationRequest module = ModuleCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Module LR EN", "Модуль LR УК"))
                .description(multiLang("Desc LR EN", "Опис LR УК"))
                .finalTask(mockPdfFile("ModuleFinal"))
                .testId(UUID.randomUUID())
                .topics(List.of(topic))
                .build();

        SectionCreationRequest section = SectionCreationRequest.builder()
                .orderIndex(1)
                .name(multiLang("Section LR EN", "Секція LR УК"))
                .description(multiLang("SecDesc LR EN", "Опис Секції LR УК"))
                .finalTask(mockPdfFile("SectionFinal"))
                .finalTestId(UUID.randomUUID())
                .modules(List.of(module))
                .build();

        EducationProgramCreationRequest request = EducationProgramCreationRequest.builder()
                .name(multiLang("Prog LR EN", "Програма LR УК"))
                .description(multiLang("Desc LR EN", "Опис LR УК"))
                .expertise(Expertise.BACKEND)
                .finalTask(mockPdfFile("ProgramFinal"))
                .sections(List.of(section))
                .build();

        EducationProgramDto result = creationService.save(request);

        ProgramTopicDto createdTopic = result.getSections().get(0)
                .getModules().get(0)
                .getTopics().get(0);
        assertThat(createdTopic.getLearningResources()).contains("https://res1.com", "https://res2.com");
    }

    //todo fails
    @Test
    @DisplayName("10) Create a larger program with multiple sections => success if all fields are provided")
    void testCreateProgram_largeScenario() {
        int sectionsCount = 5;
        int modulesPerSection = 5;
        int topicsPerModule = 9;

        List<SectionCreationRequest> sections = new ArrayList<>();
        for (int s = 1; s <= sectionsCount; s++) {
            List<ModuleCreationRequest> modules = new ArrayList<>();
            for (int m = 1; m <= modulesPerSection; m++) {
                List<TopicCreationRequest> topics = new ArrayList<>();
                for (int t = 1; t <= topicsPerModule; t++) {
                    topics.add(TopicCreationRequest.builder()
                            .orderIndex(t)
                            .name(multiLang("Topic " + s + "-" + m + "-" + t, "Тема " + s + "-" + m + "-" + t))
                            .description(multiLang("Desc " + s + "-" + m + "-" + t, "Опис " + s + "-" + m + "-" + t))
                            .task(mockPdfFile("Topic_" + s + "_" + m + "_" + t))
                            .learningMaterials(Set.of("res://" + s + "-" + m + "-" + t))
                            .testIds(Set.of(UUID.randomUUID()))
                            .build());
                }
                modules.add(ModuleCreationRequest.builder()
                        .orderIndex(m)
                        .name(multiLang("Module " + s + "-" + m, "Модуль " + s + "-" + m))
                        .description(multiLang("ModDesc " + s + "-" + m, "ОписМ " + s + "-" + m))
                        .finalTask(mockPdfFile("ModuleFinal_" + s + "_" + m))
                        .testId(UUID.randomUUID())
                        .topics(topics)
                        .build());
            }
            sections.add(SectionCreationRequest.builder()
                    .orderIndex(s)
                    .name(multiLang("Section " + s, "Секція " + s))
                    .description(multiLang("SecDesc " + s, "СекОпис " + s))
                    .finalTask(mockPdfFile("SecFinal_" + s))
                    .finalTestId(UUID.randomUUID())
                    .modules(modules)
                    .build());
        }

        EducationProgramCreationRequest request = EducationProgramCreationRequest.builder()
                .name(multiLang("Big Program", "Велика Програма"))
                .description(multiLang("Some big desc", "Деякий великий опис"))
                .expertise(Expertise.BACKEND)
                .finalTask(mockPdfFile("BigProgFinal"))
                .sections(sections)
                .build();

        EducationProgramDto result = creationService.save(request);

        assertThat(result.getSections()).hasSize(sectionsCount);
        for (int s = 0; s < sectionsCount; s++) {
            ProgramSectionDto sec = result.getSections().get(s);
            assertThat(sec.getModules()).hasSize(modulesPerSection);
            for (int m = 0; m < modulesPerSection; m++) {
                assertThat(sec.getModules().get(m).getTopics()).hasSize(topicsPerModule);
            }
        }
    }
}
