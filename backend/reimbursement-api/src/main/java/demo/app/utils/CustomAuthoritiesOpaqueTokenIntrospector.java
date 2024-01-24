package demo.app.utils;

import demo.app.validator.IntrospectTokenValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;


@Slf4j
@Configuration
@Component
public class CustomAuthoritiesOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    @Autowired
    private AuthorityExtractor authorityExtractor;

    @Autowired
    private IntrospectTokenValidator introspectTokenValidator;

    @Value("${spring.security.oauth2.opaque-token.introspection-uri}")
    private String INTROSPECT_URI;

    @Value("${spring.security.oauth2.opaque-token.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.opaque-token.client-secret}")
    private String CLIENT_SECRET;

    private OpaqueTokenIntrospector delegate() {
        return new NimbusOpaqueTokenIntrospector(INTROSPECT_URI, CLIENT_ID, CLIENT_SECRET);
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        log.info("Token Introspect: {}", token);
        OAuth2AuthenticatedPrincipal principal = delegate().introspect(token);
        introspectTokenValidator.validateToken(principal.getAttributes(), authorityExtractor.extractAuthorities(principal));
        return new DefaultOAuth2AuthenticatedPrincipal(principal.getName(), principal.getAttributes(), authorityExtractor.extractAuthorities(principal));
    }
}