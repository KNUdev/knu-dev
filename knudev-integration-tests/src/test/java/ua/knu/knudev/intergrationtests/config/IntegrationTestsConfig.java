package ua.knu.knudev.intergrationtests.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "ua.knu.knudev.teammanager",
        "ua.knu.knudev.knudevsecurity",
        "ua.knu.knudev.fileservice",
        "ua.knu.knudev.taskmanager",
        "ua.knu.knudev.intergrationtests"
})
public class IntegrationTestsConfig {

}
