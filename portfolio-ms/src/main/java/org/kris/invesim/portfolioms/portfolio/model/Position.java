package org.kris.invesim.portfolioms.portfolio.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "positions",
        uniqueConstraints = @UniqueConstraint(name = "uq_portfolio_symbol", columnNames = {"portfolio_id","symbol"}))
@Getter
@Setter
@NoArgsConstructor
public class Position {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false, length = 40)
    private String symbol;

    @Column(nullable = false, precision = 28, scale = 10)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal avgPrice = BigDecimal.ZERO;
}


