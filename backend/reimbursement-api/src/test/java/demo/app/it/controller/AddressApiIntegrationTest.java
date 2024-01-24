package demo.app.it.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.entity.Address;
import demo.app.model.AddressRequest;
import demo.app.model.AddressResponse;
import demo.app.model.MessageResponse;
import demo.app.model.WebResponse;
import demo.app.repository.AddressRepository;
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
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddressApiIntegrationTest {
    private static HttpHeaders headers;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("CE2ay3qGKuNyew6H04OvRnvqwAx7uezMO5DTVfPlIDk58PBWVhFq_d9zZQnQbREO7nFp57A");
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateCurrentAddressAndReturn200HttpStatus() throws JsonProcessingException {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("Jl Majapahit No. 15");
        addressRequest.setCity("Jakarta");
        addressRequest.setCountry("Indonesia");
        addressRequest.setProvince("DKI Jakarta");
        addressRequest.setPostalCode("12345");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(addressRequest), headers);

        ResponseEntity<WebResponse<AddressResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<AddressResponse> result = response.getBody();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test", result.getData().getAddressId());
        assertEquals("Jakarta", result.getData().getCity());
        assertEquals("Jl Majapahit No. 15", result.getData().getStreet());
        assertEquals("Indonesia", result.getData().getCountry());
        assertEquals("DKI Jakarta", result.getData().getProvince());
        assertEquals("12345", result.getData().getPostalCode());
        Optional<Address> optionalAddress = addressRepository.findFirstByAddressId("test");
        optionalAddress.map(Address::getStreet)
                .ifPresent(street -> assertEquals(result.getData().getStreet(), street));
        assertNull(result.getErrors());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateAddressWhenAddressNotFoundAndReturn404HttpStatus() throws JsonProcessingException {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("Jl Majapahit No. 15");
        addressRequest.setCity("Jakarta");
        addressRequest.setCountry("Indonesia");
        addressRequest.setProvince("DKI Jakarta");
        addressRequest.setPostalCode("12345");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(addressRequest), headers);

        ResponseEntity<WebResponse<AddressResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<AddressResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Address not found for the employee", result.getErrors());
        assertNull(result.getData());
    }

    @Test
    void testUpdateAddressWhenTokenNullAndReturn401HttpStatus() throws JsonProcessingException {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("Jl Majapahit No. 15");
        addressRequest.setCity("Jakarta");
        addressRequest.setCountry("Indonesia");
        addressRequest.setProvince("DKI Jakarta");
        addressRequest.setPostalCode("12345");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(addressRequest), headers);

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
        return "http://localhost:" + port + "/api/address";
    }
}
