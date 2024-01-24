package demo.app.service;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.repository.EmployeeRepository;
import demo.app.repository.ReimbursementRepository;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ReimbursementServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ReimbursementRepository reimbursementRepository;
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private OAuth2AuthenticatedPrincipal principal;
    @InjectMocks
    private ReimbursementService reimbursementService;
    private ReimbursementRequest reimbursementRequest;
    private Employee employee;
    private Reimbursement reimbursement;

    @BeforeEach
    void setUp() {
        reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(1000.00));
        reimbursementRequest.setActivity("Travel");
        reimbursementRequest.setTypeReimbursement("Transport");
        reimbursementRequest.setDescription("Travel to client location");
        reimbursementRequest.setStatus(false);

        employee = new Employee();
        employee.setFullName("John Doe");
        employee.setClientId("123");

        reimbursement = new Reimbursement();
        reimbursement.setEmployee(employee);
        reimbursement.setAmount(reimbursementRequest.getAmount());
        reimbursement.setActivity(reimbursementRequest.getActivity());
        reimbursement.setTypeReimbursement(reimbursementRequest.getTypeReimbursement());
        reimbursement.setDescription(reimbursementRequest.getDescription());
        reimbursement.setStatus(reimbursementRequest.getStatus());
        reimbursement.setDateCreated(LocalDateTime.now());
    }

    @Test
    void testCreateWhenAllParametersAreValidThenReturnReimbursementResponse() {
        given((principal.getAttributes())).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));
        given(reimbursementRepository.save(any(Reimbursement.class))).willReturn(reimbursement);

        ReimbursementResponse reimbursementResponse = reimbursementService.create(reimbursementRequest, principal);

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(1)).save(any(Reimbursement.class));

        assertNotNull(reimbursementResponse);
        assertEquals(reimbursement.getReimbursementId(), reimbursementResponse.getReimbursementId());
        assertEquals(reimbursement.getStatus(), reimbursementResponse.getStatus());
        assertEquals(reimbursementService.convertRupiah(reimbursement.getAmount()), reimbursementResponse.getAmount());
        assertEquals(reimbursement.getActivity(), reimbursementResponse.getActivity());
        assertEquals(reimbursement.getTypeReimbursement(), reimbursementResponse.getTypeReimbursement());
        assertEquals(reimbursement.getDescription(), reimbursementResponse.getDescription());
    }

    @Test
    void testCreateWhenReimbursementRequestIsInvalidThenThrowIllegalArgumentException() {
        reimbursementRequest.setAmount(null);

        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reimbursementService.create(reimbursementRequest, principal));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Amount cannot be zero or null", exception.getReason());

        verify(validationHelper, times(1)).validate(reimbursementRequest);
    }

    @Test
    void testCreateWhenEmployeeIsNotFoundThenThrowResponseStatusException() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reimbursementService.create(reimbursementRequest, principal));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Employee not found for the given clientId 123", exception.getReason());

        verify(validationHelper, times(1)).validate(reimbursementRequest);
    }

    @Test
    void testUpdateReimbursementUserWhenAllParametersAreValidThenReturnReimbursementResponse() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));
        given(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).willReturn(Optional.of(reimbursement));
        given(reimbursementRepository.save(any(Reimbursement.class))).willReturn(reimbursement);

        ReimbursementResponse reimbursementResponse = reimbursementService.updateReimbursementUser(1L, reimbursementRequest, principal);

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(1)).save(any(Reimbursement.class));
        assertNotNull(reimbursementResponse);
        assertEquals(reimbursement.getReimbursementId(), reimbursementResponse.getReimbursementId());
        assertEquals(reimbursement.getStatus(), reimbursementResponse.getStatus());
        assertEquals(reimbursementService.convertRupiah(reimbursement.getAmount()), reimbursementResponse.getAmount());
        assertEquals(reimbursement.getActivity(), reimbursementResponse.getActivity());
        assertEquals(reimbursement.getTypeReimbursement(), reimbursementResponse.getTypeReimbursement());
        assertEquals(reimbursement.getDescription(), reimbursementResponse.getDescription());

    }

    @Test
    void testUpdateReimbursementByUserWhenAmountIsZeroThenThrowResponseStatusException() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));
        given(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).willReturn(Optional.of(reimbursement));

        reimbursementRequest.setAmount(BigDecimal.ZERO);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reimbursementService.updateReimbursementUser(1L, reimbursementRequest, principal));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Amount cannot be zero", exception.getReason());
    }

    @Test
    void testUpdateReimbursementByAdminWhenAllParametersAreValidThenReturnReimbursementResponse() {
        reimbursementRequest.setStatus(true);

        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));
        given(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), (anyLong()))).willReturn(Optional.of(reimbursement));
        given(reimbursementRepository.save(any(Reimbursement.class))).willReturn(reimbursement);

        ReimbursementResponse reimbursementResponse = reimbursementService.updateReimbursementByAdmin("123", 1L, reimbursementRequest, principal);

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(1)).save(any(Reimbursement.class));
        assertNotNull(reimbursementResponse);

        assertEquals(reimbursement.getReimbursementId(), reimbursementResponse.getReimbursementId());
        assertEquals(reimbursement.getStatus(), reimbursementResponse.getStatus());
    }

    @Test
    void testUpdateReimbursementByAdminWhenClientIdIsInvalidThenThrowResponseStatusException() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reimbursementService.updateReimbursementByAdmin("123", 1L, reimbursementRequest, principal));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Employee is not found", exception.getReason());

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(0)).save(any(Reimbursement.class));
    }

    @Test
    void testUpdateReimbursementByAdminWhenStatusIsInvalidThenThrowResponseStatusException() {
        reimbursementRequest.setStatus(null);

        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));
        given(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), (anyLong()))).willReturn(Optional.of(reimbursement));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reimbursementService.updateReimbursementByAdmin("123", 1L, reimbursementRequest, principal));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Status cannot be null or false", exception.getReason());

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(0)).save(any(Reimbursement.class));
    }

    @Test
    void testUpdateReimbursementWhenClientIdIsInvalidThenThrowResponseStatusException() {
        given(principal.getAttributes()).willReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reimbursementService.updateReimbursementUser(1L, reimbursementRequest, principal));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Client ID not found", exception.getReason());
    }

    @Test
    void testRemoveReimbursementByAdminWhenAllParametersAreValidThenNoReturn() {
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));
        given(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).willReturn(Optional.of(reimbursement));

        reimbursementService.removeReimbursementByAdmin("123", 1L);

        verify(reimbursementRepository, times(1)).delete(any(Reimbursement.class));
    }

    @Test
    void testRemoveReimbursementByUserWhenAllParametersAreValidThenNoReturn() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));
        given(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).willReturn(Optional.of(reimbursement));

        reimbursementService.removeReimbursementByUser(1L, principal);

        verify(reimbursementRepository, times(1)).delete(any(Reimbursement.class));
    }

    @Test
    void testGetReimbursementsWithStatusFalseWhenStatusIsFalseThenReturnReimbursementResponseList() {
        given(reimbursementRepository.findByStatusFalse()).willReturn(Collections.singletonList(reimbursement));

        List<ReimbursementResponse> reimbursementResponses = reimbursementService.getReimbursementsWithStatusFalse();

        verify(reimbursementRepository, times(1)).findByStatusFalse();
        assertNotNull(reimbursementResponses);
        assertFalse(reimbursementResponses.isEmpty());

        assertEquals(reimbursement.getReimbursementId(), reimbursementResponses.get(0).getReimbursementId());
        assertEquals(reimbursement.getStatus(), reimbursementResponses.get(0).getStatus());
        assertEquals(reimbursement.getActivity(), reimbursementResponses.get(0).getActivity());
        assertEquals(reimbursement.getTypeReimbursement(), reimbursementResponses.get(0).getTypeReimbursement());
        assertEquals(reimbursement.getDescription(), reimbursementResponses.get(0).getDescription());
        assertEquals(reimbursementService.convertRupiah(reimbursement.getAmount()), reimbursementResponses.get(0).getAmount());
        assertEquals(reimbursement.getApprovedId(), reimbursementResponses.get(0).getApprovedId());
        assertEquals(reimbursement.getApprovedName(), reimbursementResponses.get(0).getApprovedName());
        assertEquals(reimbursement.getEmployee().getEmployeeId(), reimbursementResponses.get(0).getEmployeeId());
    }

    @Test
    void testToReimbursementResponseWhenReimbursementIsValidThenReturnReimbursementResponse() {
        ReimbursementResponse reimbursementResponse = reimbursementService.toReimbursementResponse(reimbursement);

        assertNotNull(reimbursementResponse);
        assertEquals(reimbursement.getReimbursementId(), reimbursementResponse.getReimbursementId());
        assertEquals(reimbursement.getStatus(), reimbursementResponse.getStatus());
        assertEquals(reimbursementService.convertRupiah(reimbursement.getAmount()), reimbursementResponse.getAmount());
        assertEquals(reimbursement.getActivity(), reimbursementResponse.getActivity());
        assertEquals(reimbursement.getTypeReimbursement(), reimbursementResponse.getTypeReimbursement());
        assertEquals(reimbursement.getDescription(), reimbursementResponse.getDescription());
    }

    @Test
    void testConvertRupiahWhenBigDecimalValueIsValidThenReturnString() {
        String result = reimbursementService.convertRupiah(BigDecimal.valueOf(1000));

        assertNotNull(result);
        assertTrue(result.contains("Rp"));
    }
}