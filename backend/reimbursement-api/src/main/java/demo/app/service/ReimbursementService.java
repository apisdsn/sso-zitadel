package demo.app.service;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.repository.EmployeeRepository;
import demo.app.repository.ReimbursementRepository;
import demo.app.validator.ValidationHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReimbursementService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ReimbursementRepository reimbursementRepository;
    @Autowired
    private ValidationHelper validationHelper;

    @Transactional
    public ReimbursementResponse create(ReimbursementRequest request, OAuth2AuthenticatedPrincipal principal) {
        validationHelper.validate(request);

        String clientId = getClientIdFromPrincipal(principal);
        Employee employee = findEmployeeByClientId(clientId);

        if (request.getAmount() == null || request.getAmount().equals(BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount cannot be zero or null");
        }

        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setEmployee(employee);
        reimbursement.setAmount(request.getAmount());
        reimbursement.setActivity(request.getActivity());
        reimbursement.setTypeReimbursement(request.getTypeReimbursement());
        reimbursement.setDescription(request.getDescription());
        reimbursement.setStatus(false);
        reimbursement.setDateCreated(LocalDateTime.now());

        reimbursementRepository.save(reimbursement);
        return toReimbursementResponse(reimbursement);
    }

    @Transactional
    public ReimbursementResponse updateReimbursementUser(Long reimbursementId, ReimbursementRequest request, OAuth2AuthenticatedPrincipal principal) {
        validationHelper.validate(request);
        Employee employee = employeeRepository.findByClientId(getClientIdFromPrincipal(principal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Reimbursement reimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement is not found"));

        if (request.getAmount().equals(BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount cannot be zero");
        }

        Optional.of(request.getAmount()).ifPresent(reimbursement::setAmount);
        Optional.ofNullable(request.getDescription()).ifPresent(reimbursement::setDescription);
        Optional.ofNullable(request.getActivity()).ifPresent(reimbursement::setActivity);
        Optional.ofNullable(request.getTypeReimbursement()).ifPresent(reimbursement::setTypeReimbursement);

        reimbursement.setDateCreated(LocalDateTime.now());

        reimbursementRepository.save(reimbursement);

        return toReimbursementResponse(reimbursement);
    }

    // admin or manager service
    @Transactional
    public ReimbursementResponse updateReimbursementByAdmin(String clientId, Long reimbursementId, ReimbursementRequest request, OAuth2AuthenticatedPrincipal principal) {
        validationHelper.validate(request);
        String idApprovedBy = getClientIdFromPrincipal(principal);
        String nameApprovedBy = principal.getAttribute("name");

        Employee employee = employeeRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Reimbursement reimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement is not found"));
        
        if (request.getStatus() == null || request.getStatus().equals(false)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status cannot be null or false");
        }
        reimbursement.setStatus(request.getStatus());
        reimbursement.setApprovedId(idApprovedBy);
        reimbursement.setApprovedName(nameApprovedBy);
        reimbursement.setDateUpdated(LocalDateTime.now());

        reimbursementRepository.save(reimbursement);

        return toReimbursementResponse(reimbursement);
    }

    @Transactional
    public void removeReimbursementByAdmin(String clientId, Long reimbursementId) {
        Employee employee = employeeRepository.findByClientId((clientId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Reimbursement reimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement is not found"));

        reimbursementRepository.delete(reimbursement);
    }

    @Transactional
    public void removeReimbursementByUser(Long reimbursementId, OAuth2AuthenticatedPrincipal principal) {
        Employee employee = employeeRepository.findByClientId(getClientIdFromPrincipal(principal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Reimbursement reimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement is not found"));

        reimbursementRepository.delete(reimbursement);
    }

    // admin or manager service
    @Transactional(readOnly = true)
    public List<ReimbursementResponse> getReimbursementsWithStatusFalse() {
        List<Reimbursement> reimbursements = reimbursementRepository.findByStatusFalse();
        return reimbursements.stream()
                .map(this::toReimbursementResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReimbursementResponse toReimbursementResponse(Reimbursement reimbursement) {
        return ReimbursementResponse.builder()
                .reimbursementId(reimbursement.getReimbursementId())
                .employeeId(reimbursement.getEmployee().getEmployeeId())
                .amount(convertRupiah(reimbursement.getAmount()))
                .approvedId(reimbursement.getApprovedId())
                .approvedName(reimbursement.getApprovedName())
                .activity(reimbursement.getActivity())
                .typeReimbursement(reimbursement.getTypeReimbursement())
                .description(reimbursement.getDescription())
                .status(reimbursement.getStatus())
                .dateCreated(reimbursement.getDateCreated())
                .dateUpdated(reimbursement.getDateUpdated())
                .build();
    }

    private String getClientIdFromPrincipal(OAuth2AuthenticatedPrincipal principal) {
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client ID not found");
        }
        return attributes.get("sub").toString();
    }

    private Employee findEmployeeByClientId(String clientId) {
        return employeeRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found for the given clientId " + clientId));
    }

    public String convertRupiah(BigDecimal bigDecimalPrice) {
        Locale localId = new Locale("in", "ID");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(localId);
        return formatter.format(bigDecimalPrice);
    }
}
