//package demo.app.it.controller;
//
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import demo.app.entity.Address;
//import demo.app.entity.Employee;
//import demo.app.model.EmployeeRequest;
//import demo.app.model.EmployeeResponse;
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
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc
//@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reimbursement.sql")
//@Transactional
//public class EmployeeControllerIntegrationTest {
//    @Autowired
//    private EmployeeRepository employeeRepository;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    private Employee employee;
//    private Address address;
//    private String token;
//
//    @BeforeEach
//    void setUp() {
//        employee = new Employee();
//        employee.setClientId("239414077758111751");
//        employee.setEmail("user@i2dev.com");
//        employee.setFullName("John Doe");
//        employee.setGender("Laki-Laki");
//        employee.setCompany("PT Cinta Sejati");
//        employee.setPosition("Software Engineer");
//        employee.setReimbursements(new ArrayList<>());
//
//        address = new Address();
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
//    void testRegisterEmployeeWhenNewEmployeeThenSuccess() throws Exception {
//        employeeRepository.deleteAll();
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setFullName("John Doe");
//        employeeRequest.setGender("Laki-Laki");
//        employeeRequest.setCompany("PT Cinta Sejati");
//        employeeRequest.setPosition("Software Engineer");
//        employeeRequest.setStreet("Jl Kenangan");
//        employeeRequest.setCity("Tanggerang Selatan");
//        employeeRequest.setProvince("Jawa Barat");
//        employeeRequest.setCountry("Indonesia");
//        employeeRequest.setPostalCode("155882");
//
//        mockMvc.perform(post("/api/employees/register")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(employeeRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isCreated()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Data has been stored in the database", response.getData());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testRegisterEmployeeWhenMissingRequiredFieldsThenBadRequest() throws Exception {
//        employeeRepository.deleteAll();
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setFullName("");
//
//        mockMvc.perform(post("/api/employees/register")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(employeeRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isBadRequest()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Full name cannot be blank", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testRegisterEmployeeWhenTokenNullThenUnauthorized() throws Exception {
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setFullName("John Doe");
//
//        mockMvc.perform(post("/api/employees/register")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(employeeRequest))
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
//
//    @Test
//    void testRegisterEmployeeWhenIsExistedEmployeeThenBadRequest() throws Exception {
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setFullName("John Doe");
//
//        mockMvc.perform(post("/api/employees/register")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(employeeRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isBadRequest()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Employee already exists", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testGetCurrentEmployeesWhenEmployeesExistThenSuccess() throws Exception {
//        mockMvc.perform(get("/api/employees/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertNotNull(response.getData());
//            assertEquals(response.getData().getEmployeeId(), employee.getEmployeeId());
//            assertEquals(response.getData().getFullName(), employee.getFullName());
//            assertEquals(response.getData().getEmail(), employee.getEmail());
//            assertEquals(response.getData().getGender(), employee.getGender());
//            assertEquals(response.getData().getCompany(), employee.getCompany());
//            assertEquals(response.getData().getPosition(), employee.getPosition());
//            assertEquals(response.getData().getAddress().getAddressId(), address.getAddressId());
//            assertEquals(response.getData().getAddress().getStreet(), address.getStreet());
//            assertEquals(response.getData().getAddress().getCity(), address.getCity());
//            assertEquals(response.getData().getAddress().getProvince(), address.getProvince());
//            assertEquals(response.getData().getAddress().getCountry(), address.getCountry());
//            assertEquals(response.getData().getAddress().getPostalCode(), address.getPostalCode());
//            assertEquals(response.getData().getReimbursements().size(), employee.getReimbursements().size());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testGetCurrentEmployeesWhenTokenNullThenUnauthorized() throws Exception {
//        mockMvc.perform(get("/api/employees/current")
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
//
//    @Test
//    void testGetCurrentEmployeesWhenNoEmployeesExistThenNotFound() throws Exception {
//        employeeRepository.deleteAll();
//        mockMvc.perform(get("/api/employees/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isNotFound()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Employee not found for the given clientId 239414077758111751", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testUpdateEmployeeWhenEmployeesExistThenSuccess() throws Exception {
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setFullName("Mamang Kasbor");
//        employeeRequest.setCompany("PT Melia Sehat Sejahtera");
//
//        mockMvc.perform(put("/api/employees/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(employeeRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//
//            });
//            assertNotNull(response.getData());
//            assertEquals(employeeRequest.getFullName(), response.getData().getFullName());
//            assertEquals(employeeRequest.getCompany(), response.getData().getCompany());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testUpdateEmployeeWhenRequestNullOrInvalidRequestThenBadRequest() throws Exception {
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//
//        mockMvc.perform(put("/api/employees/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(employeeRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isBadRequest()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Request cannot be null and Full name cannot be blank or empty", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testUpdateEmployeeWhenNoEmployeesExistThenNotFound() throws Exception {
//        employeeRepository.deleteAll();
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setFullName("John Doe");
//        mockMvc.perform(put("/api/employees/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(employeeRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isNotFound()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Employee not found for the given clientId 239414077758111751", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testUpdateEmployeeWhenTokenNullThenUnauthorized() throws Exception {
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setFullName("John Doe");
//        mockMvc.perform(put("/api/employees/current")
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
//
//    @Test
//    void testDeleteEmployeeWhenEmployeesExistThenSuccess() throws Exception {
//        mockMvc.perform(delete("/api/employees/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Data has been removed from the database", response.getData());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testDeleteEmployeeWhenNoEmployeesExistThenNotFound() throws Exception {
//        employeeRepository.deleteAll();
//        mockMvc.perform(delete("/api/employees/current")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isNotFound()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Employee not found for the given clientId 239414077758111751", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testDeleteEmployeeWhenTokenNullThenUnauthorized() throws Exception {
//        mockMvc.perform(delete("/api/employees/current")
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