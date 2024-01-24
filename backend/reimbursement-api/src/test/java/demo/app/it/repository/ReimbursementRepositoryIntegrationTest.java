package demo.app.it.repository;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import demo.app.repository.EmployeeRepository;
import demo.app.repository.ReimbursementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReimbursementRepositoryIntegrationTest {
    @Autowired
    private ReimbursementRepository reimbursementRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindAllReimbursement() {
        List<Reimbursement> reimbursement = reimbursementRepository.findAll();
        assertEquals(1, reimbursement.size());
        assertEquals(1, reimbursement.get(0).getReimbursementId());
        assertNull(reimbursement.get(0).getApprovedId());
        assertNull(reimbursement.get(0).getApprovedName());
        assertEquals("Travel", reimbursement.get(0).getActivity());
        assertEquals("Transport", reimbursement.get(0).getTypeReimbursement());
        assertEquals("Transport to client", reimbursement.get(0).getDescription());
        assertEquals("1000.00", reimbursement.get(0).getAmount().toString());
        assertEquals(false, reimbursement.get(0).getStatus());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindFirstByEmployeeAndReimbursementId() {
        Optional<Employee> employeeOptional = employeeRepository.findByClientId("239414077758111751");
        assertTrue(employeeOptional.isPresent());

        Optional<Reimbursement> findReimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employeeOptional.get(), 1L);
        assertTrue(findReimbursement.isPresent());
        findReimbursement.ifPresent(result -> assertEquals("Travel", result.getActivity()));
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 20000.00, 'Travel', 'Transport', 'Transport to Client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (2, 1, null, null, 50000.00, 'Data', 'Lain - Lain', 'Meet with Client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindByStatusFalse() {
        List<Reimbursement> findReimbursement = reimbursementRepository.findByStatusFalse();
        assertNotNull(findReimbursement);
        assertEquals(2, findReimbursement.size());
        assertEquals("Transport to Client", findReimbursement.get(0).getDescription());
        assertEquals("Travel", findReimbursement.get(0).getActivity());
        assertEquals("Meet with Client", findReimbursement.get(1).getDescription());
        assertEquals("Data", findReimbursement.get(1).getActivity());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES (1, 1, null, null, 1000.00, 'Travel', 'Transport', 'Transport to client', false, null, null)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testDeleteReimbursement() {
        Reimbursement deletedReimbursement = reimbursementRepository.findById(1L).orElse(null);
        assertNotNull(deletedReimbursement);
        reimbursementRepository.delete(deletedReimbursement);
        assertEquals(0, reimbursementRepository.count());
    }
}