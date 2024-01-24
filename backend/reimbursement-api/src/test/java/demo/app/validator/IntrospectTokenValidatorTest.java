package demo.app.validator;

import demo.app.exception.ValidatorErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class IntrospectTokenValidatorTest {
    @Mock
    private Collection<GrantedAuthority> scopes;
    @Mock
    private IntrospectTokenValidator tokenValidator;

    @BeforeEach
    void setUp() {
        tokenValidator = new IntrospectTokenValidator();
    }

//    @Test
//    @Disabled
//    void testValidateTokenWithValidTokenAndValidScopes() {
//        Map<String, Object> token = new HashMap<>();
//        token.put("token", "validToken");
//        token.put("exp", Instant.now().plusSeconds(3600));
//        token.put("active", true);
//
//        Map<String, Map<String, String>> roles = new HashMap<>();
//        roles.put("user", Map.of("2131412", "base.localhost", "2131232541", "base.localhost"));
//        roles.put("admin", Map.of("12312412", "base.localhost", "2131241", "base.localhost"));
//        token.put("roles", roles);
//
//        scopes = new ArrayList<>();
//        scopes.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//
//        assertDoesNotThrow(() -> tokenValidator.validateToken(token, scopes));
//    }

    @Test
    void testValidateTokenWithExpiredToken() {
        Map<String, Object> token = new HashMap<>();
        token.put("exp", Instant.now().minusSeconds(3600));

        scopes = new ArrayList<>();

        assertThrows(ValidatorErrorHandler.class, () -> tokenValidator.validateToken(token, scopes));
    }

    @Test
    void testValidateTokenWithInvalidScopes() {
        Map<String, Object> token = new HashMap<>();
        token.put("exp", Instant.now().plusSeconds(3600));
        token.put("active", true);

        Map<String, Map<String, String>> roles = new HashMap<>();
        roles.put("user", Map.of("2131412", "base.localhost", "2131232541", "base.localhost"));
        roles.put("admin", Map.of("12312412", "base.localhost", "2131241", "base.localhost"));
        token.put("roles", roles);

        scopes = new ArrayList<>();
        scopes.add(new SimpleGrantedAuthority("ROLE_USER"));

        assertThrows(ValidatorErrorHandler.class, () -> tokenValidator.validateToken(token, scopes));
    }

    @Test
    void testValidateTokenWithRevokedToken() {
        Map<String, Object> token = new HashMap<>();
        token.put("exp", Instant.now().plusSeconds(3600));
        token.put("active", false);

        scopes = new ArrayList<>();

        assertThrows(ValidatorErrorHandler.class, () -> tokenValidator.validateToken(token, scopes));
    }
}