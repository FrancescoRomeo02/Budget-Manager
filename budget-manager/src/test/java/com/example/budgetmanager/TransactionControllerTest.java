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
        transaction.setAmount(100.0);
        transaction.setType(Transaction.TransactionType.INCOME);

        // Simulazione del comportamento del servizio
        when(transactionService.addTransaction(Mockito.any(Transaction.class))).thenReturn(transaction);

        // Test della richiesta POST
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 100.0, \"type\": \"INCOME\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void testAddTransactionWithInvalidData() throws Exception {
        // Dati non validi (importo negativo)
        mockMvc.perform(post("/api/transactions")
                .contentType("application/json")
                .content("{\"amount\": -50.0, \"type\": \"INCOME\"}"))
                .andExpect(status().isBadRequest()); // Aspettati un errore
    }

    
}