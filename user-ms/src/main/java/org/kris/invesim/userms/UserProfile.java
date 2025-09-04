package org.kris.invesim.userms;

import jakarta.persistence.*;
import lombok.*;
import org.kris.invesim.InvestmentStrategyType;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency preferredCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentStrategyType defaultStrategy;
}



