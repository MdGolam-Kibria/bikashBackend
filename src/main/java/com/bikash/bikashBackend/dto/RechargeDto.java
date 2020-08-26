package com.bikash.bikashBackend.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RechargeDto {
    @NotEmpty(message = "phone is mandatory")
    @Pattern(regexp = "^(?:\\+?88)?01[135-9]\\d{8}$", message = "invalid mobile number.")
    @Size(max = 11, message = "digits should be 11")
    private String phone;
    @DecimalMin(value = "1000.00", message = "Opening balance must be 1000 or more")
    private double amount;
}
