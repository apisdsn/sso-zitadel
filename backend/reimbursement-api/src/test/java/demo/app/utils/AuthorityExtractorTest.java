package demo.app.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthorityExtractorTest {
    @Mock
    private OAuth2AuthenticatedPrincipal principal;
    private AuthorityExtractor authorityExtractor;

    @BeforeEach
    void setUp() {
        authorityExtractor = new AuthorityExtractor();
        authorityExtractor.ROLES_ATTRIBUTE = "roles";
    }

    @Test
    void testExtractAuthoritiesWhenScopesAndUserAuthoritiesPresentThenReturnAuthorities() {
        List<String> scopes = Arrays.asList("scope1", "scope2");
        Map<String, Map<String, String>> userAuthorities = new HashMap<>();
        userAuthorities.put("role1", new HashMap<>());
        userAuthorities.put("role2", new HashMap<>());

        when(principal.getAttribute(OAuth2TokenIntrospectionClaimNames.SCOPE)).thenReturn(scopes);
        when(principal.getAttribute("roles")).thenReturn(userAuthorities);

        Collection<GrantedAuthority> authorities = authorityExtractor.extractAuthorities(principal);

        assertThat(authorities).hasSize(4);
    }

    @Test
    void testExtractAuthoritiesWhenScopesNotPresentThenReturnAuthorities() {
        Map<String, Map<String, String>> userAuthorities = new HashMap<>();
        userAuthorities.put("role1", new HashMap<>());
        userAuthorities.put("role2", new HashMap<>());

        when(principal.getAttribute(OAuth2TokenIntrospectionClaimNames.SCOPE)).thenReturn(null);
        when(principal.getAttribute("roles")).thenReturn(userAuthorities);

        Collection<GrantedAuthority> authorities = authorityExtractor.extractAuthorities(principal);

        assertThat(authorities).hasSize(2);
    }

    @Test
    void testExtractAuthoritiesWhenUserAuthoritiesNotPresentThenThrowException() {
        List<String> scopes = Arrays.asList("scope1", "scope2");

        when(principal.getAttribute(OAuth2TokenIntrospectionClaimNames.SCOPE)).thenReturn(scopes);
        when(principal.getAttribute("roles")).thenReturn(null);

        assertThatThrownBy(() -> authorityExtractor.extractAuthorities(principal))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User does not have any assigned roles.");
    }
}
