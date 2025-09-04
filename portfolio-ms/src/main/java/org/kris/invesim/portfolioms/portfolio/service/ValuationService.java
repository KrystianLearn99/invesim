package org.kris.invesim.portfolioms.portfolio.service;

import lombok.RequiredArgsConstructor;

import org.kris.invesim.portfolioms.portfolio.model.Money;
import org.kris.invesim.portfolioms.client.MarketDataClient;
import org.kris.invesim.portfolioms.portfolio.model.Portfolio;
import org.kris.invesim.portfolioms.portfolio.model.Position;
import org.kris.invesim.portfolioms.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValuationService {

    private final PortfolioRepository portfolios;
    private final MarketDataClient market;

    public ValuationResult nav(UUID userId) {
        Portfolio p = portfolios.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found or not owned by user"));

        var symbols = p.getPositions().stream().map(Position::getSymbol).distinct().toList();
        Map<String, BigDecimal> last = symbols.isEmpty()
                ? Map.of()
                : market.getQuotes(String.join(",", symbols));

        BigDecimal positionsMv = p.getPositions().stream().map(pos -> {
            BigDecimal lastPx = Money.price(last.getOrDefault(pos.getSymbol(), BigDecimal.ZERO));
            return Money.cash(lastPx.multiply(pos.getQuantity()));
        }).reduce(Money.zero(), BigDecimal::add);

        BigDecimal nav = Money.cash(p.getCash().add(positionsMv));

        return new ValuationResult(nav, Money.cash(p.getCash()),
                p.getPositions().stream().map(pos -> new Line(
                        pos.getSymbol(),
                        pos.getQuantity(),
                        Money.price(last.getOrDefault(pos.getSymbol(), BigDecimal.ZERO)),
                        Money.cash(pos.getQuantity().multiply(Money.price(last.getOrDefault(pos.getSymbol(), BigDecimal.ZERO))))
                )).collect(Collectors.toList()));
    }

    public record Line(String symbol, BigDecimal qty, BigDecimal last, BigDecimal mv) {}
    public record ValuationResult(BigDecimal nav, BigDecimal cash, java.util.List<Line> lines) {}
}
