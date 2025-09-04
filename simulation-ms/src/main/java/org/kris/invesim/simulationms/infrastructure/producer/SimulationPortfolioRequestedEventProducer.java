package org.kris.invesim.simulationms.infrastructure.producer;

import lombok.RequiredArgsConstructor;
import org.kris.invesim.SimulationPortfolioRequestedDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimulationPortfolioRequestedEventProducer {

    private final KafkaTemplate<String, SimulationPortfolioRequestedDto> kafkaTemplate;

    private static final String TOPIC = "simulation.portfolio.requested";

    public void sendSimulationPortfolioRequestedEvent(SimulationPortfolioRequestedDto event) {
        kafkaTemplate.send(TOPIC, event.getSimulationId().toString(), event);
    }
}
