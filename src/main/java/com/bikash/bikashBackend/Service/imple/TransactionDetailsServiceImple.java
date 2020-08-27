package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.TransactionDetails;
import com.bikash.bikashBackend.Service.TransactionDetailsService;
import com.bikash.bikashBackend.repository.TransactionDetailsRepository;
import com.bikash.bikashBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("transactionDetailsService")
public class TransactionDetailsServiceImple implements TransactionDetailsService {
    private final TransactionDetailsRepository transactionDetailsRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionDetailsServiceImple(TransactionDetailsRepository transactionDetailsRepository, UserRepository userRepository) {
        this.transactionDetailsRepository = transactionDetailsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TransactionDetails create(Long transactionId, Long userId, double openingBalance, double transactionAmount, Date date,String transactionType) {
        if (openingBalance == 0) {
            //this is not a account opening time Transaction..This is another time Transaction
        }
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setCreatedAt(date);
        transactionDetails.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        transactionDetails.setTransactionId(transactionId);
        transactionDetails.setTransactionType(transactionType);
        transactionDetails.setDebitedBy(userRepository.findUserIdByPhone(SecurityContextHolder.getContext().getAuthentication().getName()));
        transactionDetails.setCreditedTo(userId);
        transactionDetails = transactionDetailsRepository.save(transactionDetails);
        if (transactionDetails != null) {
            return transactionDetails;
        }
        return null;
    }
}
