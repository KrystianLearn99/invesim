package org.kris.invesim.marketdatams.stock.persistance;

import lombok.RequiredArgsConstructor;
import org.kris.invesim.marketdatams.stock.domain.DailyPrice;
import org.kris.invesim.marketdatams.stock.domain.StockData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockDataService {

    private final StockDataRepository stockDataRepository;

    public Optional<List<DailyPrice>> getPrices(String symbol) {
        return stockDataRepository.findBySymbol(symbol)
                .map(StockData::getPrices);
    }

    public void savePrices(String symbol, List<DailyPrice> prices) {
        StockData stockData = new StockData();
        stockData.setSymbol(symbol);
        stockData.setPrices(prices);
        stockDataRepository.save(stockData);
    }
}
