package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.TransactionDetails;
import com.bikash.bikashBackend.Service.TransactionDetailsService;
import com.bikash.bikashBackend.repository.TransactionDetailsRepository;
import com.bikash.bikashBackend.util.UseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("transactionDetailsService")
public class TransactionDetailsServiceImple implements TransactionDetailsService {
    private final TransactionDetailsRepository transactionDetailsRepository;

    @Autowired
    public TransactionDetailsServiceImple(TransactionDetailsRepository transactionDetailsRepository) {
        this.transactionDetailsRepository = transactionDetailsRepository;
    }

    @Override
    public TransactionDetails create(Long transactionId, Long userId, double openingBalance, double transactionAmount, Date date) {
        if (openingBalance == 0) {
            //this is not a account opening time Transaction..This is another time Transaction
        }
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setCreatedAt(date);
        transactionDetails.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        transactionDetails.setTransactionId(transactionId);
        transactionDetails.setTransactionType(UseUtil.CREDIT);
        transactionDetails.setDebitedBy(null);//question
        transactionDetails.setCreditedTo(userId);
        transactionDetails = transactionDetailsRepository.save(transactionDetails);
        if (transactionDetails != null) {
            return transactionDetails;
        }
        return null;
    }
}
