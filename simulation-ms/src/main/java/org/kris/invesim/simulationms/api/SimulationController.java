package org.kris.invesim.simulationms.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kris.invesim.MarketPreparedCallback;
import org.kris.invesim.SimulationResultDto;
import org.kris.invesim.simulationms.api.dto.*;

import org.kris.invesim.simulationms.domain.model.SimulationResult;
import org.kris.invesim.simulationms.domain.service.SimulationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/simulation")
@RequiredArgsConstructor
@Validated
public class SimulationController {

    private final SimulationService simulationService;


    @PostMapping(value = "/manual/sync",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimulationResultDto> simulateManualSync(@Valid @RequestBody SimulationRequestDto req,
                                                                  @AuthenticationPrincipal Jwt jwt) {
        UUID simulationId = UUID.randomUUID();
        SimulationResult result = simulationService.run(req, jwt, simulationId);
        return ResponseEntity.ok(toDto(result));
    }

    @PostMapping(value = "/manual/event",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimulationAcceptedDto> simulateManualEvent(@Valid @RequestBody SimulationRequestDto req,
                                                                     @AuthenticationPrincipal Jwt jwt) {
        UUID simulationId = simulationService.startManualEvent(req, jwt);
        return ResponseEntity.accepted().body(new SimulationAcceptedDto(simulationId, "PENDING"));
    }


    @PostMapping(value = "/portfolio/sync",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimulationResultDto> simulatePortfolioSync(@Valid @RequestBody SimulationRequestForPortfolioDto req,
                                                                     @AuthenticationPrincipal Jwt jwt) {
        SimulationResult result = simulationService.simulateForExistingPortfolioHttpSync(req, jwt);
        return ResponseEntity.ok(toDto(result));
    }

    @PostMapping(value = "/portfolio/event",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimulationAcceptedDto> simulatePortfolioEvent(@Valid @RequestBody SimulationRequestForPortfolioDto req,
                                                                        @AuthenticationPrincipal Jwt jwt) {
        UUID simulationId = simulationService.startPortfolioEvent(req, jwt);
        return ResponseEntity.accepted().body(new SimulationAcceptedDto(simulationId, "PENDING"));
    }


    private SimulationResultDto toDto(SimulationResult result) {
        return SimulationResultDto.builder()
                .simulationId(result.getRunId())
                .strategyType(result.getStrategyType())
                .expectedFinalValue(result.getExpectedFinalValue())
                .p5(result.getP5())
                .p50(result.getP50())
                .p95(result.getP95())
                .var95Loss(result.getVar95Loss())
                .cvar95Loss(result.getCvar95Loss())
                .expectedReturnPct(result.getExpectedReturnPct())
                .p5ReturnPct(result.getP5ReturnPct())
                .p50ReturnPct(result.getP50ReturnPct())
                .p95ReturnPct(result.getP95ReturnPct())
                .navSample(result.getNavSample())
                .build();
    }
}
