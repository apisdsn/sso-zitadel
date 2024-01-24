//package demo.app.it.controller;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import demo.app.entity.Address;
//import demo.app.entity.Employee;
//import demo.app.model.AddressRequest;
//import demo.app.model.AddressResponse;
//import demo.app.model.MessageResponse;
//import demo.app.model.WebResponse;
//import demo.app.repository.EmployeeRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.UUID;
//
//import static demo.app.utils.CustomAuthoritiesFilter.timestamp;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc
//@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reimbursement.sql")
//@Transactional
//public class AddressControllerIntegrationTest {
//    @Autowired
//    private EmployeeRepository employeeRepository;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    private String token;
//
//    @BeforeEach
//    void setUp() {
//        Employee employee = new Employee();
//        employee.setClientId("239414077758111751");
//        employee.setEmail("user@i2dev.com");
//        employee.setFullName("John Doe");
//        employee.setGender("Laki-Laki");
//        employee.setCompany("PT Cinta Sejati");
//        employee.setPosition("Software Engineer");
//        employee.setReimbursements(new ArrayList<>());
//
//        Address address = new Address();
//        address.setAddressId(UUID.randomUUID().toString());
//        address.setStreet("Jl Kenangan");
//        address.setCity("Tanggerang Selatan");
//        address.setProvince("Jawa Barat");
//        address.setCountry("Indonesia");
//        address.setPostalCode("155882");
//        employee.setAddress(address);
//        address.setEmployee(employee);
//        employeeRepository.save(employee);
//
//        // token user
//        token = "FpcQlA16cY1WFCHlVxcRBYscPwVzdQD9Rl_eoheYtl5TgUcsVS0R_f5LsEyYxxB-593dW90";
//    }
//
//    @Test
//    void testUpdateAddressWhenOldAddressThenSuccess() throws Exception {
//        AddressRequest addressRequest = new AddressRequest();
//        addressRequest.setStreet("Jl Majapahit");
//        addressRequest.setCity("Jakarta Selatan");
//        addressRequest.setProvince("DKI Jakarta");
//        addressRequest.setCountry("Indonesia");
//        addressRequest.setPostalCode("12345");
//
//        mockMvc.perform(put("/api/address/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(addressRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertNotNull(response.getData());
//            assertEquals(addressRequest.getStreet(), response.getData().getStreet());
//            assertEquals(addressRequest.getCity(), response.getData().getCity());
//            assertEquals(addressRequest.getProvince(), response.getData().getProvince());
//            assertEquals(addressRequest.getCountry(), response.getData().getCountry());
//            assertEquals(addressRequest.getPostalCode(), response.getData().getPostalCode());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testUpdateAddressWhenEmployeeNotFoundThenBadRequest() throws Exception {
//        employeeRepository.deleteAll();
//        AddressRequest addressRequest = new AddressRequest();
//
//        mockMvc.perform(put("/api/address/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(addressRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isBadRequest()
//        ).andDo(result -> {
//            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Employee not found 239414077758111751", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testUpdateAddressWhenTokenNullThenUnauthorized() throws Exception {
//        mockMvc.perform(put("/api/address/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(
//                status().isUnauthorized()
//        ).andDo(result -> {
//            MessageResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("You are not authenticated to perform this operation", response.getErrors());
//            assertEquals("Please log in to access the requested resource.", response.getMessage());
//            assertEquals(timestamp, response.getTimestamp());
//        });
//    }
//}