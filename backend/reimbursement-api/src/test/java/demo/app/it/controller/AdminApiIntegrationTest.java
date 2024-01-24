package demo.app.it.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.model.*;
import demo.app.repository.EmployeeRepository;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;

import static demo.app.utils.CustomAuthoritiesFilter.timestamp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminApiIntegrationTest {
    private static HttpHeaders headers;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ReimbursementRepository reimbursementRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("Vy17CLE1Nom5BuaJfaaUztbGkElrNhF60U5wypY-nnQt1PmaIIfI02IrkeAgWZegsFu1A1g");
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateReimbursementWhenParameterIsValidAndReturn200HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setStatus(true);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);
        ResponseEntity<WebResponse<ReimbursementResponse>> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/239414077758111751/1", HttpMethod.PATCH, entity, new ParameterizedTypeReference<>() {
        });

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("Rp#,##0.00", symbols);

        WebResponse<ReimbursementResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(decimalFormat.format(BigDecimal.valueOf(1000.00)), result.getData().getAmount());
        assertEquals("Travel", result.getData().getActivity());
        assertEquals("Transport", result.getData().getTypeReimbursement());
        assertEquals("Transport to client", result.getData().getDescription());
        assertEquals(true, result.getData().getStatus());
        reimbursementRepository.findFirstByEmployeeAndReimbursementId(employeeRepository.findByClientId("239414077758111751").orElse(null), 1L).ifPresent(reimbursement -> {
            assertEquals(1L, reimbursement.getReimbursementId());
            assertEquals(1L, reimbursement.getEmployee().getEmployeeId());
            assertEquals("239414159781920775", reimbursement.getApprovedId());
            assertEquals("API FOR DEVELOPMENT", reimbursement.getApprovedName());
        });
        assertNull(result.getErrors());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateReimbursementWhenParameterIsInvalidAndReturn400HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setStatus(false);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);
        ResponseEntity<WebResponse<ReimbursementResponse>> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/239414077758111751/1", HttpMethod.PATCH, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<ReimbursementResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Status cannot be null or false", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testUpdateReimbursementWhenClientIdEmployeeInvalidAndReturn404HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setStatus(true);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);
        ResponseEntity<WebResponse<ReimbursementResponse>> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/9999/1", HttpMethod.PATCH, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<ReimbursementResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee is not found", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateReimbursementWhenReimbursementIdNotFoundAndReturn404HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setStatus(true);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);
        ResponseEntity<WebResponse<ReimbursementResponse>> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/239414077758111751/999", HttpMethod.PATCH, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<ReimbursementResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Reimbursement is not found", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testUpdateReimbursementWhenTokenNullAndReturn400HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setStatus(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/239414077758111751/999", HttpMethod.PATCH, entity, new ParameterizedTypeReference<>() {
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
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', true, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testDeleteReimbursementByClientIdAndReimbursementIdIsValidAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/239414077758111751/1", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", result.getData());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', true, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testDeleteReimbursementByClientIdNotValidAndReturn404HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/9999/1", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee is not found", result.getErrors());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', true, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testDeleteReimbursementByClientIdAndReimbursementIdNotValidAndReturn404HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/239414077758111751/999", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Reimbursement is not found", result.getErrors());
    }

    @Test
    void testDeleteReimbursementWhenTokenNullAndReturn401HttpStatus() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/239414077758111751/999", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
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
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', true, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetEmployeeWithClientIdIsValidAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/employees/239414077758111751", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", result.getData().getFullName());
        assertEquals("Laki-Laki", result.getData().getGender());
        employeeRepository.findByClientId("239414077758111751").ifPresent(employee -> assertEquals("PT Cinta Sejati", employee.getCompany()));
        assertEquals("Software Engineer", result.getData().getPosition());
        assertEquals("user@i2dev.com", result.getData().getEmail());
        assertEquals("089512611411", result.getData().getPhoneNumber());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', true, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetEmployeeWithClientIdNotValidAndReturn404HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/employees/999", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found for the given clientId 999", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (2, '239414077758111752', 'client@i2dev.com', 'Aden Wijaya', '0987654321', 'Laki-Laki', 'PT Melia Sejaterah', 'IT Support')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id IN ('239414077758111751', '239414077758111752')", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetAllEmployeesAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<List<EmployeeResponse>>> response = restTemplate.exchange(createURLWithPort() + "/employees/all", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<List<EmployeeResponse>> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, result.getData().size());
        // index 0
        assertEquals("239414077758111751", result.getData().get(0).getClientId());
        assertEquals("John Doe", result.getData().get(0).getFullName());
        employeeRepository.findByClientId("239414077758111751").ifPresent(employee -> assertEquals("PT Cinta Sejati", employee.getCompany()));
        // index 1
        assertEquals("239414077758111752", result.getData().get(1).getClientId());
        assertEquals("Aden Wijaya", result.getData().get(1).getFullName());
        employeeRepository.findByClientId("239414077758111752").ifPresent(employee -> assertEquals("PT Melia Sejaterah", employee.getCompany()));
    }

    @Test
    void testGetAllEmployeeWhenTokenNullAndReturn401HttpStatus() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/employees/all", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
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
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetReimbursementByStatusFalseAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<List<ReimbursementResponse>>> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/status", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<List<ReimbursementResponse>> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, result.getData().size());
        assertEquals(1, result.getData().get(0).getEmployeeId());
        assertEquals("Transport to client", result.getData().get(0).getDescription());
        assertEquals(false, result.getData().get(0).getStatus());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', true, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetReimbursementByStatusFalseIfNotHasFalseAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<List<ReimbursementResponse>>> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/status", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<List<ReimbursementResponse>> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, result.getData().size());
        assertThat(result.getData()).isEmpty();
    }

    @Test
    void testGetReimbursementByStatusFalseWhenTokenNullAndReturn401HttpStatus() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/reimbursements/status", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
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
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', true, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testDeleteEmployeeWithClientIdIsValidAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/employees/239414077758111751", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee with clientId 239414077758111751 has been removed", result.getData());
        assertNull(result.getErrors());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', true, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testDeleteEmployeeWithClientIdIsNotValidAndReturn404HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/employees/2394140777581117510", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });
        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found for the given clientId 2394140777581117510", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testDeleteEmployeeWithClientIdWhenTokenNullAndReturn401HttpStatus() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/employees/2394140777581117510", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });
        MessageResponse result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You are not authenticated to perform this operation", result.getErrors());
        assertEquals("Please log in to access the requested resource.", result.getMessage());
        assertEquals(timestamp, result.getTimestamp());
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/admin";
    }

}
