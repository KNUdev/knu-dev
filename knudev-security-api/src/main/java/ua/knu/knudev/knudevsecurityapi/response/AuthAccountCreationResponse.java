package ua.knu.knudev.knudevsecurityapi.response;

import lombok.Builder;
import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;

import java.util.Set;

@Builder
public record AuthAccountCreationResponse(
        String email,
        Set<AccountRole> roles
) {
}