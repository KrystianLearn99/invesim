package org.kris.invesim.simulationms.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.MarketDataReadyEvent;
import org.kris.invesim.SimulationResultDto;
import org.kris.invesim.SimulationStartedDto;
import org.kris.invesim.simulationms.domain.service.SimulationService;
import org.kris.invesim.simulationms.infrastructure.producer.SimulationCompletedEventProducer;
import org.kris.invesim.simulationms.infrastructure.producer.SimulationStartedEventProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataReadyListener {

    private final SimulationService simulationService;
    private final SimulationCompletedEventProducer completedProducer;
    private final SimulationStartedEventProducer startedEventProducer;


    @KafkaListener(
            topics = "simulation.market.data.ready",
            groupId = "simulation-ms",
            containerFactory = "marketDataReadyKafkaListenerContainerFactory"
    )
    public void onMarketReady(MarketDataReadyEvent event) {
        UUID simId = event.getSimulationId();
        log.info("MarketDataReady received: simId={}, symbol={}", simId, event.getSymbol());

        var started = SimulationStartedDto.builder()
                .simulationId(simId)
                .email(event.getEmail())
                .userId(event.getUserId())
                .symbol(event.getSymbol())
                .strategyType(event.getStrategy() != null ? event.getStrategy().getType() : null)
                .build();

        startedEventProducer.sendSimulationStartedEvent(started);
        SimulationResultDto result = simulationService.runFromFatEvent(simId, event);

        SimulationResultDto out = SimulationResultDto.builder()
                .simulationId(simId)
                .userId(event.getUserId())
                .email(event.getEmail())
                .strategyType(result.getStrategyType())
                .expectedFinalValue(result.getExpectedFinalValue())
                .p5(result.getP5())
                .p50(result.getP50())
                .p95(result.getP95())
                .var95Loss(result.getVar95Loss())
                .cvar95Loss(result.getCvar95Loss())
                .expectedReturnPct(result.getExpectedReturnPct())
                .p5ReturnPct(result.getP5ReturnPct())
                .p50ReturnPct(result.getP50ReturnPct())
                .p95ReturnPct(result.getP95ReturnPct())
                .navSample(result.getNavSample())
                .build();

        completedProducer.sendSimulationCompletedEvent(out);
        log.info("SimulationCompleted sent: simId={}", simId);
    }

}
