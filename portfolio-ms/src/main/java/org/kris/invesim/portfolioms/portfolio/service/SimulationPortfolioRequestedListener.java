package org.kris.invesim.portfolioms.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SimulationPortfolioRequestedListener {


    private final PortfolioService portfolioService;
    private final SimulationPortfolioReadyProducer simulationPortfolioReadyProducer;


    @KafkaListener(
            topics = "simulation.portfolio.requested",
            groupId = "portfolio-ms",
            containerFactory = "getKafkaListenerContainerFactory"
    )
    public void onSimulationRequested(SimulationPortfolioRequestedDto event) {
        var portfolio = portfolioService.getPortfolioSpec(event.getUserId());
        var symbol = portfolio.getPositions().getFirst().getSymbol();

        PortfolioDataReadyEvent out = PortfolioDataReadyEvent.builder()
                .simulationId(event.getSimulationId())
                .userId(event.getUserId())
                .email(event.getEmail())
                .portfolio(portfolio)
                .engine(event.getEngine())
                .strategy(StrategySpec.builder()
                        .type(event.getStrategyType())
                        .build())
                .bar(event.getBar())
                .symbol(symbol)
                .build();

        simulationPortfolioReadyProducer.sendPortfolioState(out);
    }


}
