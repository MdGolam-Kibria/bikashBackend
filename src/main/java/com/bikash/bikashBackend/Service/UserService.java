package com.bikash.bikashBackend.Service;

import com.bikash.bikashBackend.dto.RechargeDto;
import com.bikash.bikashBackend.dto.UserDto;
import com.bikash.bikashBackend.Model.User;
import com.bikash.bikashBackend.View.Response;

import javax.servlet.http.HttpServletRequest;


public interface UserService  {


    Response createUser(UserDto userDto);
    User getUserByPhone(String phone);
    Response getAllusers();
    Response cashOutUserOrMerchantToAgent(RechargeDto rechargeDto, HttpServletRequest request);
}
