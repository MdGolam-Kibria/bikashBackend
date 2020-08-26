package com.bikash.bikashBackend.Model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@Entity
public class Transactions extends BaseModel {
    private Long transactionId;
    private String transactionRef;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date transactionDate;
    private double transactionAmount;
    private Long userId;

}
