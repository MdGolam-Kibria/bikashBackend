package com.bikash.bikashBackend.Service.imple;

import com.bikash.bikashBackend.Model.TransactionDetails;
import com.bikash.bikashBackend.Model.Transactions;
import com.bikash.bikashBackend.Model.UserBalance;
import com.bikash.bikashBackend.Service.CashOutService;
import com.bikash.bikashBackend.Service.UserBalanceService;
import com.bikash.bikashBackend.repository.TransactionDetailsRepository;
import com.bikash.bikashBackend.repository.TransactionsRepository;
import com.bikash.bikashBackend.repository.UserBalanceRepository;
import com.bikash.bikashBackend.util.UseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("CashOutService")
public class CashOutServiceImple implements CashOutService {
    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceService userBalanceService;
    private final TransactionsRepository transactionsRepository;
    private final TransactionDetailsRepository transactionDetailsRepository;

    @Autowired
    public CashOutServiceImple(UserBalanceRepository userBalanceRepository, UserBalanceService userBalanceService, TransactionsRepository transactionsRepository, TransactionDetailsRepository transactionDetailsRepository) {
        this.userBalanceRepository = userBalanceRepository;
        this.userBalanceService = userBalanceService;
        this.transactionsRepository = transactionsRepository;
        this.transactionDetailsRepository = transactionDetailsRepository;
    }

    @Override
    public TransactionDetails cashOutAgentToAdmin(Long adminId, Long agentId, double taka, Date date) {
        Date currentDate = new Date();
        UserBalance adminBalDetails = userBalanceRepository.findUserBalanceByUserIdAndIsActiveTrue(adminId);
        UserBalance agentBalDetails = userBalanceRepository.findUserBalanceByUserIdAndIsActiveTrue(agentId);
        //now increased the admin balance
        UserBalance increasedAdminBal = userBalanceService.update(adminBalDetails.getUserId(), taka, currentDate);
        if (increasedAdminBal != null) {
            //now consume the agent bal
            Long timestamp = System.currentTimeMillis();
            UserBalance consumeAgentBal = userBalanceService.consumeBalUpdate(agentId, taka, currentDate);
            if (consumeAgentBal != null) {
                Transactions firstTransactions = createTransactionForCashoutAgentToAdmin(taka, adminId, date, timestamp);
                if (firstTransactions != null) {//complete first transaction
                    Transactions sndTransaction = createTransactionForCashoutAgentToAdmin(taka, agentId, date, timestamp);
                    if (sndTransaction != null) {//complete snd transaction
                        //now set transaction details
                        TransactionDetails firstTransactionDetails = createTransactionDetailsForCashoutAgentToAdmin(sndTransaction.getTransactionId(), UseUtil.DEBIT, agentId, adminId);
                        if (firstTransactionDetails != null) {//complete 1st transaction details
                            TransactionDetails sndTransactionDetails = createTransactionDetailsForCashoutAgentToAdmin(sndTransaction.getTransactionId(), UseUtil.CREDIT, agentId, adminId);
                            if (sndTransactionDetails != null) {//complete 2nd transaction details
                                return sndTransactionDetails;
                            }
                            return null;
                        }
                    }
                }

            }
        }
        return null;
    }

    private TransactionDetails createTransactionDetailsForCashoutAgentToAdmin(Long transactionId, String transactionType, Long debitedBy, Long creditedTo) {
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

    private Transactions createTransactionForCashoutAgentToAdmin(double taka, Long userId, Date date, Long timestamp) {

        Transactions transactions = new Transactions();
        transactions.setTransactionRef("cashOutAgentToAdmin");
        transactions.setTransactionDate(date);
        transactions.setTransactionAmount(taka);
        transactions.setUserId(userId);
        transactions = transactionsRepository.save(transactions);
        if (transactions != null) {
            String uniqueTransactionId = String.valueOf(timestamp).concat(transactions.getId().toString());
            transactions.setTransactionId(Long.parseLong(uniqueTransactionId));
            transactions = transactionsRepository.save(transactions);
            if (transactions != null) {
                return transactions;
            }
            return null;
        }
        return null;
    }
}
