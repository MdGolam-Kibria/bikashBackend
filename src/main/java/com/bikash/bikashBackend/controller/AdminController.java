package com.bikash.bikashBackend.controller;

import com.bikash.bikashBackend.Service.AuthService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.annotation.ApiController;
import com.bikash.bikashBackend.annotation.IsAdmin;
import com.bikash.bikashBackend.annotation.ValidateData;
import com.bikash.bikashBackend.dto.UserDto;
import com.bikash.bikashBackend.util.UrlConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstraint.MerchantManagement.ROOT)
public class AdminController {
    private final AuthService authService;

    @Autowired
    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(UrlConstraint.MerchantManagement.CREATE)
    @IsAdmin
    @ValidateData
    public Response createMerchant(@RequestBody @Valid UserDto userDto, BindingResult bindingResult, HttpServletRequest request) {
        return authService.createMerchantAccount(userDto, bindingResult, request);
    }
}
