package com.bikash.bikashBackend.Model;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class UserBalance extends BaseModel {
    private Long userId;
    private double balance;
}
