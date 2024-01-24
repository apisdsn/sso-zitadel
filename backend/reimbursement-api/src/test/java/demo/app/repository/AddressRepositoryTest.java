package demo.app.repository;

import demo.app.entity.Address;
import demo.app.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
public class AddressRepositoryTest {
    @Mock
    private AddressRepository addressRepository;
    private Address address;

    @BeforeEach
    void setUp() {
        Employee employee = new Employee();
        employee.setClientId("123");
        employee.setEmail("john.doe@example.com");
        employee.setFullName("John Doe");
        employee.setCompany("Acme Corporation");
        employee.setPosition("Software Engineer");
        employee.setGender("Male");

        address = new Address();
        address.setAddressId("123");
        address.setStreet("Test Street");
        address.setCity("Test City");
        address.setProvince("Test Province");
        address.setCountry("Test Country");
        address.setPostalCode("12345");

        employee.setAddress(address);
        address.setEmployee(employee);
    }

    @Test
    void testFindFirstByAddressIdWhenAddressExistsThenReturnAddress() {
        given(addressRepository.findFirstByAddressId(address.getAddressId())).willReturn(Optional.of(address));

        Optional<Address> found = addressRepository.findFirstByAddressId(address.getAddressId());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isEqualTo(address);
        verify(addressRepository, times(1)).findFirstByAddressId(address.getAddressId());
    }

    @Test
    void testFindFirstByAddressIdWhenAddressDoesNotExistThenReturnEmptyOptional() {
        Optional<Address> found = addressRepository.findFirstByAddressId("nonexistent");

        assertThat(found.isPresent()).isFalse();
        verify(addressRepository, times(0)).findFirstByAddressId(address.getAddressId());
    }
}