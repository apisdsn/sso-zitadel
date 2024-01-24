package demo.app.repository;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
public class ReimbursementRepositoryTest {
    @Mock
    private ReimbursementRepository reimbursementRepository;
    private Employee employee;
    private Reimbursement reimbursement;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setClientId("123");
        employee.setFullName("John Doe");
        employee.setEmail("john.doe@example.com");

        reimbursement = new Reimbursement();
        reimbursement.setEmployee(employee);
        reimbursement.setAmount(new BigDecimal("100.00"));
        reimbursement.setStatus(false);
        reimbursement.setActivity("Travel");
    }
    
    @Test
    void testFindFirstByEmployeeAndReimbursementIdWhenExistsThenReturnReimbursement() {
        given(reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursement.getReimbursementId())).willReturn(Optional.of(reimbursement));

        Optional<Reimbursement> foundReimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursement.getReimbursementId());

        assertTrue(foundReimbursement.isPresent());
        assertEquals(reimbursement.getReimbursementId(), foundReimbursement.get().getReimbursementId());
        verify(reimbursementRepository, times(1)).findFirstByEmployeeAndReimbursementId(employee, reimbursement.getReimbursementId());
    }

    @Test
    void testFindFirstByEmployeeAndReimbursementIdWhenNonExistentIdThenReturnEmptyOptional() {
        given(reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, Long.MAX_VALUE)).willReturn(Optional.empty());

        Optional<Reimbursement> foundReimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, Long.MAX_VALUE);

        assertFalse(foundReimbursement.isPresent());
        verify(reimbursementRepository, times(1)).findFirstByEmployeeAndReimbursementId(employee, Long.MAX_VALUE);
    }

    @Test
    void testFindFirstByEmployeeAndReimbursementIdWhenNonExistentEmployeeThenReturnEmptyOptional() {
        Employee nonExistentEmployee = new Employee();
        nonExistentEmployee.setFullName("Non Existent");
        nonExistentEmployee.setEmail("non.existent@example.com");

        Optional<Reimbursement> foundReimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(nonExistentEmployee, reimbursement.getReimbursementId());

        assertFalse(foundReimbursement.isPresent());
        verify(reimbursementRepository, times(1)).findFirstByEmployeeAndReimbursementId(nonExistentEmployee, reimbursement.getReimbursementId());
    }

    @Test
    void testFindByStatusFalseWhenExistsThenReturnReimbursements() {
        given(reimbursementRepository.findByStatusFalse()).willReturn(List.of(reimbursement));

        List<Reimbursement> foundReimbursements = reimbursementRepository.findByStatusFalse();

        assertFalse(foundReimbursements.isEmpty());
        assertTrue(foundReimbursements.contains(reimbursement));
        verify(reimbursementRepository, times(1)).findByStatusFalse();
    }

    @Test
    void testFindByStatusFalseWhenNonExistentThenReturnEmptyList() {
        given(reimbursementRepository.findByStatusFalse()).willReturn(Collections.emptyList());

        List<Reimbursement> foundReimbursements = reimbursementRepository.findByStatusFalse();

        assertTrue(foundReimbursements.isEmpty());
        verify(reimbursementRepository, times(1)).findByStatusFalse();
    }
}