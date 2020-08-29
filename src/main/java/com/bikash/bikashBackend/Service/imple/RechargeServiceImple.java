package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.*;
import com.bikash.bikashBackend.Service.RechargeService;
import com.bikash.bikashBackend.Service.TransactionDetailsService;
import com.bikash.bikashBackend.Service.TransactionService;
import com.bikash.bikashBackend.Service.UserBalanceService;
import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.View.ResponseBuilder;
import com.bikash.bikashBackend.dto.RechargeDto;
import com.bikash.bikashBackend.repository.TransactionDetailsRepository;
import com.bikash.bikashBackend.repository.TransactionsRepository;
import com.bikash.bikashBackend.repository.UserBalanceRepository;
import com.bikash.bikashBackend.repository.UserRepository;
import com.bikash.bikashBackend.util.UseUtil;
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
    private final UserBalanceRepository userBalanceRepository;
    private final TransactionsRepository transactionsRepository;
    private final TransactionDetailsRepository transactionDetailsRepository;


    @Autowired
    public RechargeServiceImple(UserRepository userRepository, ModelMapper modelMapper, TransactionService transactionService, TransactionDetailsService transactionDetailsService, UserBalanceService userBalanceService, UserBalanceRepository userBalanceRepository, TransactionsRepository transactionsRepository, TransactionDetailsRepository transactionDetailsRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.transactionService = transactionService;
        this.transactionDetailsService = transactionDetailsService;
        this.userBalanceService = userBalanceService;
        this.userBalanceRepository = userBalanceRepository;
        this.transactionsRepository = transactionsRepository;
        this.transactionDetailsRepository = transactionDetailsRepository;
    }

    @Override
    public Response recharge(RechargeDto rechargeDto, HttpServletRequest request, String transactionType) {
        Recharge recharge = modelMapper.map(rechargeDto, Recharge.class);
        if (userRepository.findUserPhoneByPhone(recharge.getPhone()) == null) {
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_FOUND, "Sorry, You Dont Have Any User With This Account");
        }
        if (userRepository.findUserPhoneByPhone(recharge.getPhone()).equals(recharge.getPhone())) {
            User user = userRepository.findByPhoneAndIsActiveTrue(recharge.getPhone());
            if (user != null) {
                Response response = transactions(user, recharge, request, new Date(), transactionType);
                return response;
            }
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    @Override
    public Response rechargeAgentToUserOrMerchant(RechargeDto rechargeDto) {//for agent to user/merchant recharge
        Date currentDate = new Date();
        Long timestamp = System.currentTimeMillis();
        Recharge recharge = modelMapper.map(rechargeDto, Recharge.class);
        //check  current logged in authority have enough amount or not
        Long userOrMerchant = userRepository.findUserIdByPhone(recharge.getPhone());
        Long currentLoggedAgentId = userRepository.findUserIdByPhone(SecurityContextHolder.getContext().getAuthentication().getName());
        UserBalance currentLoggedUserDetails = userBalanceRepository.findUserBalanceByUserIdAndIsActiveTrue(currentLoggedAgentId);
        if (userOrMerchant == null) {//if don't have any account with this number
            return ResponseBuilder.getFailureResponce(HttpStatus.BAD_REQUEST, "Sorry , you don't have any user/merchant with this number ");
        }
        if (userOrMerchant != 0) {//if have any account this number
            if (currentLoggedUserDetails.getBalance() >= recharge.getAmount()) {//if agent/admin have enough amount of many
                UserBalance increasedUserMerchantBal = userBalanceService.update(userOrMerchant, recharge.getAmount(), currentDate);
                if (increasedUserMerchantBal != null) {//increased user/merchant bal
                    UserBalance consumeAgentOrAdminBal = userBalanceService.consumeBalUpdate(currentLoggedAgentId, recharge.getAmount(), currentDate);
                    if (consumeAgentOrAdminBal != null) {//consume admin/agent bal
                        //balance increased and consume complete now create transaction and transactionsDetails
                        Transactions transactions = createTransactionsForAgentToUserOrMerchant(recharge, currentLoggedAgentId, currentLoggedUserDetails, userOrMerchant, currentDate, timestamp, null);
                        if (transactions != null) {//compete first transactions for user/merchant
                            Transactions sndTransaction = createTransactionsForAgentToUserOrMerchant(recharge, currentLoggedAgentId, currentLoggedUserDetails, currentLoggedAgentId, currentDate, timestamp, transactions.getTransactionId());
                            if (sndTransaction != null) {//complete snd transaction for mercent or admin
                                TransactionDetails firstTransactionDetails = createTransactionsDetailsForAgentToUserOrMerchant(currentLoggedAgentId, userOrMerchant, sndTransaction.getTransactionId(), currentDate, UseUtil.CREDIT);//jehoto admin/agent always dibe so type always credit hobe
                                if (firstTransactionDetails != null) {//complete first transaction details for credit
                                    //snd transaction details for debit
                                    createTransactionsDetailsForAgentToUserOrMerchant(currentLoggedAgentId, userOrMerchant, sndTransaction.getTransactionId(), currentDate, UseUtil.DEBIT);
                                    return ResponseBuilder.getSuccessResponseForTransactions(HttpStatus.OK, "Recharge Successfully", sndTransaction.getTransactionId());
                                }
                                return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
                            }
                        }
                    }
                }
            }
            return ResponseBuilder.getFailureResponce(HttpStatus.NOT_ACCEPTABLE, "Sorry , You don't have enough amount for recharge");
        }
        return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    /*
    for create transaction details for agent/admin to user/merchant
     */
    private TransactionDetails createTransactionsDetailsForAgentToUserOrMerchant(Long debitedBy, Long creditedTo, Long transactionId, Date currentDate, String transactionType) {
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setCreatedAt(currentDate);
        transactionDetails.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        transactionDetails.setCreditedTo(creditedTo);
        transactionDetails.setDebitedBy(debitedBy);
        transactionDetails.setTransactionId(transactionId);
        transactionDetails.setTransactionType(transactionType);
        transactionDetails = transactionDetailsRepository.save(transactionDetails);
        if (transactionDetails != null) {
            return transactionDetails;
        }
        return null;
    }

    /*
    for create transaction agent/admin to user/merchant
     */
    private Transactions createTransactionsForAgentToUserOrMerchant(Recharge recharge, Long currentLoggedUserId, UserBalance currentLoggedUserDetails, Long userId, Date currentDate, Long timestamp, Long transactionId) {

        Transactions transactions = new Transactions();
        transactions.setCreatedAt(currentDate);
        transactions.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        transactions.setTransactionAmount(recharge.getAmount());
        transactions.setTransactionDate(currentDate);
        transactions.setTransactionRef("agentToUserOrMerchant");
        transactions.setUserId(userId);
        transactions = transactionsRepository.save(transactions);
        if (transactions != null) {
            if (transactionId == null) {//if it is first transaction then make a manual transactionId
                String uniqueTransactionId = String.valueOf(timestamp).concat(transactions.getId().toString());
                transactions.setTransactionId(Long.parseLong(uniqueTransactionId));//set unique transactionsId
                transactions = transactionsRepository.save(transactions);
            } else {
                //for set snd transactionId from first transaction
                transactions.setTransactionId(transactionId);
                transactions = transactionsRepository.save(transactions);
            }
            if (transactions != null) {
                return transactions;
            }
            return null;
        }
        return null;
    }

    /*
    this transactions method  only for admin to Agent Recharge
     */
    public Response transactions(User user, Recharge recharge, HttpServletRequest request, Date date, String transactionType) {
        if (recharge != null) {
            Transactions transactions = transactionService.create(user.getId(), 0, recharge.getAmount(), date, SecurityContextHolder.getContext().getAuthentication().getName());
            if (transactions != null) {
                TransactionDetails transactionDetails = transactionDetailsService.create(transactions.getTransactionId(), user.getId(), 0, recharge.getAmount(), date, transactionType);
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
