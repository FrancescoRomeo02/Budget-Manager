package com.example.budgetmanager;

import com.example.budgetmanager.controller.TransactionController;
import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private TransactionService transactionService;

    @Test
    void testAddTransaction() throws Exception {
        // Mock di una transazione
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType(Transaction.TransactionType.INCOME);
        transaction.setAmount(100.0);
        transaction.setCategory("Salary");
        transaction.setDescription("Monthly salary");

        // Simulazione del comportamento del servizio
        when(transactionService.addTransaction(Mockito.any(Transaction.class))).thenReturn(transaction);

        // Richiesta POST per aggiungere una transazione
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 100.0, \"type\": \"INCOME\", \"category\": \"Salary\", \"description\": \"Monthly salary\"}"))
                .andExpect(status().isCreated()) // Aspettati un codice di stato 201
                .andExpect(jsonPath("$.id").value(1L)); // Aspettati un ID di transazione
    }

    @Test
    void testAddTransactionWithInvalidData() throws Exception {
        // Dati non validi (importo negativo)
        mockMvc.perform(post("/api/transactions")
                .contentType("application/json")
                .content("{\"amount\":, \"type\": \"INCOME\"}"))
                .andExpect(status().isBadRequest()); // Aspettati un errore
    }

    @Test
    void testGetAllTransactions() throws Exception {
        // Mock di una lista di transazioni
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setType(Transaction.TransactionType.INCOME);
        t1.setAmount(100.0);
        t1.setCategory("Salary");
        t1.setDescription("Monthly salary");

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setType(Transaction.TransactionType.EXPENSE);
        t2.setAmount(50.0);
        t2.setCategory("Food");
        t2.setDescription("Groceries");

        when(transactionService.getAllTransactions()).thenReturn(List.of(t1, t2));

        // Richiesta GET per ottenere tutte le transazioni
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk()) // Aspettati un codice di stato 200
                .andExpect(jsonPath("$[0].id").value(1L)) // Aspettati un ID di transazione
                .andExpect(jsonPath("$[1].id").value(2L)); // Aspettati un ID di transazione
    }

    @Test
    void testGetTransactionById() throws Exception {
        // Mock di una transazione
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType(Transaction.TransactionType.INCOME);
        transaction.setAmount(100.0);
        transaction.setCategory("Salary");
        transaction.setDescription("Monthly salary");

        when(transactionService.getTransactionById(1L)).thenReturn(java.util.Optional.of(transaction));

        // Richiesta GET per ottenere una transazione per ID
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk()) // Aspettati un codice di stato 200
                .andExpect(jsonPath("$.id").value(1L)); // As
    }

    @Test
    void testDeleteTransaction() throws Exception {
        // Mock di una transazione
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType(Transaction.TransactionType.INCOME);
        transaction.setAmount(100.0);
        transaction.setCategory("Salary");
        transaction.setDescription("Monthly salary");

        when(transactionService.deleteTransaction(1L)).thenReturn(true);

        // Richiesta DELETE per eliminare una transazione per ID
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent()); // Aspettati un codice di stato 204
    }

    @Test
    void testDeleteTransactionNotFound() throws Exception {
        when(transactionService.deleteTransaction(1L)).thenReturn(false);

        // Richiesta DELETE per eliminare una transazione per ID
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNotFound()); // Aspettati un codice di stato 404
    }

}