package org.kris.invesim.marketdatams.marketdata.messaging;

import lombok.RequiredArgsConstructor;
import org.kris.invesim.MarketDataReadyEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketDataReadyProducer {

    private final KafkaTemplate<String, MarketDataReadyEvent> kafkaTemplate;

    public static final String TOPIC = "simulation.market.data.ready";

    public void send(MarketDataReadyEvent event) {
        kafkaTemplate.send(TOPIC, event.getSimulationId().toString(), event);
    }
}



