package ua.knu.knudev.knudevsecurity.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.knu.knudev.knudevcommon.constant.AccountAdministrativeRole;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.utils.JWTSigningKeyProvider;
import ua.knu.knudev.knudevsecurityapi.dto.Tokens;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JWTServiceTest {

    private static final AccountTechnicalRole TEST_TECHNICAL_ROLE = AccountTechnicalRole.DEVELOPER;
    private static final AccountAdministrativeRole TEST_ADMINISTRATIVE_ROLE = AccountAdministrativeRole.HEAD_MANAGER;
    private static final String TEST_EMAIL = "testEmail@knu.ua";
    private static final Integer accessTokenExpirationInMillis = 600000;
    private static final Integer refreshTokenExpirationInMillis = 1200000;
    private static final String TEST_ISSUER_NAME = "testIssuerName";
    private JWTService jwtService;
    private AccountAuth account;

    @BeforeEach
    public void setUp() {
        jwtService = new JWTService(
                accessTokenExpirationInMillis,
                refreshTokenExpirationInMillis,
                TEST_ISSUER_NAME,
                new JWTSigningKeyProvider());

        account = AccountAuth.builder()
                .email(TEST_EMAIL)
                .technicalRole(TEST_TECHNICAL_ROLE)
                .administrativeRole(TEST_ADMINISTRATIVE_ROLE)
                .build();
    }

    @Test
    @DisplayName("Should generate access and refresh tokens and validate them successfully")
    public void should_GenerateAndValidateTokens_When_TokensAreCreated() {
        Tokens tokens = jwtService.generateTokens(account);

        assertNotNull(tokens, "Tokens should not be null");
        assertNotNull(tokens.accessToken(), "Access token should not be null");
        assertNotNull(tokens.refreshToken(), "Refresh token should not be null");

        assertTrue(jwtService.isTokenValid(tokens.accessToken(), account), "Access token should be valid");
        assertTrue(jwtService.isTokenValid(tokens.refreshToken(), account), "Refresh token should be valid");
    }

    @Test
    @DisplayName("Should extract email from both access and refresh JWT tokens")
    public void should_ExtractEmailFromJWT_When_GivenValidTokens() {
        Tokens tokens = jwtService.generateTokens(account);

        String emailFromAccessToken = jwtService.extractEmail(tokens.accessToken());
        assertEquals(TEST_EMAIL, emailFromAccessToken, "Extracted email from access token should match");

        String emailFromRefreshToken = jwtService.extractEmail(tokens.refreshToken());
        assertEquals(TEST_EMAIL, emailFromRefreshToken, "Extracted email from refresh token should match");
    }

    @Test
    @DisplayName("Should extract account roles from access JWT token")
    public void should_ExtractAccountRoles_When_GivenValidTokens() {
        Tokens tokens = jwtService.generateTokens(account);

        Set<String> rolesFromAccessToken = jwtService.extractAccountRoles(tokens.accessToken());
        assertTrue(rolesFromAccessToken.contains("DEVELOPER"), "Access token should contain the 'INTERN' roles");
    }

    @Test
    @DisplayName("Should correctly identify access and refresh token types")
    public void should_CheckTokenTypes_When_GivenValidTokens() {
        Tokens tokens = jwtService.generateTokens(account);

        assertTrue(jwtService.isAccessToken(tokens.accessToken()), "Should recognize access token as access token");
        assertFalse(jwtService.isAccessToken(tokens.refreshToken()), "Should recognize refresh token as not an access token");
    }

    @Test
    @DisplayName("Should throw ExpiredJwtException when validating an expired access token")
    public void should_ThrowExpiredJwtException_When_TryToValidateWithExpiredToken() {
        JWTService shortLivedJwtService = new JWTService(
                100,
                100,
                TEST_ISSUER_NAME,
                new JWTSigningKeyProvider());

        Tokens tokens = shortLivedJwtService.generateTokens(account);

        assertThrows(ExpiredJwtException.class, () ->
                        shortLivedJwtService.isTokenValid(tokens.accessToken(), account),
                "Validating an expired access token should throw ExpiredJwtException");

        assertThrows(ExpiredJwtException.class, () ->
                        shortLivedJwtService.isTokenValid(tokens.refreshToken(), account),
                "Validating an expired refresh token should throw ExpiredJwtException");
    }

    @Test
    @DisplayName("Should return false when validating a tampered JWT token")
    public void should_ReturnNonValidToken_When_Given_InvalidJWT() {
        Tokens tokens = jwtService.generateTokens(account);

        String tamperedToken = tokens.accessToken() + "tampered";

        assertFalse(jwtService.isTokenValid(tamperedToken, account), "Tampered token should be invalid");
    }

    @Test
    @DisplayName("Should throw JwtException when extracting claims from an invalid JWT token")
    public void should_ThrowJwtException_When_TryToExtractClaimWithInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertThrows(JwtException.class, () -> jwtService.extractEmail(invalidToken),
                "Extracting email from an invalid token should throw JwtException");
    }

    @Test
    @DisplayName("Should invalidate token when used by a different user")
    public void should_BeValidToken_When_GivenDifferentUser() {
        Tokens tokens = jwtService.generateTokens(account);

        AccountAuth differentUser = AccountAuth.builder()
                .email("Another" + TEST_EMAIL)
                .technicalRole(AccountTechnicalRole.TECHLEAD)
                .administrativeRole(AccountAdministrativeRole.SITE_MANAGER)
                .build();

        assertFalse(jwtService.isTokenValid(tokens.accessToken(), differentUser),
                "Token should be invalid for a different user");
    }
}
