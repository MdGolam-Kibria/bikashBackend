package com.bikash.bikashBackend.controller;

import com.bikash.bikashBackend.Service.AuthService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.annotation.ApiController;
import com.bikash.bikashBackend.annotation.ValidateData;
import com.bikash.bikashBackend.dto.LoginDto;
import com.bikash.bikashBackend.util.UrlConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstraint.AuthManagement.ROOT)
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ValidateData
    @GetMapping(UrlConstraint.AuthManagement.LOGIN)
    public Response login(@RequestBody @Valid LoginDto loginDto, BindingResult result, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return authService.login(loginDto, httpServletRequest);
    }


}
