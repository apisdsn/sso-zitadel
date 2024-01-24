package demo.app.controller;

import demo.app.model.AddressRequest;
import demo.app.model.AddressResponse;
import demo.app.model.WebResponse;
import demo.app.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Address", description = "Controllers for Address APIs")
@RestController
@RequestMapping("/api/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PutMapping(path = "/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> update(@RequestBody AddressRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        AddressResponse addressResponse = addressService.updateAddress(request, principal);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }
}