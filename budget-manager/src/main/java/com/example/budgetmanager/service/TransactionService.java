package com.example.budgetmanager.service;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    // Metodo per aggiungere una transazione
    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    // Metodo per ottenere tutte le transazioni
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Metodo per ottenere una transazione per ID
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    // Metodo per ottenere il saldo complessivo
    public Double getBalance() {
        List<Transaction> transactions = transactionRepository.findAll();
        double balance = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                balance += transaction.getAmount();
            } else {
                balance -= transaction.getAmount();
            }
        }
        return balance;
    }
}