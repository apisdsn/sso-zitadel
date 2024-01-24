package demo.app.it.repository;

import demo.app.entity.Address;
import demo.app.repository.AddressRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AddressRepositoryIntegrationTest {
    @Autowired
    private AddressRepository addressRepository;

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindAllAddress() {
        List<Address> address = addressRepository.findAll();
        assertEquals(1, address.size());
        assertEquals("test", address.get(0).getAddressId());
        assertEquals("JL Kenangan No. 1", address.get(0).getStreet());
        assertEquals("Surabaya", address.get(0).getCity());
        assertEquals("Jawa Timur", address.get(0).getProvince());
        assertEquals("Indonesia", address.get(0).getCountry());
        assertEquals("12332", address.get(0).getPostalCode());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindFirstById() {
        Optional<Address> address = addressRepository.findFirstByAddressId("test");
        address.ifPresent(result -> {
            assertEquals("test", result.getAddressId());
            assertEquals("JL Kenangan No. 1", result.getStreet());
            assertEquals("Surabaya", result.getCity());
            assertEquals("Jawa Timur", result.getProvince());
        });
    }
}
