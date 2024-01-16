package com.nexusforge.rsockettradingservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    private String userId;
    private int amount;
    private TransactionType type;
}
