package demo.app.controller;

import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.model.WebResponse;
import demo.app.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Employee", description = "Controllers for Employee APIs")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> registerEmployee(@RequestBody EmployeeRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        employeeService.register(request, principal);
        return WebResponse.<String>builder().data("Data has been stored in the database").build();
    }

    @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> getCurrentEmployee(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        EmployeeResponse employeeResponse = employeeService.getCurrent(principal);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    @PutMapping(path = "/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> updateCurrentEmployee(@RequestBody EmployeeRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        EmployeeResponse employeeResponse = employeeService.update(request, principal);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    @DeleteMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> removeCurrentEmployee(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        employeeService.removeCurrent(principal);
        return WebResponse.<String>builder().data("Data has been removed from the database").build();
    }
}
