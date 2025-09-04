package org.kris.invesim.simulationms.api.dto;


import java.util.UUID;

public record SimulationAcceptedDto(UUID simulationId, String status) {}
