package org.kris.invesim.marketdatams.marketdata.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kris.invesim.marketdatams.stock.domain.DailyPrice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class AlphaVantageMapper {

    public static List<DailyPrice> mapFromAlphaVantageJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);
            JsonNode timeSeries = root.path("Time Series (Daily)");

            List<DailyPrice> prices = new ArrayList<>();

            timeSeries.fieldNames().forEachRemaining(dateStr -> {
                LocalDate date = LocalDate.parse(dateStr);
                JsonNode dayData = timeSeries.get(dateStr);

                DailyPrice dailyPrice = DailyPrice.builder()
                        .date(date)
                        .open(new BigDecimal(dayData.get("1. open").asText()))
                        .high(new BigDecimal(dayData.get("2. high").asText()))
                        .low(new BigDecimal(dayData.get("3. low").asText()))
                        .close(new BigDecimal(dayData.get("4. close").asText()))
                        .volume(dayData.get("5. volume").asLong())
                        .build();

                prices.add(dailyPrice);
            });

            prices.sort(Comparator.comparing(DailyPrice::getDate));
            return prices;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AlphaVantage JSON", e);
        }
    }
}
