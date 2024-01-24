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
//import demo.app.repository.AddressRepository;
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
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc
//@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reimbursement.sql")
//@Transactional
//public class AdminControllerIntegrationTest {
//    @Autowired
//    private EmployeeRepository employeeRepository;
//    @Autowired
//    private ReimbursementRepository reimbursementRepository;
//    @Autowired
//    private AddressRepository addressRepository;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private MockMvc mockMvc;
//
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
//        // token role superadmin
//        token = "xI8dUxp6-K83KvP6BymUEQ9zKGi7n4k_ytJNg5wGMPnpcox6j5fZmJ1qtDy4Ttvw7n1gDys";
//    }
//
//    @Test
//    void testUpdateReimbursementByAdminWhenAllParametersAreValidThenReturnReimbursementResponse() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setStatus(true);
//
//        mockMvc.perform(patch("/api/admin/reimbursements/239414077758111751/1")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<ReimbursementResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//            symbols.setGroupingSeparator('.');
//            symbols.setDecimalSeparator(',');
//
//            DecimalFormat decimalFormat = new DecimalFormat("Rp#,##0.00", symbols);
//
//            assertNotNull(response.getData());
//            assertEquals(1, response.getData().getEmployeeId());
//            assertEquals(decimalFormat.format(BigDecimal.valueOf(1000.00)), response.getData().getAmount());
//            assertEquals("Travel", response.getData().getActivity());
//            assertEquals("Transport", response.getData().getTypeReimbursement());
//            assertEquals("Travel to client location", response.getData().getDescription());
//            assertEquals(true, response.getData().getStatus());
//            assertEquals("239414159781920775", response.getData().getApprovedId());
//            assertEquals("API FOR DEVELOPMENT", response.getData().getApprovedName());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testUpdateReimbursementByAdminWhenInvalidParametersAreValidThenReturnBadRequestReimbursementResponse() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setStatus(null);
//
//        mockMvc.perform(patch("/api/admin/reimbursements/239414077758111751/1")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isBadRequest()
//        ).andDo(result -> {
//            WebResponse<ReimbursementResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Status cannot be null or false", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testUpdateReimbursementByAdminWhenClientIdEmployeeInvalidThenReturnNotFoundReimbursementResponse() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setStatus(true);
//
//        mockMvc.perform(patch("/api/admin/reimbursements/239414077758111752/1")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isNotFound()
//        ).andDo(result -> {
//            WebResponse<ReimbursementResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Employee is not found", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testUpdateReimbursementByAdminWhenReimbursementIdInvalidThenReturnNotFoundReimbursementResponse() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setStatus(true);
//
//        mockMvc.perform(patch("/api/admin/reimbursements/239414077758111751/2")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(reimbursementRequest))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isNotFound()
//        ).andDo(result -> {
//            WebResponse<ReimbursementResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertEquals("Reimbursement is not found", response.getErrors());
//            assertNull(response.getData());
//        });
//    }
//
//    @Test
//    void testUpdateReimbursementByAdminWhenTokenNullThenReturnUnauthorizedReimbursementResponse() throws Exception {
//        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
//        reimbursementRequest.setStatus(true);
//
//        mockMvc.perform(patch("/api/admin/reimbursements/239414077758111751/2")
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
//    void testGetReimbursementsWithStatusFalseThenReturnListReimbursementResponse() throws Exception {
//        mockMvc.perform(get("/api/admin/reimbursements/status")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<List<ReimbursementResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//            symbols.setGroupingSeparator('.');
//            symbols.setDecimalSeparator(',');
//
//            DecimalFormat decimalFormat = new DecimalFormat("Rp#,##0.00", symbols);
//
//            assertNotNull(response.getData());
//            assertEquals(1, response.getData().get(0).getEmployeeId());
//            assertEquals(1, response.getData().get(0).getEmployeeId());
//            assertEquals("Travel", response.getData().get(0).getActivity());
//            assertEquals("Transport", response.getData().get(0).getTypeReimbursement());
//            assertEquals("Travel to client location", response.getData().get(0).getDescription());
//            assertEquals(decimalFormat.format(BigDecimal.valueOf(1000.00)), response.getData().get(0).getAmount());
//            assertEquals(false, response.getData().get(0).getStatus());
//            assertEquals(1, response.getData().size());
//            assertNull(response.getData().get(0).getApprovedName());
//            assertNull(response.getData().get(0).getApprovedId());
//            assertNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void testGetReimbursementsWithStatusTrueThenReturnListReimbursementResponse() throws Exception {
//        employeeRepository.deleteAll();
//        addressRepository.deleteAll();
//        reimbursementRepository.deleteAll();
//
//        Employee employee = new Employee();
//        employee.setClientId("239414077758111751");
//        employee.setEmail("user@i2dev.com");
//        employee.setFullName("John Doe");
//        employee.setGender("Laki-Laki");
//        employee.setCompany("PT Cinta Sejati");
//        employee.setPosition("Software Engineer");
//
//        Reimbursement reimbursement = new Reimbursement();
//        reimbursement.setAmount(BigDecimal.valueOf(1000.00));
//        reimbursement.setActivity("Travel");
//        reimbursement.setTypeReimbursement("Transport");
//        reimbursement.setDescription("Travel to client location");
//        reimbursement.setStatus(true);
//        reimbursement.setEmployee(employee);
//
//        List<Reimbursement> reimbursements = new ArrayList<>();
//        reimbursements.add(reimbursement);
//        employee.setReimbursements(reimbursements);
//        employeeRepository.save(employee);
//
//        mockMvc.perform(get("/api/admin/reimbursements/status")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//        ).andExpect(
//                status().isOk()
//        ).andDo(result -> {
//            WebResponse<List<ReimbursementResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//            });
//            assertTrue(response.getData().isEmpty());
//            assertNull(response.getErrors());
//        });
//    }
//}