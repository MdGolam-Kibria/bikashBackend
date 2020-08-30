package com.bikash.bikashBackend.Service;

import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.dto.RechargeDto;
import com.bikash.bikashBackend.dto.UserDto;

import javax.servlet.http.HttpServletRequest;

public interface AgentService {
    Response createAgent(UserDto userDto, HttpServletRequest request);
    Response cashOutToAdmin(RechargeDto rechargeDto, HttpServletRequest request);
}
