package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.Transactions;
import com.bikash.bikashBackend.Service.TransactionService;
import com.bikash.bikashBackend.repository.TransactionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("transactionService")
public class TransactionServiceImple implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImple.class);
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public TransactionServiceImple(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    @Override
    public Transactions create(Long userId, double openingBalance, double transactionAmount, Date date, String transactionsRef) {
        if (openingBalance == 0) {
            //this is not a account opening time Transaction..This is another time Transaction
            Transactions transactions = setTransaction(transactionsRef, date, transactionAmount, userId);
            if (transactions != null) {
                return transactions;
            }
            return null;
        }
        Transactions transactions = setTransaction(transactionsRef, date, openingBalance, userId);
        if (transactions != null) {
            return transactions;
        }
        return null;
    }

    private Transactions setTransaction(String transactionsRef, Date date, double transactionAmount, Long userId) {
        Transactions transactions = new Transactions();
        transactions.setCreatedAt(date);
        transactions.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        transactions.setTransactionRef(transactionsRef);
        transactions.setTransactionDate(date);
        transactions.setTransactionAmount(transactionAmount);
        transactions.setUserId(userId);
        transactions = transactionsRepository.save(transactions);
        if (transactions != null) {
                Long timestamp = System.currentTimeMillis();
                String uniqueTransactionId = String.valueOf(timestamp).concat(transactions.getId().toString());
                transactions.setTransactionId(Long.parseLong(uniqueTransactionId));
                transactions = transactionsRepository.save(transactions);//update currentTransaction for set unique Transaction id
                if (transactions != null) {
                    return transactions;
                }
        }
        return null;
    }
}
