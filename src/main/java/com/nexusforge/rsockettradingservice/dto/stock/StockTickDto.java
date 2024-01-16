package com.nexusforge.rsockettradingservice.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockTickDto {
    private String code;
    private int price;
}
