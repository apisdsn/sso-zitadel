package demo.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeRequest {

    @Size(max = 50)
    @JsonProperty("full_name")
    private String fullName;

    @Size(max = 50)
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Size(max = 50)
    private String company;

    @Size(max = 50)
    private String position;

    @Size(max = 20)
    private String gender;

    @Size(max = 200)
    private String street;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String province;

    @Size(max = 100)
    private String country;

    @Size(max = 10)
    @JsonProperty("postal_code")
    private String postalCode;

}
