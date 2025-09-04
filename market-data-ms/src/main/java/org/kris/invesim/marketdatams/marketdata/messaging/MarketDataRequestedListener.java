package org.kris.invesim.marketdatams.marketdata.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.DailyPriceDto;
import org.kris.invesim.MarketDataReadyEvent;
import org.kris.invesim.MarketDataRequestedEvent;
import org.kris.invesim.StockDataDto;
import org.kris.invesim.marketdatams.marketdata.MarketDataService;
import org.kris.invesim.marketdatams.stock.domain.DailyPrice;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataRequestedListener {

    private final MarketDataService marketDataService;
    private final MarketDataReadyProducer readyProducer;

    @KafkaListener(
            topics = "simulation.market.data.requested",
            groupId = "market-data-ms",
            containerFactory = "getKafkaListenerContainerFactory"
    )
    public void onMarketRequested(MarketDataRequestedEvent event) {
        var prices = marketDataService.fetch(event.getSymbol()).orElse(List.of());

        var stock = StockDataDto.builder()
                .symbol(event.getSymbol())
                .prices(prices.stream().map(this::toDto).toList())
                .build();

        MarketDataReadyEvent out = MarketDataReadyEvent.builder()
                .simulationId(event.getSimulationId())
                .userId(event.getUserId())
                .email(event.getEmail())
                .symbol(event.getSymbol())
                .bar(event.getBar())
                .stock(stock)
                .engine(event.getEngine())
                .strategy(event.getStrategy())
                .portfolio(event.getPortfolio())
                .build();

        readyProducer.send(out);
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
