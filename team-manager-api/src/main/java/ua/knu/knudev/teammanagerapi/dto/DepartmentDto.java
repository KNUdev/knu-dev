package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record DepartmentDto(
        UUID id,
        String name,
        Set<SpecialtyCreationDto> specialties
) {
}
