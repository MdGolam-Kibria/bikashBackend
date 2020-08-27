package com.bikash.bikashBackend.Service;

import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.dto.RechargeDto;

import javax.servlet.http.HttpServletRequest;

public interface RechargeService {
    Response recharge(RechargeDto recharge, HttpServletRequest request,String transactionType);//for admin to agent recharge
    Response rechargeAgentToUserOrMerchant(RechargeDto rechargeDto);
}
