package org.kris.invesim.simulationms.infrastructure.producer;

import lombok.RequiredArgsConstructor;
import org.kris.invesim.MarketDataRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketDataRequestedEventProducer {

    private final KafkaTemplate<String, MarketDataRequestedEvent> kafkaTemplate;

    private static final String TOPIC = "simulation.market.data.requested";

    public void sendMarketDataRequested(MarketDataRequestedEvent event) {
        kafkaTemplate.send(TOPIC, event.getSimulationId().toString(), event);
    }
}
