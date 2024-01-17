package com.nexusforge.rsockettradingservice.controller;

import com.nexusforge.rsockettradingservice.dto.StockTradeRequest;
import com.nexusforge.rsockettradingservice.dto.StockTradeResponse;
import com.nexusforge.rsockettradingservice.dto.stock.StockTickDto;
import com.nexusforge.rsockettradingservice.service.StockClient;
import com.nexusforge.rsockettradingservice.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("stock")
public class TradeController {

    @Autowired
    private TradeService service;

    @Autowired
    private StockClient stockClient;

    @GetMapping(value = "tick/strea", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StockTickDto> stockTickDtoFlux() {
        return this.stockClient.getStockStream();
    }

    @PostMapping("trade")
    public Mono<ResponseEntity<StockTradeResponse>> trade(@RequestBody Mono<StockTradeRequest> tradeRequestMono) {
        return tradeRequestMono
                .filter(tr -> tr.getQuantity() > 0)
                .flatMap(this.service::trade)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
