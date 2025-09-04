package org.kris.invesim.notificationms;

import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.SimulationStartedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimulationStartedListener {

    @Autowired
    private EmailService emailService;

    @KafkaListener(
            topics = "simulation.started",
            groupId = "simulation-notification",
            containerFactory = "simulationStartedKafkaListenerContainerFactory"
    )
    public void listenSimulationStarted(SimulationStartedDto event) {
        log.info("Otrzymano event rozpoczęcia symulacji: " + event);

        log.info("Użytkownik {} zgłasza rozpoczętą symulację {}", event.getUserId(), event.getSimulationId());

        String subject = "Symulacja rozpoczęta";

        String text = String.format(
                "Symulacja została rozpoczęta dla użytkownika %s.\n\n" +
                        "Szczegóły symulacji:\n" +
                        "- Strategia:  %s",
                event.getUserId(), event.getStrategyType()
        );

        emailService.sendEmail(event.getEmail(), subject, text);
    }

}


