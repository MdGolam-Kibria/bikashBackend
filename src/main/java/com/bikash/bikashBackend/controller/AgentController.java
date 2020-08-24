package com.bikash.bikashBackend.controller;

import com.bikash.bikashBackend.Service.AgentService;
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
@RequestMapping(UrlConstraint.AgentManagement.ROOT)
public class AgentController {
    private final AgentService agentService;

    @Autowired
    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping(UrlConstraint.AgentManagement.CREATE)
    @ValidateData
    @IsAdmin
    public Response createAgent(@RequestBody @Valid UserDto userDto, BindingResult result, HttpServletRequest request) {
        return agentService.createAgent(userDto, request);
    }
}
