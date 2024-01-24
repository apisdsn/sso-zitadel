package demo.app.controller;

import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.model.WebResponse;
import demo.app.service.ReimbursementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reimbursement", description = "Controllers for Reimbursement APIs")
@RestController
@RequestMapping("/api/reimbursements")
public class ReimbursementController {
    @Autowired
    private ReimbursementService reimbursementService;

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<ReimbursementResponse> createReimbursement(@RequestBody ReimbursementRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        ReimbursementResponse reimbursementResponse = reimbursementService.create(request, principal);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    @PatchMapping(path = "/{reimbursementId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ReimbursementResponse> updateReimbursement(@PathVariable("reimbursementId") Long reimbursementId, @RequestBody ReimbursementRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        ReimbursementResponse reimbursementResponse = reimbursementService.updateReimbursementUser(reimbursementId, request, principal);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    @DeleteMapping(value = "/{reimbursementId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> deleteReimbursement(@PathVariable("reimbursementId") Long reimbursementId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        reimbursementService.removeReimbursementByUser(reimbursementId, principal);
        return WebResponse.<String>builder().data("Reimbursement deleted").build();
    }
}
