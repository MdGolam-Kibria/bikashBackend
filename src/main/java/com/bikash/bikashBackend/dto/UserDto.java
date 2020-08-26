package com.bikash.bikashBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class UserDto {

    private Long id;
    @NotEmpty(message = "User Name is Mandatory")
    private String username;
    @NotEmpty(message = "You Have To Enter The Password")
    @Size(min = 8, max = 20, message = "Password length should be in between 8 to 20")
    @JsonProperty
    private String password;
    //@AssertTrue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isMerchant;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isAgent;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tradeLicence;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String instituteName;

    @NotEmpty(message = "Phone Number is Mandatory")
    @Pattern(regexp = "^(?:\\+?88)?01[135-9]\\d{8}$", message = "invalid mobile number.")
    @Size(max = 11, message = "digits should be 11")
    private String phone;
    @DecimalMin(value = "500.00", message = "Opening balance must be 500 or more")
    private double openingBalance;
    @NotNull(message = "Nid is Mandatory")
    //@Min(value = 10, message = "Nid length should be 10 or more")
    private Long nid;
    @NotEmpty(message = "Email field should not be empty")
    @Email(regexp = "^(.+)@(.+)$", message = "Invalid Email Pattern")
    private String email;


}
