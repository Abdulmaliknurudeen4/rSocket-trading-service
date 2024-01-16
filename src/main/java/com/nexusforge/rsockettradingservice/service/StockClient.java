package com.nexusforge.rsockettradingservice.service;

import com.nexusforge.rsockettradingservice.dto.stock.StockTickDto;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class StockClient {

    private final RSocketRequester requester;
    private Flux<StockTickDto> flux;

    private final Map<String, Integer> map;

    public StockClient(RSocketRequester.Builder builder,
                       RSocketConnectorConfigurer connectorConfigurer,
                       @Value("${stock.service.host}") String host,
                       @Value("${stock.service.port}") int port) {
        this.requester = builder
                .rsocketConnector(connectorConfigurer)
                .transport(TcpClientTransport.create(host, port));

        this.initialize();
        this.map = new HashMap<>();
    }

    public Flux<StockTickDto> getStockStream() {
        return this.flux;
    }

    private void initialize() {
        this.flux = this.requester.route("stock.ticks")
                .retrieveFlux(StockTickDto.class)
                .doOnNext(s -> map.put(s.getCode(), s.getPrice()))
                // stream level retyr logic
                .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2)))
                .publish()
                .autoConnect(0);
    }

    public int getCurrentStockPrice(String stokcSymbol) {
        return this.map.getOrDefault(stokcSymbol, 0);
    }


}
