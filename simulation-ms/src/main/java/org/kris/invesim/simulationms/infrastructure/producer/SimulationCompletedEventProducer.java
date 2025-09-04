package org.kris.invesim.simulationms.infrastructure.producer;

import lombok.RequiredArgsConstructor;
import org.kris.invesim.SimulationResultDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimulationCompletedEventProducer {

    private final KafkaTemplate<String, SimulationResultDto> kafkaTemplate;

    private static final String TOPIC = "simulation.completed";

    public void sendSimulationCompletedEvent(SimulationResultDto event) {
        kafkaTemplate.send(TOPIC, event.getSimulationId().toString(), event);
    }
}
