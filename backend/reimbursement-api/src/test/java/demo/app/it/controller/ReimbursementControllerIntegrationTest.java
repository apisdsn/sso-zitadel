//package demo.app.it.controller;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import demo.app.entity.Address;
//import demo.app.entity.Employee;
//import demo.app.entity.Reimbursement;
//import demo.app.model.MessageResponse;
//import demo.app.model.ReimbursementRequest;
//import demo.app.model.ReimbursementResponse;
//import demo.app.model.WebResponse;
//import demo.app.repository.EmployeeRepository;
//import demo.app.repository.ReimbursementRepository;
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
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;
//import java.util.ArrayList;
//import java.util.List;
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
//public class ReimbursementControllerIntegrationTest {
//    @Autowired
//    private ReimbursementRepository reimbursementRepository;
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
//
//        Address address = new Address();
//        address.setAddressId(UUID.randomUUID().toString());
//        address.setStreet("Jl Kenangan");
//        address.setCity("Tanggerang Selatan");
//        address.setProvince("Jawa Barat");
//        address.setCountry("Indonesia");
//        address.setPostalCode("155882");
//
//        address.setEmployee(employee);
//        employee.setAddress(address);
//
//        Reimbursement reimbursement = new Reimbursement();
//        reimbursement.setAmount(BigDecimal.valueOf(1000.00));
//        reimbursement.setActivity("Travel");
//        reimbursement.setTypeReimbursement("Transport");
//        reimbursement.setDescription("Travel to client location");
//        reimbursement.setStatus(false);
//        reimbursement.setEmployee(employee);
//
//        List<Reimbursement> reimbursements = new ArrayList<>();
//        reimbursements.add(reimbursement);
//        employee.setReimbursements(reimbursements);
//        employeeRepository.save(employee);
//
//        // token user
//        token = "FpcQlA16cY1WFCHlVxcRBYscPwVzdQD9Rl_eoheYtl5TgUcsVS0R_f5LsEyYxxB-593dW90";
//    }
//
//    @Test
//    void testCreateReimbursementWhenAllParametersAreValidThenReturnReimbursementResponse() throws Exception {
//        reimbursementRepository.deleteAll();
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setAmount(BigDecimal.valueOf(1000.00));
//        reimbursementRequest.setActivity("Travel");
//        reimbursementRequest.setTypeReimbursement("Transport");
//        reimbursementRequest.setDescription("Travel to client location");
//        reimbursementRequest.setStatus(false);
//
//        mockMvc.perform(post("/api/reimbursements")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//
//        ).andExpect(
//                status().isCreated()
//        ).andDo(result -> {
//            WebResponse<ReimbursementResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//
//            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//            symbols.setGroupingSeparator('.');
//            symbols.setDecimalSeparator(',');
//
//            DecimalFormat decimalFormat = new DecimalFormat("Rp#,##0.00", symbols);
//
//            assertNotNull(response.getData());
//            assertEquals(decimalFormat.format(reimbursementRequest.getAmount()), response.getData().getAmount());
//            assertEquals(reimbursementRequest.getActivity(), response.getData().getActivity());
//            assertEquals(reimbursementRequest.getTypeReimbursement(), response.getData().getTypeReimbursement());
//            assertEquals(reimbursementRequest.getDescription(), response.getData().getDescription());
//            assertEquals(reimbursementRequest.getStatus(), response.getData().getStatus());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testCreateReimbursementWhenMissingRequiredFieldsThenBadRequest() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setAmount(BigDecimal.valueOf(0));
//
//        mockMvc.perform(post("/api/reimbursements")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isBadRequest()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Amount cannot be zero or null", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testCreateReimbursementWhenTokenNullThenUnauthorized() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setAmount(BigDecimal.valueOf(1000.00));
//        reimbursementRequest.setActivity("Travel");
//        reimbursementRequest.setTypeReimbursement("Transport");
//        reimbursementRequest.setDescription("Travel to client location");
//        reimbursementRequest.setStatus(false);
//
//        mockMvc.perform(post("/api/reimbursements")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
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
//    void testUpdateReimbursementWhenAllParametersAreValidThenReturnReimbursementResponse() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setAmount(BigDecimal.valueOf(50000.00));
//        reimbursementRequest.setActivity("Data");
//        reimbursementRequest.setTypeReimbursement("Lain - Lain");
//        reimbursementRequest.setDescription("Data for call client");
//        reimbursementRequest.setStatus(false);
//
//        mockMvc.perform(patch("/api/reimbursements/1")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<ReimbursementResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//
//            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//            symbols.setGroupingSeparator('.');
//            symbols.setDecimalSeparator(',');
//
//            DecimalFormat decimalFormat = new DecimalFormat("Rp#,##0.00", symbols);
//
//            assertNotNull(response.getData());
//            assertEquals(decimalFormat.format(reimbursementRequest.getAmount()), response.getData().getAmount());
//            assertEquals(reimbursementRequest.getActivity(), response.getData().getActivity());
//            assertEquals(reimbursementRequest.getTypeReimbursement(), response.getData().getTypeReimbursement());
//            assertEquals(reimbursementRequest.getDescription(), response.getData().getDescription());
//            assertEquals(reimbursementRequest.getStatus(), response.getData().getStatus());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testUpdateReimbursementWhenReimbursementIdNotFoundThenNotFound() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setAmount(BigDecimal.valueOf(50000.00));
//        reimbursementRequest.setActivity("Data");
//        reimbursementRequest.setTypeReimbursement("Lain - Lain");
//        reimbursementRequest.setDescription("Data for call client");
//
//        mockMvc.perform(patch("/api/reimbursements/9999")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isNotFound()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Reimbursement is not found", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testUpdateReimbursementWhenReimbursementIdInvalidThenBadRequest() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setAmount(BigDecimal.valueOf(50000.00));
//        reimbursementRequest.setActivity("Data");
//        reimbursementRequest.setTypeReimbursement("Lain - Lain");
//        reimbursementRequest.setDescription("Data for call client");
//
//        mockMvc.perform(patch("/api/reimbursements/invalid_id")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testUpdateReimbursementWhenTokenNullThenUnauthorized() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setAmount(BigDecimal.valueOf(50000.00));
//        reimbursementRequest.setActivity("Data");
//        reimbursementRequest.setTypeReimbursement("Lain - Lain");
//        reimbursementRequest.setDescription("Data for call client");
//
//        mockMvc.perform(patch("/api/reimbursements/1")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
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
//    void testDeleteReimbursementWhenAllParametersAreValidThenSuccess() throws Exception {
//        mockMvc.perform(delete("/api/reimbursements/1")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Reimbursement deleted", response.getData());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testDeleteReimbursementWhenReimbursementIdNotFoundThenNotFound() throws Exception {
//        mockMvc.perform(delete("/api/reimbursements/9999")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isNotFound()
//        ).andDo(result -> {
//            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Reimbursement is not found", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testDeleteReimbursementWhenTokenNullThenUnauthorized() throws Exception {
//        mockMvc.perform(delete("/api/reimbursements/1")
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