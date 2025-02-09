package com.example.budgetmanager.controller;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Aggiungere una nuova transazione
    @PostMapping
    public ResponseEntity<?> addTransaction(@RequestBody Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getType() == null) {
            return ResponseEntity.badRequest().body("Errore: importo e tipo di transazione sono obbligatori.");
        }
        Transaction savedTransaction = transactionService.addTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    // Ottenere tutte le transazioni
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactions);
    }

    // Ottenere una transazione per ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Ottenere il saldo complessivo
    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance() {
        double balance = transactionService.getBalance();
        return ResponseEntity.ok(balance);
    }

    // Gestione delle eccezioni generali
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body("Errore: " + ex.getMessage());
    }
}