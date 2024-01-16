package com.nexusforge.rsockettradingservice.util;

import com.nexusforge.rsockettradingservice.dto.StockTradeRequest;
import com.nexusforge.rsockettradingservice.dto.StockTradeResponse;
import com.nexusforge.rsockettradingservice.dto.TradeStatus;
import com.nexusforge.rsockettradingservice.dto.TradeType;
import com.nexusforge.rsockettradingservice.dto.user.TransactionRequest;
import com.nexusforge.rsockettradingservice.dto.user.TransactionType;
import com.nexusforge.rsockettradingservice.entity.UserStock;
import org.springframework.beans.BeanUtils;

public class EntityDtoUtil {

    public static UserStock toUserStock(StockTradeRequest request) {
        UserStock stock = new UserStock();
        stock.setUserId(request.getUserId());
        stock.setStockSymbol(request.getStockSymbol());
        stock.setQuantity(0);

        return stock;
    }

    public static TransactionRequest toTransactionRequest(StockTradeRequest stockTradeRequest, int amount) {
        TransactionRequest transactionRequest = new TransactionRequest();
        TransactionType transactionType = TradeType.BUY.equals(stockTradeRequest.getTradeType()) ? TransactionType.DEBIT : TransactionType.CREDIT;

        transactionRequest.setType(transactionType);
        transactionRequest.setUserId(stockTradeRequest.getUserId());
        transactionRequest.setAmount(amount);

        return transactionRequest;
    }

    public static StockTradeResponse toTradeResponse(StockTradeRequest request, TradeStatus status, int price) {
        StockTradeResponse response = new StockTradeResponse();
        BeanUtils.copyProperties(request, response);

        response.setTradeStatus(status);
        response.setPrice(price);

        return response;
    }
}
