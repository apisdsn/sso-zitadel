package demo.app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.model.MessageResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class CustomAuthoritiesFilter extends GenericFilterBean {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String timestamp = LocalDateTime.now().format(formatter);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String requestedPath = request.getRequestURI();
        Map<String, List<String>> rolePathsMap = new LinkedHashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: {}", authentication);
        
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal principal) {
            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
            List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();

            log.info("Roles: {}", roles);

            if (requestedPath.startsWith("/api/admin") || requestedPath.startsWith("/api/**")) {
                rolePathsMap.put("ROLE_ADMIN", List.of(requestedPath));
                rolePathsMap.put("ROLE_MANAGER", List.of(requestedPath));
            } else if (requestedPath.startsWith("/api/employees") || requestedPath.startsWith("/api/address") || requestedPath.startsWith("/api/reimbursements")) {
                rolePathsMap.put("ROLE_USER", List.of(requestedPath));
            } else {
                throw new ServletException("Invalid path or insufficient privileges. Requested path: " + requestedPath);
            }

            boolean isPathAllowed = roles.stream()
                    .anyMatch(role -> rolePathsMap.getOrDefault(role, Collections.emptyList()).stream()
                            .anyMatch(requestedPath::startsWith));

            log.info("isPathAllowed : {} ", isPathAllowed);

            if (isPathAllowed) {
                filterChain.doFilter(request, response);
            } else {
                setForbidden(response);
            }
        } else {
            setUnauthorized(response);
        }
    }

    private void sendResponse(HttpServletResponse response, int statusCode, String error, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = new MessageResponse();
        response.setStatus(statusCode);
        PrintWriter writer = response.getWriter();

        messageResponse.setErrors(error);
        messageResponse.setMessage(message);
        messageResponse.setTimestamp(timestamp);

        writer.println(objectMapper.writeValueAsString(messageResponse));
        writer.flush();
    }

    private void setUnauthorized(HttpServletResponse response) throws IOException {
        sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                "You are not authenticated to perform this operation",
                "Please log in to access the requested resource.");
    }

    private void setForbidden(HttpServletResponse response) throws IOException {
        sendResponse(response, HttpServletResponse.SC_FORBIDDEN,
                "You do not have access to the requested resource. Access denied.",
                "Make sure you have the appropriate role or contact the system administrator for further assistance.");
    }
}
