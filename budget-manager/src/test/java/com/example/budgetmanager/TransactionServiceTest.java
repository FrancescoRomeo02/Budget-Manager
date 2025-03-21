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
        transaction.setType(Transaction.TransactionType.INCOME);
        transaction.setAmount(100.0);
        
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
    void testGetTransactionById() {
        // Dati di esempio per una transazione
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TransactionType.INCOME);
        transaction.setAmount(100.0);

        // Salvataggio nel database H2
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Test del servizio
        var result = transactionService.getTransactionById(savedTransaction.getId());

        // Asserzioni
        assertTrue(result.isPresent());
        assertEquals(100.0, result.get().getAmount());
        assertEquals(Transaction.TransactionType.INCOME, result.get().getType());
    }

    @Test
    void testGetTransactionByIdNotFound() {
        // Test del servizio
        var result = transactionService.getTransactionById(1L);

        // Asserzioni
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTransactionByIdNull() {
        // Test del servizio
        var result = transactionService.getTransactionById(null);

        // Asserzioni
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllTransactions() {
        // Dati di esempio per le transazioni
        Transaction t1 = new Transaction();
        t1.setType(Transaction.TransactionType.INCOME);
        t1.setAmount(100.0);
        t1.setCategory("Salary");
        t1.setDate(LocalDate.now());
        t1.setDescription("test");
        
        Transaction t2 = new Transaction();
        t2.setType(Transaction.TransactionType.EXPENSE);
        t2.setAmount(50.0);
        t2.setCategory("Food");
        t2.setDate(LocalDate.now());
        t2.setDescription("test");
        
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
        t1.setType(Transaction.TransactionType.INCOME);
        t1.setAmount(100.0);

        Transaction t2 = new Transaction();
        t2.setType(Transaction.TransactionType.EXPENSE);
        t2.setAmount(50.0);

        // Salvataggio delle transazioni nel database H2
        transactionRepository.save(t1);
        transactionRepository.save(t2);

        // Test del servizio
        double balance = transactionService.getBalance();

        // Asserzioni
        assertEquals(50.0, balance);
    }

    @Test
    void testDeleteTransaction() {
        // Dati di esempio per una transazione
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TransactionType.INCOME);
        transaction.setAmount(100.0);
        
        // Salvataggio nel database H2
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Test del servizio
        boolean result = transactionService.deleteTransaction(savedTransaction.getId());
        
        // Asserzioni
        assertTrue(result);
        assertFalse(transactionRepository.existsById(savedTransaction.getId()));
    }

    @Test
    void testGetTotalRevenue() {
        // Dati di esempio per le transazioni
        Transaction t1 = new Transaction();
        t1.setType(Transaction.TransactionType.INCOME);
        t1.setAmount(100.0);

        Transaction t2 = new Transaction();
        t2.setType(Transaction.TransactionType.INCOME);
        t2.setAmount(50.0);

        // Salvataggio delle transazioni nel database H2
        transactionRepository.save(t1);
        transactionRepository.save(t2);

        // Test del servizio
        double totalRevenue = transactionService.getTotalRevenue();

        // Asserzioni
        assertEquals(150.0, totalRevenue);
    }

    @Test
    void testGetTotalExpenses() {
        // Dati di esempio per le transazioni
        Transaction t1 = new Transaction();
        t1.setType(Transaction.TransactionType.EXPENSE);
        t1.setAmount(100.0);

        Transaction t2 = new Transaction();
        t2.setType(Transaction.TransactionType.EXPENSE);
        t2.setAmount(50.0);

        // Salvataggio delle transazioni nel database H2
        transactionRepository.save(t1);
        transactionRepository.save(t2);

        // Test del servizio
        double totalExpenses = transactionService.getTotalExpenses();

        // Asserzioni
        assertEquals(150.0, totalExpenses);
    }

    @Test
    void testGetExpenseCategorySummary() {
        // Dati
        Transaction t1 = new Transaction();
        t1.setType(Transaction.TransactionType.EXPENSE);
        t1.setAmount(100.0);
        t1.setCategory("Food");

        Transaction t2 = new Transaction();
        t2.setType(Transaction.TransactionType.EXPENSE);
        t2.setAmount(50.0);
        t2.setCategory("Transport");

        // Salvataggio delle transazioni nel database H2
        transactionRepository.save(t1);
        transactionRepository.save(t2);

        // Test del servizio
        var result = transactionService.getExpenseCategorySummary();

        // Asserzioni
        assertEquals(2, result.size());
        assertEquals(100.0, result.get("Food"));
        assertEquals(50.0, result.get("Transport"));

    }   

}