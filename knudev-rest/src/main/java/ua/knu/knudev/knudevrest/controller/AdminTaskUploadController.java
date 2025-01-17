package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.taskmanagerapi.api.TaskUploadAPI;

@RestController
@RequestMapping("/admin/task/upload")
@RequiredArgsConstructor
public class AdminTaskUploadController {

    private final TaskUploadAPI taskUploadAPI;

    @Operation(
            summary = "Upload task for role",
            description = """
                            This endpoint allows administrators to upload tasks associated with a specific role.
                            The role is passed as a path variable, and the task file is sent as a multipart file.
                            Example:
                            - Role: Intern
                            - File: task-details.pdf
                    """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Task successfully uploaded.",
                    content = @Content(
                            mediaType = "text/plain;charset=UTF-8",
                            schema = @Schema(type = "string", example = "Task uploaded successfully.")
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "You do not have access to this endpoint.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping(
            value = "/campus/{role}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "text/plain;charset=UTF-8"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadTaskForRole(
            @PathVariable("role")
            @Parameter(
                    name = "Account role",
                    description = "Current user's account role",
                    in = ParameterIn.HEADER,
                    example = "Intern"
            ) String accountRole,
            @RequestParam("taskFile") @Valid @NotNull
            @Parameter(
                    name = "Multipart file",
                    description = "File with task for user",
                    in = ParameterIn.HEADER
            ) MultipartFile taskFile) {
        return taskUploadAPI.uploadTaskForRole(accountRole, taskFile);
    }
}
