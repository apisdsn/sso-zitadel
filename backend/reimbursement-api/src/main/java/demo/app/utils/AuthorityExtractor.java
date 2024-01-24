package demo.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class AuthorityExtractor {
    public static final String ROLE_PREFIX = "ROLE_";

    @Value("${zitadel.iam.org.project.roles-attribute}")
    public String ROLES_ATTRIBUTE;

    public Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
        List<String> scopes = getScopesOrDefault(principal);
        List<String> userAuthorities = getUserAuthorities(principal);

        log.info("Scopes: {}", scopes);
        log.info("User Authorities: {}", userAuthorities);

        List<String> allAuthorities = Stream.concat(scopes.stream(), userAuthorities.stream())
                .collect(Collectors.toList());
        log.info("All Authorities: {}", allAuthorities);

        return allAuthorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private List<String> getScopesOrDefault(OAuth2AuthenticatedPrincipal principal) {
        List<String> scopes = principal.getAttribute(OAuth2TokenIntrospectionClaimNames.SCOPE);
        return scopes != null ? scopes : Collections.emptyList();
    }

    private List<String> getUserAuthorities(OAuth2AuthenticatedPrincipal principal) {
        Map<String, Map<String, String>> projectRoles = principal.getAttribute(ROLES_ATTRIBUTE);
        if (projectRoles == null || projectRoles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have any assigned roles.");
        }
        return projectRoles.keySet().stream().map(role -> ROLE_PREFIX + role.toUpperCase()).toList();
    }
}
