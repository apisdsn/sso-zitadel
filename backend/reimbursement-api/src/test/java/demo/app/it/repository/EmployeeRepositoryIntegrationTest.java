package demo.app.it.repository;


import demo.app.entity.Employee;
import demo.app.repository.EmployeeRepository;
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
public class EmployeeRepositoryIntegrationTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @Sql(statements = "DELETE FROM employees WHERE client_id  = '239414077758111753'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testCreateEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeId(3L);
        employee.setClientId("239414077758111753");
        employee.setEmail("wHr0e@example.com");
        employee.setFullName("Asep Jajang");
        employee.setPhoneNumber("089512611413");
        employee.setGender("Laki-Laki");
        employee.setCompany("PT Tandon Air Widjaya");
        employee.setPosition("CEO");

        Employee savedEmployee = employeeRepository.save(employee);

        assertEquals(1, employeeRepository.count());
        Optional<Employee> findEmployee = employeeRepository.findByClientId(savedEmployee.getClientId());
        assertTrue(findEmployee.isPresent());
        findEmployee.ifPresent(result -> assertEquals("Asep Jajang", result.getFullName()));
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (2, '239414077758111752', 'dadang@i2dev.com', 'Dadang Kornelo', '089512611412', 'Laki-Laki', 'PT Melia Sejaterah', 'Account Executive')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id IN ('239414077758111751', '239414077758111752')", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindAllEmployee() {
        List<Employee> employee = employeeRepository.findAll();
        assertEquals(2, employee.size());
        // index 0
        assertEquals(1, employee.get(0).getEmployeeId());
        Optional<Employee> employeeOptionalIndexZero = employeeRepository.findByClientId("239414077758111751");
        employeeOptionalIndexZero.ifPresent(result -> assertEquals("John Doe", result.getFullName()));
        assertEquals("user@i2dev.com", employee.get(0).getEmail());
        // index 1
        assertEquals(2, employee.get(1).getEmployeeId());
        Optional<Employee> employeeOptionalIndexOne = employeeRepository.findByClientId("239414077758111752");
        employeeOptionalIndexOne.ifPresent(result -> assertEquals("Dadang Kornelo", result.getFullName()));
        assertEquals("dadang@i2dev.com", employee.get(1).getEmail());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (2, '239414077758111752', 'dadang@i2dev.com', 'Dadang Kornelo', '089512611412', 'Laki-Laki', 'PT Melia Sejaterah', 'Account Executive')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id IN ('239414077758111751', '239414077758111752')", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testExistsByClientId() {
        assertTrue(employeeRepository.existsByClientId("239414077758111751"));
        assertTrue(employeeRepository.existsByClientId("239414077758111752"));
        assertFalse(employeeRepository.existsByClientId("239414077758111753"));
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindByClientId() {
        Optional<Employee> employeeOptional = employeeRepository.findByClientId("239414077758111751");
        employeeOptional.ifPresent(result -> {
            assertEquals("John Doe", result.getFullName());
            assertEquals("user@i2dev.com", result.getEmail());
            assertEquals("Laki-Laki", result.getGender());
            assertEquals("PT Cinta Sejati", result.getCompany());
        });
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testDeleteEmployee() {
        Optional<Employee> employeeOptional = employeeRepository.findByClientId("239414077758111751");
        assertTrue(employeeOptional.isPresent(), "Employee with client ID 239414077758111751 not found");

        Employee employee = employeeOptional.get();
        employeeRepository.delete(employee);

        assertFalse(employeeRepository.existsByClientId("239414077758111751"), "Employee with client ID 239414077758111751 still exists");
        assertEquals(0, employeeRepository.count(), "Employee count is not zero");
        assertEquals(0, employeeRepository.findAll().size(), "Employee list is not empty");
    }
}
