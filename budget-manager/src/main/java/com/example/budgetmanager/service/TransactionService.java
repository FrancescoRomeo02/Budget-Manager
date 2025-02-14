package com.example.budgetmanager.service;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    // Constructor injection
    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Aggiunge una transazione
    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    // Recupera una transazione per ID
    public Optional<Transaction> getTransactionById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return transactionRepository.findById(id);
    }

    // Recupera tutte le transazioni
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        // Ordina le transazioni per data decrescente
        transactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        
        return transactions;
    }

    // Calcola il balance totale
    public double getBalance() {
        return getTotalRevenue() - getTotalExpenses();
    }

    // Elimina una transazione per ID
    public boolean deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Ottieni un riepilogo per categoria delle spese
    public Map<String, Double> getExpenseCategorySummary() {
        return transactionRepository.getExpenseCategorySummary().stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0], // categoria della transazione
                        obj -> (Double) obj[1] // somma delle spese per categoria
                ));
    }

    // Calcola il totale delle entrate
    public double getTotalRevenue() {
        return Optional.ofNullable(transactionRepository.getTotalRevenue()).orElse(0.0);
    }

    // Calcola il totale delle spese
    public double getTotalExpenses() {
        return Optional.ofNullable(transactionRepository.getTotalExpenses()).orElse(0.0);
    }
    
}