package com.example.budgetmanager.controller;

import java.util.List;
import java.util.Map;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.service.TransactionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        // Attributi per la view
        model.addAttribute("balance", transactionService.getBalance());
        model.addAttribute("totalRevenue", transactionService.getTotalRevenue());
        model.addAttribute("totalExpenses", transactionService.getTotalExpenses());
        List<Transaction> latestTransactions = transactions.subList(0, Math.min(transactions.size(), 10));
        model.addAttribute("latestTransactions", latestTransactions.size()>0 ? latestTransactions : null);

        // Pagina iniziale
        return "index";
    }

    @GetMapping("/transactions")
    public String transactionsPage(Model model) {
        
        // Dati per la tabella
        model.addAttribute("transactions", transactionService.getAllTransactions());

        // Dati per il grafico a torta (distribuzione per categoria solo delle spese)
        Map<String, Double> categoryData = transactionService.getExpenseCategorySummary();
        model.addAttribute("categories", categoryData.keySet());
        model.addAttribute("categoryAmounts", categoryData.values());

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

        // Redirect alla pagina delle transazioni (uso redirect per evitare di aggiungere la stessa transazione più volte)
        return "redirect:/transactions";
    }

    @PostMapping("/transactions/delete")
    public String deleteTransaction(@RequestParam("id") Long id) {
        transactionService.deleteTransaction(id);

        // Redirect alla pagina delle transazioni (uso redirect per evitare di provare ad eliminare la stessa transazione più volte)
        return "redirect:/transactions";
    }

}