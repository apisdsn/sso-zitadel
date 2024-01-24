package demo.app.it.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.entity.Employee;
import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.model.MessageResponse;
import demo.app.model.WebResponse;
import demo.app.repository.EmployeeRepository;
import demo.app.service.EmployeeService;
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

import java.util.Collections;
import java.util.Optional;

import static demo.app.utils.CustomAuthoritiesFilter.timestamp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeApiIntegrationTest {
    private static HttpHeaders headers;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("CE2ay3qGKuNyew6H04OvRnvqwAx7uezMO5DTVfPlIDk58PBWVhFq_d9zZQnQbREO7nFp57A");
    }

    @Test
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testRegisterEmployeeWhenValidParameterAndReturn201HttpStatus() throws JsonProcessingException {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("John Doe");
        employeeRequest.setGender("Laki-Laki");
        employeeRequest.setCompany("PT Cinta Sejati");
        employeeRequest.setPosition("Software Engineer");
        employeeRequest.setPhoneNumber("081234567890");
        employeeRequest.setProvince("Indonesia");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(employeeRequest), headers);

        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/register", HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Data has been stored in the database", result.getData());
        assertNull(result.getErrors());
    }

    @Test
    void testRegisterEmployeeWhenFullNameBlankAndReturn400HttpStatus() throws JsonProcessingException {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(employeeRequest), headers);
        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/register", HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Full name cannot be blank", result.getErrors());
    }

    @Test
    void testRegisterEmployeeWhenTokenNullAndReturn401HttpStatus() throws JsonProcessingException {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("John Doe");
        employeeRequest.setGender("Laki-Laki");
        employeeRequest.setCompany("PT Cinta Sejati");
        employeeRequest.setPosition("Software Engineer");
        employeeRequest.setPhoneNumber("081234567890");
        employeeRequest.setProvince("Indonesia");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(employeeRequest), headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/register", HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
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
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetCurrentEmployeeWhenEmployeeHasRegisterAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, result.getData().getEmployeeId());
        assertEquals("239414077758111751", result.getData().getClientId());
        assertEquals("user@i2dev.com", result.getData().getEmail());
        assertEquals("John Doe", result.getData().getFullName());
        assertEquals("089512611411", result.getData().getPhoneNumber());
        assertEquals("Laki-Laki", result.getData().getGender());
        assertEquals("PT Cinta Sejati", result.getData().getCompany());
        assertEquals("Software Engineer", result.getData().getPosition());
        assertEquals("Laki-Laki", result.getData().getGender());
        assertEquals("239414077758111751", employeeService.getByClientId("239414077758111751").getClientId());
        Optional<Employee> employeeOptional = employeeRepository.findByClientId("239414077758111751");
        employeeOptional.map(Employee::getFullName)
                .ifPresent(fullName -> assertEquals(result.getData().getFullName(), fullName));
        assertThat(result.getData().getReimbursements()).isEmpty();
        assertNull(result.getData().getAddress());
        assertNull(result.getErrors());
    }

    @Test
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetCurrentEmployeeWhenEmployeeNotRegisteredAndReturn404HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found for the given clientId 239414077758111751", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testGetCurrentEmployeeWhenTokenNullAndReturn401HttpStatus() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
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
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateCurrentWhenEmployeeHasRegisterAndReturn200HttpStatus() throws JsonProcessingException {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("Alex Benjamin");
        employeeRequest.setPhoneNumber("089512611412");
        employeeRequest.setGender("Laki-Laki");
        employeeRequest.setCompany("PT Melia Sejaterah");
        employeeRequest.setPosition("Driver");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(employeeRequest), headers);

        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, result.getData().getEmployeeId());
        assertEquals("239414077758111751", result.getData().getClientId());
        assertEquals("user@i2dev.com", result.getData().getEmail());
        assertEquals("Alex Benjamin", result.getData().getFullName());
        assertEquals("089512611412", result.getData().getPhoneNumber());
        assertEquals("Laki-Laki", result.getData().getGender());
        assertEquals("PT Melia Sejaterah", result.getData().getCompany());
        assertEquals("Driver", result.getData().getPosition());
        assertEquals("Laki-Laki", result.getData().getGender());
        assertEquals("239414077758111751", employeeService.getByClientId("239414077758111751").getClientId());
        Optional<Employee> employeeOptional = employeeRepository.findByClientId("239414077758111751");
        employeeOptional.map(Employee::getFullName)
                .ifPresent(fullName -> assertEquals(result.getData().getFullName(), fullName));
        assertThat(result.getData().getReimbursements()).isEmpty();
        assertNull(result.getData().getAddress());
        assertNull(result.getErrors());
    }

    @Test
    void testUpdateCurrentWhenEmployeeNotRegisteredAndReturn404HttpStatus() throws JsonProcessingException {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("Alex Benjamin");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(employeeRequest), headers);

        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found for the given clientId 239414077758111751", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateCurrentEmployeeWhenFullNameBlankAndReturn400HttpStatus() throws JsonProcessingException {
        EmployeeRequest employeeRequest = new EmployeeRequest();

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(employeeRequest), headers);

        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Request cannot be null and Full name cannot be blank or empty", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testUpdateCurrentEmployeeWhenTokenNullAndReturn401HttpStatus() throws JsonProcessingException {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("Alex Benjamin");
        employeeRequest.setPhoneNumber("089512611412");
        employeeRequest.setGender("Laki-Laki");
        employeeRequest.setCompany("PT Melia Sejaterah");
        employeeRequest.setPosition("Driver");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(employeeRequest), headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
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
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testRemoveCurrentWhenEmployeeHasRegisteredAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data has been removed from the database", result.getData());
        assertNull(result.getErrors());
    }

    @Test
    void testRemoveCurrentWhenEmployeeNotRegisteredAndReturn404HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found for the given clientId 239414077758111751", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testRemoveCurrentWhenTokenNullAndReturn401HttpStatus() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<MessageResponse> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });

        MessageResponse result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You are not authenticated to perform this operation", result.getErrors());
        assertEquals("Please log in to access the requested resource.", result.getMessage());
        assertEquals(timestamp, result.getTimestamp());
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/employees";
    }
}