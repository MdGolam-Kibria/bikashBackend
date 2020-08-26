package com.bikash.bikashBackend.testApi;

import com.bikash.bikashBackend.Model.Role;
import com.bikash.bikashBackend.Model.User;
import com.bikash.bikashBackend.repository.RoleRepository;
import com.bikash.bikashBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
public class CreateRunTimeUserTest {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${login.username}")
    private String phone;
    @Value("${login.password}")
    private String password;

    @Autowired
    public CreateRunTimeUserTest(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void test() {
        String roleName = "ROLE_ADMIN";
        int roleExistCount = roleRepository.countByNameAndIsActiveTrue(roleName);
        Role role = null;
        if (roleExistCount == 1) {
            role = roleRepository.findByNameAndIsActiveTrue(roleName);
        } else {
            role = new Role();
            role.setName(roleName);
            role = roleRepository.save(role);
        }
        User user = userRepository.findByPhoneAndIsActiveTrue(phone);
        if (user == null) {
            user = new User();
            user.setCreatedBy("RunTimeUser");
            user.setUsername("Golam Kibria");
            user.setPassword(passwordEncoder.encode(password));
            user.setIsMerchant(true);
            user.setOpeningBalance(Double.parseDouble("55290"));
            user.setNid(Long.parseLong("1234567890"));
            user.setPhone(phone);
            user.setEmail("golamkibria.java@gmail.com");
        }

        user.setRoles(Arrays.asList(role));
        user = userRepository.save(user);

    }
}

