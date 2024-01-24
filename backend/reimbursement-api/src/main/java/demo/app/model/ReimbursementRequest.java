package demo.app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReimbursementRequest {
    @NotNull
    private BigDecimal amount;
    private String approvedId;
    @NotBlank
    private String activity;
    @NotBlank
    private String typeReimbursement;
    @NotBlank
    private String description;
    private Boolean status;
}
