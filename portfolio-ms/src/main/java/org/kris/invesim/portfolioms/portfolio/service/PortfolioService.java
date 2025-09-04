package org.kris.invesim.portfolioms.portfolio.service;

import lombok.RequiredArgsConstructor;

import org.kris.invesim.PortfolioSpec;
import org.kris.invesim.PositionDto;
import org.kris.invesim.TransactionType;
import org.kris.invesim.portfolioms.portfolio.model.Money;
import org.kris.invesim.portfolioms.portfolio.model.Portfolio;
import org.kris.invesim.portfolioms.portfolio.model.Position;
import org.kris.invesim.portfolioms.portfolio.model.Transaction;
import org.kris.invesim.portfolioms.portfolio.repository.PortfolioRepository;
import org.kris.invesim.portfolioms.portfolio.repository.PositionRepository;
import org.kris.invesim.portfolioms.portfolio.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public PortfolioSpec getPortfolioSpec(UUID userId) {
        Portfolio portfolio = portfolioRepository.findByUserIdWithPositions(userId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found for userId: " + userId));

        PortfolioSpec spec = new PortfolioSpec();
        spec.setCash(portfolio.getCash().doubleValue());

        List<PositionDto> positions = portfolio.getPositions() == null
                ? List.of()
                : portfolio.getPositions().stream()
                .map(p -> new PositionDto(p.getSymbol(), p.getQuantity().doubleValue()))
                .toList();

        spec.setPositions(positions);
        return spec;
    }

    @Transactional
    public UUID createPortfolio(UUID userId, BigDecimal initialCash) {
        if (portfolioRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Portfolio already exists for userId: " + userId);
        }
        Portfolio p = new Portfolio();
        p.setUserId(userId);
        p.setCash(Money.cash(Money.safe(initialCash)));
        portfolioRepository.save(p);

        if (p.getCash().signum() > 0) {
            Transaction tx = new Transaction();
            tx.setPortfolio(p);
            tx.setType(TransactionType.DEPOSIT);
            tx.setAmount(p.getCash());
            tx.setOccurredAt(Instant.now());
            transactionRepository.save(tx);
        }
        return p.getId();
    }

    @Transactional
    public void deposit(UUID userId, BigDecimal amount) {
        Portfolio p = getOwned(userId);
        BigDecimal amt = Money.cash(amount);
        if (amt.signum() <= 0) throw new IllegalArgumentException("Deposit must be > 0");

        p.setCash(p.getCash().add(amt));
        Transaction tx = new Transaction();
        tx.setPortfolio(p);
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(amt);
        transactionRepository.save(tx);
    }

    @Transactional
    public void withdraw(UUID userId, BigDecimal amount) {
        Portfolio p = getOwned(userId);
        BigDecimal amt = Money.cash(amount);
        if (amt.signum() <= 0) throw new IllegalArgumentException("Withdraw must be > 0");
        if (p.getCash().compareTo(amt) < 0) throw new IllegalStateException("Insufficient cash");

        p.setCash(p.getCash().subtract(amt));
        Transaction tx = new Transaction();
        tx.setPortfolio(p);
        tx.setType(TransactionType.WITHDRAW);
        tx.setAmount(amt);
        transactionRepository.save(tx);
    }

    @Transactional
    public void buy(UUID userId, String symbol, BigDecimal quantity, BigDecimal price) {
        Portfolio p = getOwned(userId);
        BigDecimal qty = Money.qty(quantity);
        BigDecimal px  = Money.price(price);
        if (qty.signum() <= 0 || px.signum() <= 0) throw new IllegalArgumentException("qty/price must be > 0");

        BigDecimal cost = Money.cash(px.multiply(qty));
        if (p.getCash().compareTo(cost) < 0) throw new IllegalStateException("Insufficient cash");

        Position pos = positionRepository.findByPortfolio_IdAndSymbol(p.getId(), symbol).orElseGet(() -> {
            Position ne = new Position();
            ne.setPortfolio(p);
            ne.setSymbol(symbol);
            ne.setQuantity(Money.qty(BigDecimal.ZERO));
            ne.setAvgPrice(Money.price(BigDecimal.ZERO));
            return ne;
        });

        BigDecimal oldQty = pos.getQuantity();
        BigDecimal newQty = Money.qty(oldQty.add(qty));
        BigDecimal oldCost = pos.getAvgPrice().multiply(oldQty);
        BigDecimal newCost = oldCost.add(px.multiply(qty));
        BigDecimal newAvg = newQty.signum() == 0 ? Money.price(BigDecimal.ZERO)
                : Money.price(newCost.divide(newQty, Money.SCALE_PRICE, Money.RM));

        pos.setQuantity(newQty);
        pos.setAvgPrice(newAvg);
        positionRepository.save(pos);

        p.setCash(p.getCash().subtract(cost));

        Transaction tx = new Transaction();
        tx.setPortfolio(p);
        tx.setType(TransactionType.BUY);
        tx.setSymbol(symbol);
        tx.setQuantity(qty);
        tx.setPrice(px);
        tx.setAmount(cost);
        transactionRepository.save(tx);
    }

    @Transactional
    public void sell(UUID userId, String symbol, BigDecimal quantity, BigDecimal price) {
        Portfolio p = getOwned(userId);
        BigDecimal qty = Money.qty(quantity);
        BigDecimal px  = Money.price(price);
        if (qty.signum() <= 0 || px.signum() <= 0) throw new IllegalArgumentException("qty/price must be > 0");

        Position pos = positionRepository.findByPortfolio_IdAndSymbol(p.getId(), symbol)
                .orElseThrow(() -> new IllegalStateException("No such position"));

        if (pos.getQuantity().compareTo(qty) < 0) throw new IllegalStateException("Not enough quantity");

        BigDecimal proceeds = Money.cash(px.multiply(qty));
        BigDecimal remaining = Money.qty(pos.getQuantity().subtract(qty));
        pos.setQuantity(remaining);

        positionRepository.save(pos);

        p.setCash(p.getCash().add(proceeds));

        Transaction tx = new Transaction();
        tx.setPortfolio(p);
        tx.setType(TransactionType.SELL);
        tx.setSymbol(symbol);
        tx.setQuantity(qty);
        tx.setPrice(px);
        tx.setAmount(proceeds);
        transactionRepository.save(tx);
    }

    private Portfolio getOwned(UUID userId) {
        return portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found or not owned by user"));
    }
}
