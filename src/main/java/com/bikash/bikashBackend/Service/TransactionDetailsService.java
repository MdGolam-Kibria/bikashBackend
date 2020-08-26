package com.bikash.bikashBackend.Service;

import com.bikash.bikashBackend.Model.TransactionDetails;

import java.util.Date;

public interface TransactionDetailsService {
    TransactionDetails create(Long transactionId, Long userId, double openingBalance, double transactionAmount, Date date,String transactionType);
}
