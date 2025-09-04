package org.kris.invesim.notificationms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.SimulationResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/simulation-started")
    public ResponseEntity<Void> notifySimulationStarted(@AuthenticationPrincipal Jwt jwt) {
        log.info("Użytkownik {} zgłasza rozpoczętą symulację {}", jwt.getSubject());

        String to = jwt.getClaim("email");
        String subject = "Symulacja w toku";

        String text = String.format(
                "Symulacja została rozpoczęta dla użytkownika %s.\n\n" +
                        "Szczegóły symulacji:\n" +
                        "- Strategia: ",
                jwt.getSubject()
        );

        emailService.sendEmail(to, subject, text);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/simulation-completed")
    public ResponseEntity<Void> notifySimulationCompleted(@AuthenticationPrincipal Jwt jwt,
                                                          @RequestBody SimulationResultDto dto) {
        log.info("Użytkownik {} zgłasza zakończoną symulację {}", jwt.getSubject(), dto.getSimulationId());

        String to = jwt.getClaim("email");
        String subject = "Symulacja zakończona pomyślnie";

        String text = String.format(
                "Symulacja o ID %s została zakończona dla użytkownika %s.\n\n" +
                        "Szczegóły symulacji:\n" +
                        "- Strategia: %s\n" +
                        "- Wartość oczekiwana: %.2f\n" +
                        "- P5: %.2f\n" +
                        "- Mediana: %.2f\n" +
                        "- P95: %.2f\n",
                dto.getSimulationId(),
                jwt.getSubject(),
                dto.getStrategyType(),
                dto.getExpectedFinalValue(),
                dto.getP5(),
                dto.getP50(),
                dto.getP95()
        );

        emailService.sendEmail(to, subject, text);
        return ResponseEntity.ok().build();
    }
}

