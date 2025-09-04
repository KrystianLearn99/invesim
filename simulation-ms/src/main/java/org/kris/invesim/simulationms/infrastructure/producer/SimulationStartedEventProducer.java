package org.kris.invesim.simulationms.infrastructure.producer;

import lombok.RequiredArgsConstructor;
import org.kris.invesim.SimulationStartedDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimulationStartedEventProducer {

    private final KafkaTemplate<String, SimulationStartedDto> kafkaTemplate;

    private static final String TOPIC = "simulation.started";

    public void sendSimulationStartedEvent(SimulationStartedDto event) {
        kafkaTemplate.send(TOPIC, event.getSimulationId().toString(), event);
    }
}
