package ua.knu.knudev.knudevsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;

import java.util.UUID;

public interface AccountAuthRepository extends JpaRepository<AccountAuth, UUID> {
    AccountAuth findAccountAuthByEmail(String email);
}