package org.kris.invesim;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarketSpec {
    @NotEmpty
    private List<String> symbols;

    @NotBlank
    private String bar;
}
