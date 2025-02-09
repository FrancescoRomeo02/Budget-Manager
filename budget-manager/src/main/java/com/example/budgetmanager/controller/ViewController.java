package com.example.budgetmanager.controller;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.service.TransactionService;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

@Controller
@RequestMapping("/")
public class ViewController {

    private final TransactionService transactionService;

    public ViewController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public String homePage(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();

        double balance = transactionService.getBalance();
        double totalRevenue = transactionService.getTotalRevenue();
        double totalExpenses = transactionService.getTotalExpenses();

        // Attributi per la view
        model.addAttribute("balance", balance);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("latestTransactions", transactions.subList(0, Math.min(transactions.size(), 10)));

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

        // Dati per il bilancio
        model.addAttribute("balance", transactionService.getBalance());

        return "view_transactions";
    }

    @GetMapping("/add_transactions")
    public String addTransactionPage() {
        return "add_transaction";
    }

    @PostMapping("/transactions/add")
    public String addTransaction(@ModelAttribute Transaction transaction) {
        transactionService.addTransaction(transaction);
        return "redirect:/transactions";
    }

    @PostMapping("/transactions/delete")
    public String deleteTransaction(@RequestParam("id") Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("message", "La pagina che stai cercando non è stata trovata.");
        return "error"; // La view 404.html
    }


}