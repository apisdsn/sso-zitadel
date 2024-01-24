package demo.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.model.AddressRequest;
import demo.app.model.AddressResponse;
import demo.app.model.WebResponse;
import demo.app.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AddressService addressService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUpdateValidAddressRequestShouldReturnAddressResponse() throws Exception {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("Jl Kenangan");
        addressRequest.setCity("Tanggerang Selatan");
        addressRequest.setProvince("Jawa Barat");
        addressRequest.setCountry("Indonesia");
        addressRequest.setPostalCode("155882");

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setAddressId("1");
        addressResponse.setStreet("Jl Kenangan");
        addressResponse.setCity("Tanggerang Selatan");
        addressResponse.setProvince("Jawa Barat");
        addressResponse.setCountry("Indonesia");
        addressResponse.setPostalCode("155882");

        given(addressService.updateAddress(any(AddressRequest.class), any())).willReturn(addressResponse);

        mockMvc.perform(put("/api/address/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(addressResponse, null))))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.addressId").value("1"))
                .andExpect(jsonPath("$.data.street").value("Jl Kenangan"))
                .andExpect(jsonPath("$.data.city").value("Tanggerang Selatan"))
                .andExpect(jsonPath("$.data.province").value("Jawa Barat"))
                .andExpect(jsonPath("$.data.country").value("Indonesia"))
                .andExpect(jsonPath("$.data.postalCode").value("155882"));

        verify(addressService, times(1)).updateAddress(any(AddressRequest.class), any());
    }

    @Test
    void testUpdateInvalidAddressRequestShouldThrowResponseStatusException() throws Exception {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("Test");
        addressRequest.setCity("Test");
        addressRequest.setProvince("Test");
        addressRequest.setCountry("Test");
        addressRequest.setPostalCode("Test");

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid address request")).when(addressService).updateAddress(any(AddressRequest.class), any());

        mockMvc.perform(put("/api/address/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(null, "Invalid address request"))));

        verify(addressService, times(1)).updateAddress(any(AddressRequest.class), any());
        verify(addressService, times(1)).updateAddress(eq(addressRequest), any());
    }
}