package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.*;
import com.bikash.bikashBackend.View.ResponseBuilder;
import com.bikash.bikashBackend.dto.RechargeDto;
import com.bikash.bikashBackend.dto.UserDto;
import com.bikash.bikashBackend.Service.UserService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.repository.RoleRepository;
import com.bikash.bikashBackend.repository.TransactionDetailsRepository;
import com.bikash.bikashBackend.repository.UserBalanceRepository;
import com.bikash.bikashBackend.repository.UserRepository;
import com.bikash.bikashBackend.util.RoleConstraint;
import com.bikash.bikashBackend.util.UseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service("userService")
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserBalanceRepository userBalanceRepository;
    private final RoleRepository roleRepository;
    private final TransactionServiceImple transactionServiceImple;
    private final TransactionDetailsRepository transactionDetailsRepository;

    @Autowired
    public UserServiceImplement(UserRepository userRepository, ModelMapper modelMapper, UserBalanceRepository userBalanceRepository, RoleRepository roleRepository, TransactionServiceImple transactionServiceImple, TransactionDetailsRepository transactionDetailsRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userBalanceRepository = userBalanceRepository;
        this.roleRepository = roleRepository;
        this.transactionServiceImple = transactionServiceImple;
        this.transactionDetailsRepository = transactionDetailsRepository;
    }

    @Override
    public Response createUser(UserDto userDto) {
        return null;
    }//this service cover from auth service implementation

    @Override
    public User getUserByPhone(String phone) {
        return userRepository.findByPhoneAndIsActiveTrue(phone);
    }

    @Override
    public Response getAllusers() {
        return null;
    }

    /*
    for cash out user/merchant to Agent
     */
    @Override
    public Response cashOutUserOrMerchantToAgent(RechargeDto rechargeDto, HttpServletRequest request) {
        Recharge recharge = modelMapper.map(rechargeDto, Recharge.class);
        Long currentUserId = userRepository.findUserIdByPhone(SecurityContextHolder.getContext().getAuthentication().getName());
        Long isAuthenticateAgentId = userRepository.findUserIdByPhone(recharge.getPhone());
        User agentDetails = userRepository.findByPhoneAndIsActiveTrue(rechargeDto.getPhone());
        if (isAuthenticateAgentId == null) {
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "You don't have any account with this number");
        }
        if (agentDetails.getIsAgent() == false) {
            return ResponseBuilder.getFailureResponce(HttpStatus.BAD_REQUEST, "Sorry This is not a Agent Number");
        }
        Long haveAgent = userRepository.findUserIdByPhone(recharge.getPhone());

        if (haveAgent != null) {
            //now check user have enough balance or not
            UserBalance currentUserBalDetails = userBalanceRepository.findUserBalanceByUserIdAndIsActiveTrue(currentUserId);
            if (currentUserBalDetails.getBalance() >= recharge.getAmount()) {
                /*
                   #increased agent balance
                   #consume user balance
                   #add transaction and transaction details.
                 */
                //increased agent bal
                UserBalance currentAgentBal = userBalanceRepository.findUserBalanceByUserIdAndIsActiveTrue(isAuthenticateAgentId);
                currentAgentBal.setBalance(currentAgentBal.getBalance() + recharge.getAmount());//set change amout
                currentAgentBal = userBalanceRepository.save(currentAgentBal);
                if (currentAgentBal != null) {
                    //consume userBal
                    UserBalance currentUserBal = userBalanceRepository.findUserBalanceByUserIdAndIsActiveTrue(currentUserId);
                    currentUserBal.setBalance(currentUserBal.getBalance() - recharge.getAmount());//set change amount
                    currentUserBal = userBalanceRepository.save(currentUserBal);
                    if (currentUserBal != null) {
                        Transactions firstTransaction = transactionServiceImple.setTransaction("cashOutUserToAgent", new Date(), recharge.getAmount(), currentUserId, null);
                        if (firstTransaction != null) {//complete first transaction
                            Transactions sndTransaction = transactionServiceImple.setTransaction("cashOutUserToAgent", new Date(), recharge.getAmount(), haveAgent, firstTransaction.getTransactionId());
                            if (sndTransaction != null) {//complete snd transaction
                                //now set transaction details
                                TransactionDetails firstTransactionDetails = createTransactionDetailsForCashOutUserOrMerchantToAgent(sndTransaction.getTransactionId(), "cashOutUserOrMerchantToAgent", currentUserId, haveAgent, UseUtil.DEBIT);
                                if (firstTransactionDetails != null) {//complete firstTransaction details
                                    TransactionDetails sndTransactionDetails = createTransactionDetailsForCashOutUserOrMerchantToAgent(sndTransaction.getTransactionId(), "cashOutUserOrMerchantToAgent", currentUserId, haveAgent, UseUtil.CREDIT);
                                    if (sndTransactionDetails != null) {//complete snd transaction details
                                        return ResponseBuilder.getSuccessResponseForTransactions(HttpStatus.ACCEPTED, "Cash Out Successfully", sndTransaction.getTransactionId());
                                    }
                                    return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
                                }
                            }
                        }
                    }
                }
            }
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "Sorry , you don't have enough balance");
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private TransactionDetails createTransactionDetailsForCashOutUserOrMerchantToAgent(Long transactionId, String cashOutUserOrMerchantToAgent, Long debitedBy, Long creditedTo, String transactionType) {
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setTransactionId(transactionId);
        transactionDetails.setTransactionType(transactionType);
        transactionDetails.setDebitedBy(debitedBy);
        transactionDetails.setCreditedTo(creditedTo);
        transactionDetails = transactionDetailsRepository.save(transactionDetails);
        if (transactionDetails != null) {
            return transactionDetails;
        }
        return null;
    }
}
