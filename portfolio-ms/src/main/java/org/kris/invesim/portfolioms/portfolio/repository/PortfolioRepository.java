package org.kris.invesim.portfolioms.portfolio.repository;

import org.kris.invesim.portfolioms.portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    Optional<Portfolio> findByUserId(UUID userId);
    @Query("""
        select p
        from Portfolio p
        left join fetch p.positions
        where p.userId = :userId
    """)
    Optional<Portfolio> findByUserIdWithPositions(UUID userId);
    boolean existsByUserId(UUID userId);
}
