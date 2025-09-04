package org.kris.invesim.portfolioms.portfolio.repository;

import org.kris.invesim.portfolioms.portfolio.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {
    Optional<Position> findByPortfolio_IdAndSymbol(UUID portfolioId, String symbol);
}
