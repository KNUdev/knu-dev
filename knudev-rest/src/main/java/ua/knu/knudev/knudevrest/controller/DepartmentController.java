package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.DepartmentApi;
import ua.knu.knudev.teammanagerapi.dto.ShortDepartmentDto;
import ua.knu.knudev.teammanagerapi.dto.ShortSpecialtyDto;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentApi departmentApi;

    @Operation(
            summary = "Get list of departments",
            description = "This endpoint retrieves a list of short department details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of departments.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShortDepartmentDto.class, type = "array")
                    ))
    })
    @GetMapping("/departments")
    public Set<ShortDepartmentDto> getShortDepartments() {
        return departmentApi.getShortDepartments();
    }

    @Operation(
            summary = "Get specialties by department",
            description = "This endpoint retrieves a list of specialties for a given department based on its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the specialties for the department.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShortSpecialtyDto.class, type = "array")
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Department not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @GetMapping("/departments/{departmentId}/specialties")
    public Set<ShortSpecialtyDto> getSpecialtiesByDepartment(
            @PathVariable
            @Parameter(name = "Department id", description = "The unique ID of the department", required = true,
                    example = "f47ac10b-58cc-4372-a567-0e02b2c3d479", in = ParameterIn.HEADER) UUID departmentId) {
        return departmentApi.getSpecialtiesByDepartmentId(departmentId);
    }

}
