package org.kris.invesim;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class StrategySpec {

    @NotBlank
    private InvestmentStrategyType type;

    private Map<String, Object> params;
}

