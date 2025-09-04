package org.kris.invesim.marketdatams.stock.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Document(collection = "stock_data")
public class StockData {
    @Id
    private String id;
    private String symbol;
    private String timezone;
    private LocalDate lastRefreshed;
    private List<DailyPrice> prices;

}



