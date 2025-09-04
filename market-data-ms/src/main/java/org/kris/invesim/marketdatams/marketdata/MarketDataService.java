package org.kris.invesim.marketdatams.marketdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.DailyPriceDto;
import org.kris.invesim.StockDataDto;
import org.kris.invesim.marketdatams.marketdata.provider.AlphaVantageProvider;
import org.kris.invesim.marketdatams.stock.domain.DailyPrice;
import org.kris.invesim.marketdatams.stock.persistance.StockDataService;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataService {

    private final StockDataService stockDataService;
    private final AlphaVantageProvider alphaClient;

    public Optional<List<DailyPrice>> fetch(String symbol) {
        Optional<List<DailyPrice>> existing = stockDataService.getPrices(symbol);
        if (existing.isPresent()) {
            log.info("Data for {} already exists in MongoDB. Skipping HTTP fetch.", symbol);
            return existing;
        }

        Optional<List<DailyPrice>> fetched = alphaClient.fetchDailyPrices(symbol);
        fetched.ifPresent(prices -> stockDataService.savePrices(symbol, prices));
        return fetched;
    }

    public Map<String, BigDecimal> fetchLatest(List<String> symbols) {
        return symbols.stream()
                .distinct()
                .map(symbol -> stockDataService.getPrices(symbol)
                        .flatMap(prices -> prices.stream().reduce((first, second) -> second))
                        .map(DailyPrice::getClose)
                        .map(price -> Map.entry(symbol, price))
                        .orElse(Map.entry(symbol, BigDecimal.ZERO))
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public StockDataDto fetchDto(String symbol, String bar) {
        var opt = fetch(symbol);
        var prices = opt.orElseThrow(() -> new IllegalStateException("No data for symbol " + symbol))
                .stream()
                .map(this::toDto)
                .toList();
        return StockDataDto.builder()
                .symbol(symbol)
                .prices(prices)
                .build();
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



}
