package com.bikash.bikashBackend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LoginDto {
    @NotEmpty(message = "phone field should not be empty")
    @Pattern(regexp = "^(?:\\+?88)?01[135-9]\\d{8}$", message = "invalid mobile number.")
    @Size(max = 11, message = "digits should be 11")
    private String phone;
    @NotBlank(message = "Password Mandatory")
    @Size(min = 8, max = 20, message = "length should be in between 8 to 20")
    private String password;
}
