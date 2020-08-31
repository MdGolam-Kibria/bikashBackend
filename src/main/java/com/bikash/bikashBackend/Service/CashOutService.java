package com.bikash.bikashBackend.Service;

import com.bikash.bikashBackend.Model.TransactionDetails;

import java.util.Date;

public interface CashOutService {
     TransactionDetails cashOutAgentToAdmin(Long adminId, Long agentId, double taka,double totalTaka ,Date date);
}
