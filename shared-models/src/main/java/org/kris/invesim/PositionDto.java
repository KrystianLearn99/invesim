package org.kris.invesim;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class PositionDto {
    @NotBlank
    private String symbol;
    @DecimalMin("0.0")
    private double quantity;
}