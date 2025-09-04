package org.kris.invesim;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDataDto {
    private String symbol;
    private List<DailyPriceDto> prices;
}