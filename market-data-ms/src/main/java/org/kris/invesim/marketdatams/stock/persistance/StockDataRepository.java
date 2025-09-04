package org.kris.invesim.marketdatams.stock.persistance;

import org.kris.invesim.marketdatams.stock.domain.StockData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockDataRepository extends MongoRepository<StockData, String> {

    Optional<StockData> findBySymbol(String symbol);

    @Query(value = "{ 'symbol': ?0 }", fields = "{ 'prices': 1, '_id': 0 }")
    Optional<StockData> findPricesBySymbol(String symbol);
}
