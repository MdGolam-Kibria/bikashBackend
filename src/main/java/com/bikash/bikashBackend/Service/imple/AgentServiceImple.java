package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.Role;
import com.bikash.bikashBackend.Model.User;
import com.bikash.bikashBackend.Service.AgentService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.View.ResponseBuilder;
import com.bikash.bikashBackend.dto.UserDto;
import com.bikash.bikashBackend.repository.RoleRepository;
import com.bikash.bikashBackend.repository.UserRepository;
import com.bikash.bikashBackend.util.RoleConstraint;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;

@Service("agentService")
public class AgentServiceImple implements AgentService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthServiceImple authServiceImple;

    @Autowired
    public AgentServiceImple(RoleRepository roleRepository, UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, AuthServiceImple authServiceImple) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authServiceImple = authServiceImple;
    }

    @Override
    public Response createAgent(UserDto userDto, HttpServletRequest request) {
        User user = modelMapper.map(userDto, User.class);
        user.setCreatedAt(new Date());
        user.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIsMerchant(false);
        if (user.getInstituteName()==null || user.getTradeLicence()==null){
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE,"For Create Agent Account InstituteName/TradeLicence is mandatory");
        }
        if (userRepository.findUserPhoneByPhone(user.getPhone()) == null) {
            Role role;
            role = new Role();
            if (user.getIsAgent()) {
                role = createAgentAccountRole(role);
                user.setRoles(Collections.singletonList(role));
                user = userRepository.save(user);
                ///set success response
                return authServiceImple.createTransaction(user);
            } else if (user.getIsAgent() == false) {
                return ResponseBuilder.getFailureResponce(HttpStatus.BAD_REQUEST, "For Create Agent Account IsAgent Must Be True");
            }
        }
        if (userRepository.findUserPhoneByPhone(user.getPhone()).equals(user.getPhone())) {
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "You already have an account with this phone number , try to login");

        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private Role createAgentAccountRole(Role role) {
        int haveAnyAgent = roleRepository.countByNameAndIsActiveTrue(RoleConstraint.ROLE_AGENT.name());
        if (haveAnyAgent == 0) {
            role.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            role.setName(RoleConstraint.ROLE_AGENT.name());
            role = roleRepository.save(role);
            return role;
        }
        role = roleRepository.findByNameAndIsActiveTrue(RoleConstraint.ROLE_AGENT.name());
        return role;
    }
}
