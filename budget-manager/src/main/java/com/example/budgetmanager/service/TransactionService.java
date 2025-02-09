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
        return transactionRepository.findById(id);
    }

    // Recupera tutte le transazioni
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Calcola il saldo totale
    public double getBalance() {
        return transactionRepository.findAll().stream()
                .mapToDouble(this::calculateAmount)
                .sum();
    }

    // Elimina una transazione per ID
    public boolean deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Ottieni un riepilogo per categoria
    public Map<String, Double> getCategorySummary() {
        return transactionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(this::calculateAmount)
                ));
    }

    // Ottieni le etichette delle date in ordine
    public List<String> getDateLabels() {
        return transactionRepository.findAll().stream()
                .map(Transaction::getDate)
                .distinct()
                .sorted()
                .map(java.time.LocalDate::toString)
                .collect(Collectors.toList());
    }

    // Metodo privato per calcolare l'importo in base al tipo di transazione
    private double calculateAmount(Transaction transaction) {
        return transaction.getType() == Transaction.TransactionType.ENTRATA
                ? transaction.getAmount()
                : -transaction.getAmount();
    }
}