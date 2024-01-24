package demo.app.utils;

import demo.app.validator.IntrospectTokenValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthoritiesOpaqueTokenIntrospectorTest {
    private static final String INTROSPECTION_URL = "https://server.example.com";
    private static final String CLIENT_ID = "client";
    private static final String CLIENT_SECRET = "secret";
    @Mock
    private AuthorityExtractor authorityExtractor;
    @Mock
    private IntrospectTokenValidator introspectTokenValidator;
    @InjectMocks
    private CustomAuthoritiesOpaqueTokenIntrospector customAuthoritiesOpaqueTokenIntrospector;

//    @Test
//    @Disabled
//    void testIntrospectWhenValidTokenThenReturnPrincipal() {
//        // Arrange
//        String token = "validToken";
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("attribute1", "value1");
//        OAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal("name", attributes, Collections.emptyList());
//
//        OpaqueTokenIntrospector introspectionClient = new NimbusOpaqueTokenIntrospector(INTROSPECTION_URL, CLIENT_ID, CLIENT_SECRET);
//        OAuth2AuthenticatedPrincipal result;
//        try {
//            result = introspectionClient.introspect(token);
//        } catch (OAuth2IntrospectionException e) {
//            System.out.println("Error occurred during token introspection: " + e.getMessage());
//            return;
//        }
//
//        assertEquals(principal, result);
//        assertThat(principal.getAttributes().get("attribute1"), equalTo("value1"));
//        verify(introspectTokenValidator, times(1)).validateToken(principal.getAttributes(), authorityExtractor.extractAuthorities(principal));
//    }

    @Test
    void testIntrospectWhenInvalidTokenThenThrowException() {
        String token = "invalidToken";

        assertThrows(RuntimeException.class, () -> customAuthoritiesOpaqueTokenIntrospector.introspect(token));
        verify(introspectTokenValidator, never()).validateToken(any(), any());
    }
}
