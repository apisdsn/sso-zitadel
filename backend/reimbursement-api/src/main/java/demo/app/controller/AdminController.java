package demo.app.controller;

import demo.app.model.EmployeeResponse;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.model.WebResponse;
import demo.app.service.EmployeeService;
import demo.app.service.ReimbursementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "Controllers for Admin APIs")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ReimbursementService reimbursementService;
    @Autowired
    private EmployeeService employeeService;

    // Reimbursement Controller - Admin
    @PatchMapping(path = "/reimbursements/{clientId}/{reimbursementId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ReimbursementResponse> updateReimbursementByAdmin(@PathVariable("clientId") String clientId, @PathVariable("reimbursementId") Long reimbursementId, @RequestBody ReimbursementRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        ReimbursementResponse reimbursementResponse = reimbursementService.updateReimbursementByAdmin(clientId, reimbursementId, request, principal);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    @GetMapping(path = "/reimbursements/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<List<ReimbursementResponse>> getReimbursementsWithFalseStatus() {
        List<ReimbursementResponse> reimbursementResponses = reimbursementService.getReimbursementsWithStatusFalse();
        return WebResponse.<List<ReimbursementResponse>>builder().data(reimbursementResponses).build();
    }

    @DeleteMapping("/reimbursements/{clientId}/{reimbursementId}")
    public WebResponse<String> removeReimbursementByAdmin(@PathVariable("clientId") String clientId, @PathVariable("reimbursementId") Long reimbursementId) {
        reimbursementService.removeReimbursementByAdmin(clientId, reimbursementId);
        return WebResponse.<String>builder().data("OK").build();
    }

    // Employee Controller - Admin
    @GetMapping(path = "/employees/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> getEmployeeByClientId(@PathVariable("clientId") String clientId) {
        EmployeeResponse employeeResponse = employeeService.getByClientId(clientId);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    @GetMapping(path = "/employees/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employeeResponseAll = employeeService.findAllEmployee();
        return WebResponse.<List<EmployeeResponse>>builder().data(employeeResponseAll).build();
    }

    @DeleteMapping(path = "/employees/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> removeEmployee(@PathVariable("clientId") String clientId) {
        employeeService.removeByClientId(clientId);
        return WebResponse.<String>builder().data("Employee with clientId " + clientId + " has been removed").build();
    }
}
