package com.example.budgetmanager.controller;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.service.TransactionService;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

/* ------------------------------------------------------------------
Controller per gestire le richieste relative alle view
Questo controller gestisce le richieste per le pagine HTML e 
fornisce i dati necessari per visualizzare le informazioni 
------------------------------------------------------------------ */

@Controller
@RequestMapping("/")
public class TransactionsViewController {

    private final TransactionService transactionService;

    public TransactionsViewController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public String homePage(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();

        // Dati per il riepilogo
        double balance = transactionService.getBalance();
        double totalRevenue = transactionService.getTotalRevenue();
        double totalExpenses = transactionService.getTotalExpenses();

        // Attributi per la view
        model.addAttribute("balance", balance);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("latestTransactions", transactions.subList(0, Math.min(transactions.size(), 10)));

        // Pagina iniziale
        return "index";
    }

    @GetMapping("/transactions")
    public String transactionsPage(Model model) {
        
        // Dati per la tabella
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);

        // Dati per il grafico a torta (distribuzione per categoria solo delle spese)
        Map<String, Double> categoryData = transactionService.getOutcomeCategorySummary();
        model.addAttribute("categories", categoryData.keySet());
        model.addAttribute("categoryAmounts", categoryData.values());

        // Dati per il riepilogo
        double totalRevenue = transactionService.getTotalRevenue();
        double totalExpenses = transactionService.getTotalExpenses();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalExpenses", totalExpenses);

        // Dati per il saldo
        model.addAttribute("balance", transactionService.getBalance());
        
        // Pagina per visualizzare le transazioni
        return "view_transactions";
    }

    @GetMapping("/add_transactions")
    public String addTransactionPage() {

        // Pagina per aggiungere una transazione
        return "add_transaction";
    }

    @PostMapping("/transactions/add")
    public String addTransaction(@ModelAttribute Transaction transaction) {
        transactionService.addTransaction(transaction);

        // Redirect alla pagina delle transazioni
        return "redirect:/transactions";
    }

    @PostMapping("/transactions/delete")
    public String deleteTransaction(@RequestParam("id") Long id) {
        transactionService.deleteTransaction(id);

        // Redirect alla pagina delle transazioni
        return "redirect:/transactions";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound() {

        // Redirect alla pagina di errore
        return "error";
    }


}