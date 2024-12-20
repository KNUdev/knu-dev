package ua.knu.knudev.teammanagerapi.api;

import jakarta.validation.Valid;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

import java.util.UUID;

public interface AccountProfileApi {
    AccountRegistrationResponse register(@Valid AccountCreationRequest registrationRequest);
    AccountProfileDto getById(UUID id);
    AccountProfileDto getByEmail(String email);
    boolean existsByEmail(String email);
    void assertEmailExists(String email) throws AccountException;
}
