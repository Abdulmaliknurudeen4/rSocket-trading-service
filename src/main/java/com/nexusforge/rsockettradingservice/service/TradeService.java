package com.nexusforge.rsockettradingservice.service;

import com.nexusforge.rsockettradingservice.dto.StockTradeRequest;
import com.nexusforge.rsockettradingservice.dto.StockTradeResponse;
import com.nexusforge.rsockettradingservice.dto.TradeStatus;
import com.nexusforge.rsockettradingservice.dto.TradeType;
import com.nexusforge.rsockettradingservice.dto.user.TransactionRequest;
import com.nexusforge.rsockettradingservice.dto.user.TransactionStatus;
import com.nexusforge.rsockettradingservice.util.EntityDtoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TradeService {

    @Autowired
    private UserStockService stockService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private StockClient stockClient;


    public Mono<StockTradeResponse> trade(StockTradeRequest tradeRequest) {
        TransactionRequest transactionRequest = EntityDtoUtil.toTransactionRequest(tradeRequest, this.estimatePrice(tradeRequest));
        Mono<StockTradeResponse> responseMono = TradeType.BUY.equals(tradeRequest.getTradeType()) ?
                buyStock(tradeRequest, transactionRequest) : sellProcess(tradeRequest, transactionRequest);

        return responseMono
                .defaultIfEmpty(EntityDtoUtil.toTradeResponse(tradeRequest, TradeStatus.FAILED, 0));
    }

    private Mono<StockTradeResponse> buyStock(StockTradeRequest request, TransactionRequest transactionRequest) {
        return this.userClient.doTransaction(transactionRequest)
                .filter(transactionResponse -> TransactionStatus.COMPLETED.equals(transactionResponse.getStatus()))
                .flatMap(transactionResponse -> this.stockService.buyStock(request))
                .map(userStock -> EntityDtoUtil.toTradeResponse(request, TradeStatus.COMPLETED, transactionRequest.getAmount()));
    }

    private Mono<StockTradeResponse> sellProcess(StockTradeRequest request, TransactionRequest transactionRequest) {
        return this.stockService.sellStock(request)
                .flatMap(userStock -> this.userClient.doTransaction(transactionRequest))
                .map(tr -> EntityDtoUtil.toTradeResponse(request, TradeStatus.COMPLETED, transactionRequest.getAmount()));

    }

    private int estimatePrice(StockTradeRequest request) {
        return request.getQuantity() * this.stockClient.getCurrentStockPrice(request
                .getStockSymbol());
    }
}
