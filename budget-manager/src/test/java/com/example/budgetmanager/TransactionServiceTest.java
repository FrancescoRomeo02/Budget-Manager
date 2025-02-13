package com.example.budgetmanager;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.repository.TransactionRepository;
import com.example.budgetmanager.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TransactionServiceTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        // Inizializza il servizio con il repository autowired
        transactionService = new TransactionService(transactionRepository);
    }

    @Test
    void testAddTransaction() {
        // Dati di esempio per una transazione
        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setType(Transaction.TransactionType.INCOME);
        
        // Test del servizio che salva su H2
        Transaction savedTransaction = transactionService.addTransaction(transaction);
        
        // Asserzioni
        assertNotNull(savedTransaction);
        assertEquals(100.0, savedTransaction.getAmount());
        assertEquals(Transaction.TransactionType.INCOME, savedTransaction.getType());

        // Verifica che la transazione sia effettivamente nel database
        Transaction foundTransaction = transactionRepository.findById(savedTransaction.getId()).orElse(null);
        assertNotNull(foundTransaction);
        assertEquals(transaction.getAmount(), foundTransaction.getAmount());
    }

    @Test
    void testGetAllTransactions() {
        // Dati di esempio per le transazioni
        Transaction t1 = new Transaction();
        t1.setAmount(100.0);
        t1.setCategory("Salary");
        t1.setDate(LocalDate.now());
        t1.setDescription("test");
        t1.setType(Transaction.TransactionType.INCOME);
        
        Transaction t2 = new Transaction();
        t2.setAmount(50.0);
        t2.setCategory("Food");
        t2.setDate(LocalDate.now());
        t2.setDescription("test");
        t2.setType(Transaction.TransactionType.EXPENSE);
        
        // Salvataggio nel database H2
        transactionRepository.save(t1);
        transactionRepository.save(t2);

        // Test del servizio
        List<Transaction> result = transactionService.getAllTransactions();
        
        // Asserzioni
        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).getAmount());
        assertEquals(Transaction.TransactionType.INCOME, result.get(0).getType());
        assertEquals(50.0, result.get(1).getAmount());
        assertEquals(Transaction.TransactionType.EXPENSE, result.get(1).getType());
    }

    @Test
    void testGetBalance() {
        // Dati di esempio per le transazioni
        Transaction t1 = new Transaction();
        t1.setAmount(100.0);
        t1.setType(Transaction.TransactionType.INCOME);

        Transaction t2 = new Transaction();
        t2.setAmount(50.0);
        t2.setType(Transaction.TransactionType.EXPENSE);

        // Salvataggio delle transazioni nel database H2
        transactionRepository.save(t1);
        transactionRepository.save(t2);

        // Test del servizio
        double balance = transactionService.getBalance();

        // Asserzioni
        assertEquals(50.0, balance);
    }
}