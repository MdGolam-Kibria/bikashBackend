package com.bikash.bikashBackend.testApi;

import com.bikash.bikashBackend.Model.*;
import com.bikash.bikashBackend.Service.TransactionDetailsService;
import com.bikash.bikashBackend.Service.TransactionService;
import com.bikash.bikashBackend.repository.*;
import com.bikash.bikashBackend.util.UseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;

@Configuration
public class CreateRunTimeUserTest {
    private static final Logger logger = LoggerFactory.getLogger(CreateRunTimeUserTest.class.getName());
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionService transactionService;
    private final TransactionDetailsService transactionDetailsService;
    private final TransactionsRepository transactionsRepository;
    private final TransactionDetailsRepository transactionDetailsRepository;
   private final UserBalanceRepository userBalanceRepository;

    @Value("${login.username}")
    private String phone;
    @Value("${login.password}")
    private String password;

    @Autowired
    public CreateRunTimeUserTest(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, TransactionService transactionService, TransactionDetailsService transactionDetailsService, TransactionsRepository transactionsRepository, TransactionDetailsRepository transactionDetailsRepository, UserBalanceRepository userBalanceRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionService = transactionService;
        this.transactionDetailsService = transactionDetailsService;
        this.transactionsRepository = transactionsRepository;
        this.transactionDetailsRepository = transactionDetailsRepository;
        this.userBalanceRepository = userBalanceRepository;
    }

    @PostConstruct
    public void test() {
        String roleName = "ROLE_ADMIN";
        int roleExistCount = roleRepository.countByNameAndIsActiveTrue(roleName);
        Role role = null;
        if (roleExistCount == 1) {
            role = roleRepository.findByNameAndIsActiveTrue(roleName);
        } else {
            role = new Role();
            role.setName(roleName);
            role = roleRepository.save(role);
        }
        User user = userRepository.findByPhoneAndIsActiveTrue(phone);
        if (user == null) {
            user = new User();
            user.setCreatedBy("RunTimeUser");
            user.setUsername("Golam Kibria");
            user.setPassword(passwordEncoder.encode(password));
            user.setOpeningBalance(Double.parseDouble("55290"));
            user.setNid(Long.parseLong("1234567890"));
            user.setPhone(phone);
            user.setEmail("golamkibria.java@gmail.com");
        }

        user.setRoles(Arrays.asList(role));
        user = userRepository.save(user);
        if (user != null) {
            Long timestamp = System.currentTimeMillis();


            Transactions transactions = new Transactions();
            transactions.setUserId(user.getId());
            transactions.setTransactionDate(new Date());
            transactions.setTransactionRef("runtimeAdmin");
            transactions.setTransactionAmount(50000);
            transactions = transactionsRepository.save(transactions);
            if (transactions != null) {
                String uniqueTransactionId = String.valueOf(timestamp).concat(transactions.getId().toString());
                transactions.setTransactionId(Long.parseLong(uniqueTransactionId));
                transactions = transactionsRepository.save(transactions);
                if (transactions != null) {
                    TransactionDetails transactionDetails = new TransactionDetails();
                    transactionDetails.setCreditedTo(user.getId());
                    transactionDetails.setTransactionType(UseUtil.DEBIT);
                    transactionDetails.setTransactionId(transactions.getTransactionId());
                    transactionDetails = transactionDetailsRepository.save(transactionDetails);
                    if (transactionDetails != null) {
                        logger.info("admin created with transaction and transaction details");
                        UserBalance userBalance = new UserBalance();
                        userBalance.setUserId(user.getId());
                        userBalance.setBalance(50000);
                        userBalance = userBalanceRepository.save(userBalance);
                        if (userBalance!=null){
                            logger.info("user balanced added");
                        }
                    }
                }
            }
        }

    }
}

