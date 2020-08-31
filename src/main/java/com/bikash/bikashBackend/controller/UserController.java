package com.bikash.bikashBackend.controller;

import com.bikash.bikashBackend.Service.AuthService;
import com.bikash.bikashBackend.Service.UserService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.annotation.ApiController;
import com.bikash.bikashBackend.annotation.IsUser;
import com.bikash.bikashBackend.annotation.ValidateData;
import com.bikash.bikashBackend.dto.RechargeDto;
import com.bikash.bikashBackend.dto.UserDto;
import com.bikash.bikashBackend.util.UrlConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstraint.UserManagement.ROOT)
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    @PostMapping(UrlConstraint.UserManagement.CREATE)
    @ValidateData
    public Response createUser(@RequestBody @Valid UserDto userDto, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {
        return authService.createUserAccount(userDto, bindingResult, request);
    }

    @IsUser
    @PostMapping(UrlConstraint.CASHOUT + UrlConstraint.AGENT)
    @ValidateData
    public Response cashOutUserOrMerchantToAgent(@RequestBody @Valid RechargeDto rechargeDto, BindingResult result, HttpServletRequest request) {
        return userService.cashOutUserOrMerchantToAgent(rechargeDto, request);
    }
}
