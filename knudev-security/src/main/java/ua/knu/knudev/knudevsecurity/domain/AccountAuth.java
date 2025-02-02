package ua.knu.knudev.knudevsecurity.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.knu.knudev.knudevcommon.constant.AccountRole;
import ua.knu.knudev.knudevcommon.constant.AccountAdministrativeRole;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Table(schema = "security_management", name = "auth_account")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountAuth implements Serializable, UserDetails {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, updatable = false, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "technical_role", nullable = false)
    private AccountTechnicalRole technicalRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "administrative_role")
    private AccountAdministrativeRole administrativeRole;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "is_non_locked", nullable = false)
    private boolean nonLocked;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of(technicalRole, administrativeRole)
                .filter(Objects::nonNull)
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return nonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public Set<AccountRole> getRoles() {
        return new HashSet<>(Set.of(technicalRole, administrativeRole));
    }
}
