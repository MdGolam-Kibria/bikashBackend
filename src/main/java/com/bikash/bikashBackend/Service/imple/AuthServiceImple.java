package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.*;
import com.bikash.bikashBackend.Service.AuthService;
import com.bikash.bikashBackend.Service.TransactionDetailsService;
import com.bikash.bikashBackend.Service.TransactionService;
import com.bikash.bikashBackend.Service.UserBalanceService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.View.ResponseBuilder;
import com.bikash.bikashBackend.dto.LoginDto;
import com.bikash.bikashBackend.dto.LoginResponseDto;
import com.bikash.bikashBackend.dto.UserDto;
import com.bikash.bikashBackend.filter.JwtTokenProvider;
import com.bikash.bikashBackend.repository.RoleRepository;
import com.bikash.bikashBackend.repository.UserRepository;
import com.bikash.bikashBackend.util.RoleConstraint;
import com.bikash.bikashBackend.util.UseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;

@Service("authService")
public class AuthServiceImple implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TransactionService transactionService;
    private final TransactionDetailsService transactionDetailsService;
    private final UserBalanceService userBalanceService;

    @Autowired
    public AuthServiceImple(UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, ModelMapper modelMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository, TransactionService transactionService, TransactionDetailsService transactionDetailsService, UserBalanceService userBalanceService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.transactionService = transactionService;
        this.transactionDetailsService = transactionDetailsService;
        this.userBalanceService = userBalanceService;
    }

    @Override
    public Response login(LoginDto loginDto, HttpServletRequest request) {
        User user = userRepository.findByPhoneAndIsActiveTrue(loginDto.getPhone());
        if (user == null) {
            return ResponseBuilder.getFailureResponce(HttpStatus.UNAUTHORIZED, "Invalid Phone or Password");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getPhone(), loginDto.getPassword()));
        if (authentication.isAuthenticated()) {
            LoginResponseDto loginResponseDto = new LoginResponseDto();
            loginResponseDto.setToken(jwtTokenProvider.generateToken(authentication, request));
            loginResponseDto.setUsername(user.getUsername());
            return ResponseBuilder.getSuccessResponce(HttpStatus.OK, "Logged In Success", loginResponseDto);
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.BAD_REQUEST, "Invalid Phone or Password");
    }

    @Override
    public Response createUserAccount(UserDto userDto, BindingResult result, HttpServletRequest request) {
        User user = modelMapper.map(userDto, User.class);
        user.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setCreatedAt(new Date());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIsAgent(false);
        user.setTradeLicence(null);
        user.setInstituteName(null);
        if (userRepository.findUserPhoneByPhone(user.getPhone()) == null) {
            Role role;
            role = new Role();
            if (user.getIsMerchant() == false) {
                int haveAnyUser = roleRepository.countByNameAndIsActiveTrue(RoleConstraint.ROLE_USER.name());
                if (haveAnyUser == 0) {
                    role.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
                    role.setName(RoleConstraint.ROLE_USER.name());
                    role = roleRepository.save(role);
                }
                role = roleRepository.findByNameAndIsActiveTrue(RoleConstraint.ROLE_USER.name());
                user.setRoles(Collections.singletonList(role));
                user = userRepository.save(user);
            }
            if (user.getIsMerchant()) {
                return ResponseBuilder.getSuccessResponce(HttpStatus.BAD_REQUEST, "For Create User Account IsMerchant Must Be false", null);
            }
//now save account opening time Transaction,TransactionDetails,and set userBalance
            Response response = createTransaction(user,"Create User");
            return response;
        }
        if (userRepository.findUserPhoneByPhone(user.getPhone()).equals(user.getPhone())) {
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "You already have an account with this phone number , try to login");
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    @Override
    public Response createMerchantAccount(UserDto userDto, BindingResult bindingResult, HttpServletRequest request) {

        User user = modelMapper.map(userDto, User.class);
        user.setCreatedAt(new Date());
        user.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIsAgent(false);

        if (user.getInstituteName()==null || user.getTradeLicence()==null){
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE,"For Create Merchant Account InstituteName/TradeLicence is mandatory");
        }

        if (userRepository.findUserPhoneByPhone(user.getPhone()) == null) {
            Role role;
            role = new Role();
            if (user.getIsMerchant()) {
                role = createMerchantAccountRole(role);
                user.setRoles(Collections.singletonList(role));
                user = userRepository.save(user);
            } else {
                return ResponseBuilder.getSuccessResponce(HttpStatus.BAD_REQUEST, "For Create Merchant Account IsMerchant Must Be True", null);
            }
//now save account opening time Transaction,TransactionDetails,and set userBalance
            Response response = createTransaction(user,"Create Marchent");
            return response;
        }
        if (userRepository.findUserPhoneByPhone(user.getPhone()).equals(user.getPhone())) {
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "You already have an account with this phone number , try to login");
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private Role createMerchantAccountRole(Role role) {
        int haveAnyMerchant = roleRepository.countByNameAndIsActiveTrue(RoleConstraint.ROLE_MERCHANT.name());
        if (haveAnyMerchant == 0) {
            role.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            role.setName(RoleConstraint.ROLE_MERCHANT.name());
            role = roleRepository.save(role);
            return role;
        }
        role = roleRepository.findByNameAndIsActiveTrue(RoleConstraint.ROLE_MERCHANT.name());
        return role;
    }

    public Response createTransaction(User user,String transactionsRef) {
        if (user != null) {
            Transactions transactions = transactionService.create(user.getId(), user.getOpeningBalance(), 0, new Date(), transactionsRef);
            if (transactions != null) {
                TransactionDetails transactionDetails = transactionDetailsService.create(transactions.getTransactionId(), user.getId(), user.getOpeningBalance(),0,new Date(), UseUtil.CREDIT);
                if (transactionDetails != null) {
                    UserBalance userBalance = userBalanceService.create(user.getId(), user.getOpeningBalance(),user.getCreatedAt());
                    if (userBalance != null) {
                        return ResponseBuilder.getSuccessResponseForTransactions(HttpStatus.CREATED, "Account Successfully Created", transactionDetails.getTransactionId());
                    }
                }
            }
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
}
