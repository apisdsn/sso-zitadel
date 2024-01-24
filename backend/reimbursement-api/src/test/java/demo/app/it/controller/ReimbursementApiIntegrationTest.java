package demo.app.it.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.entity.Reimbursement;
import demo.app.model.MessageResponse;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.model.WebResponse;
import demo.app.repository.ReimbursementRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static demo.app.utils.CustomAuthoritiesFilter.timestamp;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReimbursementApiIntegrationTest {
    private static HttpHeaders headers;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ReimbursementRepository reimbursementRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("CE2ay3qGKuNyew6H04OvRnvqwAx7uezMO5DTVfPlIDk58PBWVhFq_d9zZQnQbREO7nFp57A");
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testCreateReimbursementWhenAllParametersAreValidAndReturn201HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(1000.00));
        reimbursementRequest.setActivity("Travel");
        reimbursementRequest.setTypeReimbursement("Transport");
        reimbursementRequest.setDescription("Travel to client location");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);

        ResponseEntity<WebResponse<ReimbursementResponse>> response = restTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<ReimbursementResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Rp1.000,00", result.getData().getAmount());
        assertEquals("Travel", result.getData().getActivity());
        assertEquals("Transport", result.getData().getTypeReimbursement());
        assertEquals("Travel to client location", result.getData().getDescription());
        assertEquals(false, result.getData().getStatus());
        List<Reimbursement> optionalReimbursement = reimbursementRepository.findByStatusFalse();
        boolean isDescriptionFound = optionalReimbursement.stream()
                .map(Reimbursement::getDescription)
                .anyMatch(description -> description.equals(result.getData().getDescription()));
        assertTrue(isDescriptionFound);
        assertNull(result.getErrors());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testCreateReimbursementWhenMissingRequiredFieldsAndReturn400HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(0));
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);

        ResponseEntity<WebResponse<ReimbursementResponse>> response = restTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<ReimbursementResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Amount cannot be zero or null", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testCreateReimbursementWhenTokenNullAndReturn401HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(1000.00));
        reimbursementRequest.setActivity("Travel");
        reimbursementRequest.setTypeReimbursement("Transport");
        reimbursementRequest.setDescription("Travel to client location");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });
        MessageResponse result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You are not authenticated to perform this operation", result.getErrors());
        assertEquals("Please log in to access the requested resource.", result.getMessage());
        assertEquals(timestamp, result.getTimestamp());
    }

    @Test
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateReimbursementWhenAllParametersAreValidAndReturn200HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(5000.00));
        reimbursementRequest.setActivity("Travel");
        reimbursementRequest.setTypeReimbursement("Transport");
        reimbursementRequest.setDescription("Travel to client location");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);

        ResponseEntity<WebResponse<ReimbursementResponse>> response = restTemplate.exchange(createURLWithPort() + "/1", HttpMethod.PATCH, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<ReimbursementResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rp5.000,00", result.getData().getAmount());
        assertEquals("Travel", result.getData().getActivity());
        assertEquals("Transport", result.getData().getTypeReimbursement());
        assertEquals("Travel to client location", result.getData().getDescription());
        assertEquals(false, result.getData().getStatus());
        List<Reimbursement> optionalReimbursement = reimbursementRepository.findByStatusFalse();
        boolean isDescriptionFound = optionalReimbursement.stream()
                .map(Reimbursement::getDescription)
                .anyMatch(description -> description.equals(result.getData().getDescription()));
        assertTrue(isDescriptionFound);
        assertNull(result.getErrors());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateReimbursementWhenReimbursementIdNotFoundAndReturn404HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(50000.00));
        reimbursementRequest.setActivity("Data");
        reimbursementRequest.setTypeReimbursement("Lain - Lain");
        reimbursementRequest.setDescription("Data for call client");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);

        ResponseEntity<WebResponse<ReimbursementResponse>> response = restTemplate.exchange(createURLWithPort() + "/999", HttpMethod.PATCH, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<ReimbursementResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Reimbursement is not found", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testUpdateReimbursementWhenTokenNullAndReturn401HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(50000.00));
        reimbursementRequest.setActivity("Data");
        reimbursementRequest.setTypeReimbursement("Lain - Lain");
        reimbursementRequest.setDescription("Data for call client");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort(), HttpMethod.PATCH, entity, new ParameterizedTypeReference<>() {
        });
        MessageResponse result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You are not authenticated to perform this operation", result.getErrors());
        assertEquals("Please log in to access the requested resource.", result.getMessage());
        assertEquals(timestamp, result.getTimestamp());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testDeleteReimbursementAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/1", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reimbursement deleted", result.getData());
        assertNull(result.getErrors());
    }

    @Test
    void testDeleteReimbursementWhenReimbursementIdNotFoundAndReturn404HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/999", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Reimbursement is not found", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testDeleteReimbursementWhenTokenNullAndReturn401HttpStatus() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/1", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });
        MessageResponse result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You are not authenticated to perform this operation", result.getErrors());
        assertEquals("Please log in to access the requested resource.", result.getMessage());
        assertEquals(timestamp, result.getTimestamp());
    }

    @Test
    void testEmployeeGetAccessAdminEndpointAndReturn403HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange("http://127.0.0.1:" + port + "/api/admin/employees/all", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        MessageResponse result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You do not have access to the requested resource. Access denied.", result.getErrors());
        assertEquals("Make sure you have the appropriate role or contact the system administrator for further assistance.", result.getMessage());
        assertEquals(timestamp, result.getTimestamp());
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/reimbursements";
    }
}