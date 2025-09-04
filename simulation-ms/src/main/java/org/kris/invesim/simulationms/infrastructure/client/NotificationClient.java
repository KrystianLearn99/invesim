package org.kris.invesim.simulationms.infrastructure.client;

import org.kris.invesim.SimulationResultDto;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
@HttpExchange(url = "/api/notification", accept = "application/json", contentType = "application/json")
public interface NotificationClient {

    @PostExchange("/simulation-started")
    void sendSimulationStarted(@RequestHeader("Authorization") String bearerToken);

    @PostExchange("/simulation-completed")
    void sendSimulationCompleted(@RequestHeader("Authorization") String bearerToken,
                                 @RequestBody SimulationResultDto dto);
}
