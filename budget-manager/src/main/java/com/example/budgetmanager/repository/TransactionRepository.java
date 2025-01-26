package com.example.budgetmanager.repository;

import com.example.budgetmanager.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Query personalizzate se necessario
}