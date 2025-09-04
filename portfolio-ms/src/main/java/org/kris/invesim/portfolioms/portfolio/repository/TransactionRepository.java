package org.kris.invesim.portfolioms.portfolio.repository;


import org.kris.invesim.portfolioms.portfolio.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> { }
