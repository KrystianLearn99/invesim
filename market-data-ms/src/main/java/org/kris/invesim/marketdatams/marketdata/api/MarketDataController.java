package org.kris.invesim.marketdatams.marketdata.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.DailyPriceDto;
import org.kris.invesim.MarketPreparedCallback;
import org.kris.invesim.PrepareMarketRequest;
import org.kris.invesim.StockDataDto;
import org.kris.invesim.marketdatams.marketdata.MarketDataService;
import org.kris.invesim.marketdatams.stock.domain.DailyPrice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/market-data")
public class MarketDataController {

    private final MarketDataService marketDataService;
    private final RestClient restClient;


    @GetMapping("/{symbol}/history")
    public ResponseEntity<StockDataDto> getHistory(@PathVariable String symbol) {
        log.info("Fetching history for symbol CONTROLLER: {}", symbol);

        Optional<List<DailyPrice>> optionalResponse = marketDataService.fetch(symbol);

        return optionalResponse
                .map(list -> ResponseEntity.ok(
                        StockDataDto.builder()
                                .symbol(symbol)
                                .prices(list.stream()
                                        .map(this::toDto)
                                        .collect(Collectors.toList()))
                                .build()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private DailyPriceDto toDto(DailyPrice dp) {
        return DailyPriceDto.builder()
                .date(dp.getDate())
                .open(dp.getOpen())
                .high(dp.getHigh())
                .low(dp.getLow())
                .close(dp.getClose())
                .volume(dp.getVolume())
                .build();
    }

    @GetMapping("/quotes")
    public ResponseEntity<Map<String, BigDecimal>> getQuotes(@RequestParam String symbols) {
        List<String> symbolList = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .toList();

        Map<String, BigDecimal> quotes = marketDataService.fetchLatest(symbolList);

        return ResponseEntity.ok(quotes);
    }

    @PostMapping("/prepare")
    public ResponseEntity<Void> prepare(@AuthenticationPrincipal Jwt jwt,
            @RequestBody PrepareMarketRequest req) {
        String symbol = req.symbol();
        String bar    = req.bar() == null ? "1d" : req.bar();
        log.info("Prepare market data: simId={}, symbol={}, bar={}", req.simulationId(), symbol, bar);


        StockDataDto stock = marketDataService.fetchDto(symbol, bar);

        MarketPreparedCallback cb = MarketPreparedCallback.builder()
                .simulationId(req.simulationId())
                .userId(req.userId())
                .email(req.email())
                .symbol(symbol)
                .bar(bar)
                .stock(stock)
                .engine(req.engine())
                .strategy(req.strategy())
                .portfolio(req.portfolio())
                .build();

        restClient.post()
                .uri(req.callbackUrl())
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .body(cb)
                .retrieve()
                .toBodilessEntity();


        log.info("Market data prepared & callback sent: simId={}, symbol={}", req.simulationId(), symbol);
        return ResponseEntity.accepted().build();
    }

}
