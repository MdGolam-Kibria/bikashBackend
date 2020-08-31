package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.*;
import com.bikash.bikashBackend.Service.AgentService;
import com.bikash.bikashBackend.Service.CashOutService;
import com.bikash.bikashBackend.Service.UserBalanceService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.View.ResponseBuilder;
import com.bikash.bikashBackend.dto.RechargeDto;
import com.bikash.bikashBackend.dto.UserDto;
import com.bikash.bikashBackend.repository.CommissionRepository;
import com.bikash.bikashBackend.repository.RoleRepository;
import com.bikash.bikashBackend.repository.UserBalanceRepository;
import com.bikash.bikashBackend.repository.UserRepository;
import com.bikash.bikashBackend.util.CashOutDemandUtil;
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
    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceService userBalanceService;
    private final CommissionRepository commissionRepository;
    private final CashOutService cashOutService;

    @Autowired
    public AgentServiceImple(RoleRepository roleRepository, UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, AuthServiceImple authServiceImple, UserBalanceRepository userBalanceRepository, UserBalanceService userBalanceService, CommissionRepository commissionRepository, CashOutService cashOutService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authServiceImple = authServiceImple;
        this.userBalanceRepository = userBalanceRepository;
        this.userBalanceService = userBalanceService;
        this.commissionRepository = commissionRepository;
        this.cashOutService = cashOutService;
    }

    @Override
    public Response createAgent(UserDto userDto, HttpServletRequest request) {
        User user = modelMapper.map(userDto, User.class);
        user.setCreatedAt(new Date());
        user.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIsMerchant(false);
        if (user.getInstituteName() == null || user.getTradeLicence() == null) {
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "For Create Agent Account InstituteName/TradeLicence is mandatory");
        }
        if (userRepository.findUserPhoneByPhone(user.getPhone()) == null) {
            Role role;
            role = new Role();
            if (user.getIsAgent()) {
                role = createAgentAccountRole(role);
                user.setRoles(Collections.singletonList(role));
                user = userRepository.save(user);
                ///set success response
                return authServiceImple.createTransaction(user, "createAgent");
            } else if (user.getIsAgent() == false) {
                return ResponseBuilder.getFailureResponce(HttpStatus.BAD_REQUEST, "For Create Agent Account IsAgent Must Be True");
            }
        }
        if (userRepository.findUserPhoneByPhone(user.getPhone()).equals(user.getPhone())) {
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "You already have an account with this phone number , try to login");

        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    @Override
    public Response cashOutToAdmin(RechargeDto rechargeDto, HttpServletRequest request) {//for cashOut to agent
        Recharge recharge = modelMapper.map(rechargeDto, Recharge.class);
        Long isAuthenticateAdmin = userRepository.findUserIdByPhone(recharge.getPhone());
        if (isAuthenticateAdmin == null) {
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "You don't have any account with this number");
        }

        Long haveAdmin = userRepository.findUserIdByPhone(recharge.getPhone());
        Role isAdmin = roleRepository.findByNameAndIsActiveTrue(RoleConstraint.ROLE_ADMIN.name());
//how to verify this number is admin or not
        if (haveAdmin != null && isAdmin != null) {
            //now check agent  have enough many or not
            Long agentId = userRepository.findUserIdByPhone(SecurityContextHolder.getContext().getAuthentication().getName());
            UserBalance agentBalanceDetails = userBalanceRepository.findUserBalanceByUserIdAndIsActiveTrue(agentId);
            if (agentBalanceDetails.getBalance() >= recharge.getAmount()) {
                UserBalance adminBalDetails = userBalanceRepository.findUserBalanceByUserIdAndIsActiveTrue(haveAdmin);
                if (adminBalDetails.getBalance() != 0) {
                    //cashout
                    double expectedCashOutBal = recharge.getAmount();

                    double agentCommissionPerTaka = expectedCashOutBal * CashOutDemandUtil.forAgentPerTakaCommission;
                    double adminCommissionperTaka = expectedCashOutBal * CashOutDemandUtil.forAdminPerTakaCommission;

                    double afterAllCommissionTotalBal = expectedCashOutBal - (agentCommissionPerTaka + adminCommissionperTaka);
                        /*
                        user balance update complete in cashOutService
                         */
                    TransactionDetails transactionDetails = cashOutService.cashOutAgentToAdmin(haveAdmin, agentId, afterAllCommissionTotalBal, recharge.getAmount(), new Date());
                    if (transactionDetails != null) {
                        //now s
                        // et commission
                        Commission agentCommission = new Commission();
                        //distribute commission to agent
                        agentCommission.setUserId(agentId);
                        agentCommission.setCommissionAmount(agentCommissionPerTaka);
                        agentCommission.setTotalAmount(expectedCashOutBal);
                        agentCommission.setTransactionId(transactionDetails.getTransactionId());
                        agentCommission = commissionRepository.save(agentCommission);
                        if (agentCommission != null) {
                            //distribute commision to Admin
                            Commission adminCommission = new Commission();
                            adminCommission.setUserId(haveAdmin);
                            adminCommission.setCommissionAmount(adminCommissionperTaka);
                            adminCommission.setTotalAmount(expectedCashOutBal);
                            adminCommission.setTransactionId(transactionDetails.getTransactionId());
                            adminCommission = commissionRepository.save(adminCommission);
                            if (adminCommission != null) {
                                return ResponseBuilder.getSuccessResponseForTransactions(HttpStatus.OK, "Cash Out Successfully", transactionDetails.getTransactionId());
                            }
                            return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
                        }
                    }
                }
                return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "Sorry , You don't have enough balance for Cash Out");
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.BAD_REQUEST, "Bad request");
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
