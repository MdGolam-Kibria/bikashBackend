package com.bikash.bikashBackend.controller;

import com.bikash.bikashBackend.Service.RechargeService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.annotation.AdminOrAgent;
import com.bikash.bikashBackend.annotation.ApiController;
import com.bikash.bikashBackend.annotation.ValidateData;
import com.bikash.bikashBackend.dto.RechargeDto;
import com.bikash.bikashBackend.util.UrlConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstraint.AgentManagement.ROOT)
public class AgentController {
    private final RechargeService rechargeService;

    @Autowired
    public AgentController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @AdminOrAgent
    @PostMapping(UrlConstraint.RECHARGE + UrlConstraint.USER)
    @ValidateData
    public Response rechargeAgentToUserOrMerchant(@RequestBody @Valid RechargeDto rechargeDto, BindingResult result, HttpServletRequest request) {
        return rechargeService.rechargeAgentToUserOrMerchant(rechargeDto);
    }

}
