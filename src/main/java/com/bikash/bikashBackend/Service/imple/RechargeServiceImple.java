package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.*;
import com.bikash.bikashBackend.Service.RechargeService;
import com.bikash.bikashBackend.Service.TransactionDetailsService;
import com.bikash.bikashBackend.Service.TransactionService;
import com.bikash.bikashBackend.Service.UserBalanceService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.View.ResponseBuilder;
import com.bikash.bikashBackend.dto.RechargeDto;
import com.bikash.bikashBackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service("RechargeService")
public class RechargeServiceImple implements RechargeService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final TransactionService transactionService;
    private final TransactionDetailsService transactionDetailsService;
    private final UserBalanceService userBalanceService;


    @Autowired
    public RechargeServiceImple(UserRepository userRepository, ModelMapper modelMapper, TransactionService transactionService, TransactionDetailsService transactionDetailsService, UserBalanceService userBalanceService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.transactionService = transactionService;
        this.transactionDetailsService = transactionDetailsService;
        this.userBalanceService = userBalanceService;
    }

    @Override
    public Response recharge(RechargeDto rechargeDto, HttpServletRequest request,String transactionType) {
        Recharge recharge = modelMapper.map(rechargeDto, Recharge.class);
        if (userRepository.findUserPhoneByPhone(recharge.getPhone()) == null) {
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_FOUND, "Sorry, You Dont Have Any User With This Account");
        }
        if (userRepository.findUserPhoneByPhone(recharge.getPhone()).equals(recharge.getPhone())) {
            User user = userRepository.findByPhoneAndIsActiveTrue(recharge.getPhone());
            if (user != null) {
                Response response = transactions(user, recharge, request, new Date(),transactionType);
                return response;
            }
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    public Response transactions(User user, Recharge recharge, HttpServletRequest request, Date date,String transactionType) {
        if (recharge != null) {
            Transactions transactions = transactionService.create(user.getId(), 0, recharge.getAmount(), date, SecurityContextHolder.getContext().getAuthentication().getName());
            if (transactions != null) {
                TransactionDetails transactionDetails = transactionDetailsService.create(transactions.getTransactionId(), user.getId(), 0, recharge.getAmount(), date,transactionType);
                if (transactionDetails != null) {
                    UserBalance userBalance = userBalanceService.update(user.getId(), recharge.getAmount(), date);
                    if (userBalance != null) {
                        return ResponseBuilder.getSuccessResponseForTransactions(HttpStatus.OK, "Recharge Successfully", transactions.getTransactionId());
                    }
                }
            }
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
}
