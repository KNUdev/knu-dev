package ua.knu.knudevapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ua.knu.knudev.knudevcommon.config.CommonConfig;
import ua.knu.knudev.knudevliquibase.config.LiquibaseConfig;
import ua.knu.knudev.knudevrest.config.RestConfig;
import ua.knu.knudev.knudevsecurity.config.SecurityConfig;
import ua.knu.knudev.knudevsecurityapi.config.SecurityApiConfig;
import ua.knu.knudev.taskmanager.config.TaskManagerConfig;
import ua.knu.knudev.taskmanagerapi.config.TaskManagerApiConfig;
import ua.knu.knudev.teammanager.config.TeamManagerConfig;
import ua.knu.knudev.teammanagerapi.config.TeamManagerApiConfig;

@SpringBootApplication
@Import({CommonConfig.class, LiquibaseConfig.class, RestConfig.class, SecurityConfig.class, SecurityApiConfig.class,
        TaskManagerConfig.class, TaskManagerApiConfig.class, TeamManagerConfig.class, TeamManagerApiConfig.class})
public class KnudevApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnudevApplication.class, args);
    }

}
