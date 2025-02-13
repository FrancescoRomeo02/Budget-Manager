package com.example.budgetmanager;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.repository.TransactionRepository;
import com.example.budgetmanager.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        // Mock del repository
        transactionRepository = Mockito.mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository);
    }

    @Test
    void testAddTransaction() {
        // Dati di esempio per una transazione
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100.0);
        transaction.setType(Transaction.TransactionType.INCOME);
        
        // Mock del comportamento di save
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        
        // Test del servizio
        Transaction savedTransaction = transactionService.addTransaction(transaction);
        
        // Asserzioni
        assertNotNull(savedTransaction);
        assertEquals(100.0, savedTransaction.getAmount());
        assertEquals(Transaction.TransactionType.INCOME, savedTransaction.getType());

        // Verifica che save sia stato chiamato una volta
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testGetAllTransactions() {
        // Dati di esempio per le transazioni
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setAmount(100.0);
        t1.setType(Transaction.TransactionType.INCOME);
        
        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setAmount(50.0);
        t2.setType(Transaction.TransactionType.EXPENSE);
        
        List<Transaction> transactions = Arrays.asList(t1, t2);

        // Mock del comportamento di findAll
        when(transactionRepository.findAll()).thenReturn(transactions);
        
        // Test del servizio
        List<Transaction> result = transactionService.getAllTransactions();
        
        // Asserzioni
        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).getAmount());
        assertEquals(Transaction.TransactionType.INCOME, result.get(0).getType());
        assertEquals(50.0, result.get(1).getAmount());
        assertEquals(Transaction.TransactionType.EXPENSE, result.get(1).getType());

        // Verifica che findAll sia stato chiamato una volta
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testGetBalance() {
        // Dati di esempio per le transazioni
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setAmount(100.0);
        t1.setType(Transaction.TransactionType.INCOME);
        
        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setAmount(50.0);
        t2.setType(Transaction.TransactionType.EXPENSE);

        List<Transaction> transactions = Arrays.asList(t1, t2);

        // Mock del comportamento di findAll
        when(transactionRepository.findAll()).thenReturn(transactions);
        

        // Verifica che findAll sia stato chiamato una volta
        verify(transactionRepository, times(1)).findAll();
    }

}