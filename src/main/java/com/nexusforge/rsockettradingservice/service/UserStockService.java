package com.nexusforge.rsockettradingservice.service;

import com.nexusforge.rsockettradingservice.dto.StockTradeRequest;
import com.nexusforge.rsockettradingservice.dto.UserStockDto;
import com.nexusforge.rsockettradingservice.dto.stock.StockTickDto;
import com.nexusforge.rsockettradingservice.entity.UserStock;
import com.nexusforge.rsockettradingservice.repository.UserStockRepository;
import com.nexusforge.rsockettradingservice.util.EntityDtoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserStockService {

    @Autowired
    private UserStockRepository repository;

    @Autowired
    private StockClient stockClient;

    //buy
    public Mono<UserStock> buyStock(StockTradeRequest request) {
        return this.repository.
                findByUserIdAndStockSymbol(request.getUserId(),
                        request.getStockSymbol())
                .defaultIfEmpty(EntityDtoUtil.toUserStock(request))
                .doOnNext(us -> us.setQuantity(us.getQuantity() + request.getQuantity()))
                .flatMap(this.repository::save);
    }

    //sell

    public Mono<UserStock> sellStock(StockTradeRequest request) {
        return this.repository.
                findByUserIdAndStockSymbol(request.getUserId(),
                        request.getStockSymbol())
                .filter(userStock -> userStock.getQuantity() >= request.getQuantity())
                .doOnNext(us -> us.setQuantity(us.getQuantity() - request.getQuantity()))
                .flatMap(this.repository::save);
    }

    public Flux<StockTickDto> getUserStock(String userId) {
        return this.repository
                .findByUserId(userId)
                .flatMap(userStock ->
                        stockClient.getStockStream()
                                .filter(stockTickDto -> userStock.getStockSymbol()
                                        .equals(stockTickDto.getCode())))
                .doOnNext(System.out::println);
    }

    public Flux<UserStockDto> getUserStockAss(String userId) {
        return this.repository
                .findByUserId(userId)
                .map(EntityDtoUtil::toUserStockDto);
    }


}
