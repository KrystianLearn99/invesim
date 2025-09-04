package org.kris.invesim.portfolioms.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.kris.invesim.PortfolioDataReadyEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimulationPortfolioReadyProducer {

    private final KafkaTemplate<String, PortfolioDataReadyEvent> kafkaTemplate;

    private static final String TOPIC = "simulation.portfolio.ready";

    public void sendPortfolioState(PortfolioDataReadyEvent event) {
        kafkaTemplate.send(TOPIC, event.getSimulationId().toString(), event);
    }
}
