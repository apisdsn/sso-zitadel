package demo.app.service;

import demo.app.entity.Address;
import demo.app.entity.Employee;
import demo.app.model.AddressRequest;
import demo.app.model.AddressResponse;
import demo.app.repository.AddressRepository;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AddressServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private OAuth2AuthenticatedPrincipal principal;
    @InjectMocks
    private AddressService addressService;
    private AddressRequest addressRequest;
    private Employee employee;
    private Address address;

    @BeforeEach
    void setUp() {
        addressRequest = new AddressRequest();
        addressRequest.setStreet("123 Street");
        addressRequest.setCity("City");
        addressRequest.setProvince("Province");
        addressRequest.setCountry("Country");
        addressRequest.setPostalCode("12345");

        address = new Address();
        address.setAddressId("addressId");
        address.setStreet("123 Street");
        address.setCity("City");
        address.setProvince("Province");
        address.setCountry("Country");
        address.setPostalCode("12345");

        employee = new Employee();
        employee.setClientId("clientId");
        employee.setAddress(address);
    }

    @Test
    void testUpdateAddressWhenAddressIsUpdatedThenReturnUpdatedAddress() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "clientId");
        given(principal.getAttributes()).willReturn(attributes);
        given(employeeRepository.findByClientId("clientId")).willReturn(Optional.of(employee));
        given(addressRepository.save(address)).willReturn(address);

        AddressResponse addressResponse = addressService.updateAddress(addressRequest, principal);

        assertEquals(address.getAddressId(), addressResponse.getAddressId());
        assertEquals(address.getStreet(), addressResponse.getStreet());
        assertEquals(address.getCity(), addressResponse.getCity());
        assertEquals(address.getProvince(), addressResponse.getProvince());
        assertEquals(address.getCountry(), addressResponse.getCountry());
        assertEquals(address.getPostalCode(), addressResponse.getPostalCode());

        verify(validationHelper, times(1)).validate(addressRequest);
        verify(employeeRepository, times(1)).findByClientId("clientId");
        verify(addressRepository, times(1)).save(address);

    }

    @Test
    void testUpdateAddressWhenAddressNotFoundThenThrowResponseStatusException() {
        employee.setAddress(null);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "clientId");
        given(principal.getAttributes()).willReturn(attributes);
        given(employeeRepository.findByClientId("clientId")).willReturn(Optional.of(employee));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> addressService.updateAddress(addressRequest, principal));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Address not found for the employee", exception.getReason());

        verify(addressRepository, times(0)).save(address);
        verify(employeeRepository, times(1)).findByClientId("clientId");
    }

    @Test
    void testToAddressResponse() {
        AddressResponse addressResponse = addressService.toAddressResponse(address);

        assertEquals(address.getAddressId(), addressResponse.getAddressId());
        assertEquals(address.getStreet(), addressResponse.getStreet());
        assertEquals(address.getCity(), addressResponse.getCity());
        assertEquals(address.getProvince(), addressResponse.getProvince());
        assertEquals(address.getCountry(), addressResponse.getCountry());
        assertEquals(address.getPostalCode(), addressResponse.getPostalCode());
    }
}
