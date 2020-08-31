package com.bikash.bikashBackend.Model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class Commission extends BaseModel {
    private Long userId;//je commission passe
    private Long transactionId;
    @Column(updatable = false)
    private double commissionAmount;
    private double totalAmount;

}
