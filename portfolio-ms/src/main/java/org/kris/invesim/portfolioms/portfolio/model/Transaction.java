package org.kris.invesim.portfolioms.portfolio.model;

import jakarta.persistence.*;
import lombok.*;
import org.kris.invesim.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_tx_portfolio_date", columnList = "portfolio_id, occurredAt")
})
@Getter @Setter @NoArgsConstructor
public class Transaction {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TransactionType type;

    @Column(length = 40)
    private String symbol;

    @Column(precision = 28, scale = 10)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 6)
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant occurredAt = Instant.now();
}


