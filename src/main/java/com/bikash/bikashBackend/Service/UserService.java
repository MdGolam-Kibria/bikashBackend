package com.bikash.bikashBackend.Service;

import com.bikash.bikashBackend.dto.UserDto;
import com.bikash.bikashBackend.Model.User;
import com.bikash.bikashBackend.View.Response;


public interface UserService  {


    Response createUser(UserDto userDto);
    User getUserByPhone(String phone);
    Response getAllusers();
}
