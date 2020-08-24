package com.bikash.bikashBackend.Service;

import com.bikash.bikashBackend.Model.UserBalance;

import java.util.Date;

public interface UserBalanceService {
    UserBalance create(Long userId, double balance, Date createdAt);
}
