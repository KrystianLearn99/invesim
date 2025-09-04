package org.kris.invesim.notificationms;

import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.SimulationResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimulationCompletedListener {

    @Autowired
    private EmailService emailService;

    @KafkaListener(
            topics = "simulation.completed",
            groupId = "simulation-notification",
            containerFactory = "simulationCompletedKafkaListenerContainerFactory"
    )
    public void listenSimulationCompleted(SimulationResultDto event) {
        log.info("Otrzymano event zakończenia symulacji: " + event);

        log.info("Użytkownik {} zgłasza zakończoną symulację {}", event.getUserId(), event.getSimulationId());

        String subject = "Symulacja zakończona pomyślnie";

        String text = String.format(
                "Symulacja o ID %s została zakończona dla użytkownika %s.\n\n" +
                        "Szczegóły symulacji:\n" +
                        "- Strategia: %s\n" +
                        "- Wartość oczekiwana: %.2f\n" +
                        "- P5: %.2f\n" +
                        "- Mediana: %.2f\n" +
                        "- P95: %.2f\n",
                event.getSimulationId(),
                event.getUserId(),
                event.getStrategyType(),
                event.getExpectedFinalValue(),
                event.getP5(),
                event.getP50(),
                event.getP95()
        );

        emailService.sendEmail(event.getEmail(), subject, text);
    }

}


