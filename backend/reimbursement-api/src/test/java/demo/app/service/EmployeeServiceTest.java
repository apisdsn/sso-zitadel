package demo.app.service;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.model.ReimbursementResponse;
import demo.app.repository.EmployeeRepository;
import demo.app.validator.ValidationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class EmployeeServiceTest {
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ReimbursementService reimbursementService;
    @Mock
    private OAuth2AuthenticatedPrincipal principal;
    @InjectMocks
    private EmployeeService employeeService;
    private EmployeeRequest employeeRequest;
    private Employee employee;


    @BeforeEach
    void setUp() {
        employeeRequest = new EmployeeRequest();
        employee = new Employee();
        employeeRequest.setFullName("test");
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setAmount(BigDecimal.valueOf(1000.00));
        reimbursement.setDescription("Expense reimbursement");
        employee.setReimbursements(new ArrayList<>(Collections.singletonList(reimbursement)));
    }

    @Test
    void testCreateEmployeeWhenValidRequestThenEmployeeCreated() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123", "email", "test@test.com"));
        given(employeeRepository.existsByClientId(anyString())).willReturn(false);

        employeeService.register(employeeRequest, principal);
        verify(validationHelper, times(1)).validate(employeeRequest);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testCreateEmployeeWhenFullNameBlankInvalidRequestThenBadRequest() {
        employeeRequest.setFullName("");
        given(principal.getAttributes()).willReturn(Map.of("sub", "123", "email", "test@test.com"));
        given(employeeRepository.existsByClientId(anyString())).willReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.register(employeeRequest, principal));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Full name cannot be blank", exception.getReason());

        verify(validationHelper, times(1)).validate(employeeRequest);
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }

    @Test
    void testCreateEmployeeWhenNullRequestThenBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.register(null, principal));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Request cannot be null", exception.getReason());

        verify(validationHelper, times(0)).validate(employeeRequest);
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }

    @Test
    void testCreateEmployeeWhenExistingClientIdThenBadRequest() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123", "email", "test@test.com"));
        given(employeeRepository.existsByClientId(anyString())).willReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.register(employeeRequest, principal));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Employee already exists", exception.getReason());

        verify(validationHelper, times(1)).validate(employeeRequest);
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }

    @Test
    void testGetEmployeeByClientIdWhenValidThenEmployeeReturned() {
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getByClientId(anyString());

        assertNotNull(response);
        assertEquals(employee.getFullName(), response.getFullName());
        assertEquals(employee.getEmail(), response.getEmail());

        verify(employeeRepository, times(1)).findByClientId(anyString());
    }

    @Test
    void testGetEmployeeCurrentWhenValidThenEmployeeReturned() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123", "email", "test@test.com"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));
        EmployeeResponse response = employeeService.getCurrent(principal);

        assertEquals(employee.getFullName(), response.getFullName());
        assertEquals(employee.getEmail(), response.getEmail());

        verify(employeeRepository, times(1)).findByClientId(anyString());
    }

    @Test
    void testGetIfAttributesIsNullReturnNotFound() {
        given(principal.getAttributes()).willReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.getCurrent(principal));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Client ID not found", exception.getReason());

        verify(principal, times(1)).getAttributes();
    }

    @Test
    void testGetEmployeeByIdWhenInvalidClientIdThenNotFound() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.getCurrent(principal));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Employee not found for the given clientId 123", exception.getReason());

        verify(employeeRepository, times(1)).findByClientId(anyString());
    }

    @Test
    void testGetAllEmployeesThenAllEmployeesReturned() {
        given(employeeRepository.findAll()).willReturn(List.of(employee));
        given(reimbursementService.toReimbursementResponse(any())).willReturn(new ReimbursementResponse());

        List<EmployeeResponse> employeeResponses = employeeService.findAllEmployee();

        assertNotNull(employeeResponses);
        assertEquals(1, employeeResponses.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testUpdateEmployeeWhenValidRequestThenEmployeeUpdated() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));
        given(reimbursementService.toReimbursementResponse(any())).willReturn(new ReimbursementResponse());

        EmployeeResponse employeeResponse = employeeService.update(employeeRequest, principal);

        assertNotNull(employeeResponse);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployeeWhenNullRequestThenBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.update(null, principal));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Request cannot be null and Full name cannot be blank or empty", exception.getReason());
    }

    @Test
    void testDeleteEmployeeWhenValidClientIdThenEmployeeDeleted() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));

        employeeService.removeCurrent(principal);

        verify(employeeRepository, times(1)).delete(any(Employee.class));
    }

    @Test
    void testDeleteEmployeeWhenInvalidClientIdThenNotFound() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.removeCurrent(principal));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Employee not found for the given clientId 123", exception.getReason());

        verify(employeeRepository, times(1)).findByClientId(anyString());
        verify(employeeRepository, times(0)).delete(any(Employee.class));
    }

    @Test
    void testIfEmployeeExistsByClientIdWhenNullClientIdThenBadRequest() {
        given(employeeRepository.existsByClientId(null)).willReturn(false);
        boolean exists = employeeRepository.existsByClientId(null);
        assertFalse(exists);
    }

    @Test
    void testDeleteByClientIdWhenValidClientIdThenEmployeeDelete() {
        Employee employee = new Employee();
        employee.setClientId("123");
        employee.setFullName("John Doe");
        employee.setEmail("john.doe@example.com");

        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));

        employeeService.removeByClientId("123");

        verify(employeeRepository, times(1)).findByClientId("123");
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void testDeleteByClientIdWhenValidClientIdIfEmployeeNotExistsThenNotFound() {
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.removeByClientId("123"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Employee not found for the given clientId 123", exception.getReason());

        verify(employeeRepository, times(1)).findByClientId("123");
        verify(employeeRepository, times(0)).delete(any(Employee.class));
    }

    @Test
    void testDeleteEmployeeByClientIdWhenNullThenNotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.removeByClientId(null));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Employee not found for the given clientId null", exception.getReason());

        verify(employeeRepository, times(0)).delete(any(Employee.class));
    }
}