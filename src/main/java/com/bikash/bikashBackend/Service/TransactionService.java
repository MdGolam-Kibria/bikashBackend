package com.bikash.bikashBackend.Service;

import com.bikash.bikashBackend.Model.Transactions;

import java.util.Date;

public interface TransactionService {
    Transactions create(Long id, double openingBalance, double transactionAmount, Date date,String transactionsRef);
}
