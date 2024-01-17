package com.nexusforge.rsockettradingservice.controller;

import com.nexusforge.rsockettradingservice.dto.UserStockDto;
import com.nexusforge.rsockettradingservice.dto.stock.StockTickDto;
import com.nexusforge.rsockettradingservice.dto.user.UserDto;
import com.nexusforge.rsockettradingservice.service.UserClient;
import com.nexusforge.rsockettradingservice.service.UserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserClient client;

    @Autowired
    private UserStockService userStockService;

    @GetMapping("all")
    public Flux<UserDto> allUsers() {
        return this.client.allUsers();
    }

    @GetMapping("{userId}/stocks")
    public Flux<UserStockDto> userStock(@PathVariable("userId") String userId) {
        return userStockService
                .getUserStockAss(userId);
    }

    @GetMapping("{userId}/stocksList")
    public Flux<ResponseEntity<StockTickDto>> userStockList(@PathVariable("userId") String userId) {
        return userStockService
                .getUserStock(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
