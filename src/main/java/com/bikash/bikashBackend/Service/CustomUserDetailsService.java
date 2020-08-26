package com.bikash.bikashBackend.Service;

import com.bikash.bikashBackend.Model.User;
import com.bikash.bikashBackend.dto.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userService.getUserByPhone(phone);
        UserPrincipal userDetails = UserPrincipal.create(user);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Phone Number Not Found");
        }
        return userDetails;
    }
}
