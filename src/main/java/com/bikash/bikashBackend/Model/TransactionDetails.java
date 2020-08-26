package com.bikash.bikashBackend.Model;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class TransactionDetails extends BaseModel {
    private Long transactionId;
    private String transactionType;
    private Long debitedBy;
    private Long creditedTo;
}
