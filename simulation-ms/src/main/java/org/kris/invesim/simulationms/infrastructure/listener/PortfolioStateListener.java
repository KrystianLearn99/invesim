package org.kris.invesim.simulationms.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.MarketDataRequestedEvent;
import org.kris.invesim.PortfolioDataReadyEvent;
import org.kris.invesim.simulationms.infrastructure.producer.MarketDataRequestedEventProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioStateListener {

    private final MarketDataRequestedEventProducer marketDataRequestedEventProducer;

    @KafkaListener(
            topics = "simulation.portfolio.ready",
            groupId = "simulation-ms",
            containerFactory = "portfolioReadyKafkaListenerContainerFactory"
    )
    public void onPortfolioReady(PortfolioDataReadyEvent event) {
        MarketDataRequestedEvent out = MarketDataRequestedEvent.builder()
                .simulationId(event.getSimulationId())
                .userId(event.getUserId())
                .email(event.getEmail())
                .symbol(event.getSymbol())
                .bar(event.getBar())
                .engine(event.getEngine())
                .strategy(event.getStrategy())
                .portfolio(event.getPortfolio())
                .build();

        marketDataRequestedEventProducer.sendMarketDataRequested(out);
    }

}
