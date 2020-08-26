package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.dto.UserDto;
import com.bikash.bikashBackend.Model.User;
import com.bikash.bikashBackend.Service.UserService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplement(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Response createUser(UserDto userDto) {
        return null;
    }

    @Override
    public User getUserByPhone(String phone) {
        return userRepository.findByPhoneAndIsActiveTrue(phone);
    }

    @Override
    public Response getAllusers() {
        return null;
    }
}
