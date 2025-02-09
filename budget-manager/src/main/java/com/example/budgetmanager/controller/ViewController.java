package com.example.budgetmanager.controller;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.service.TransactionService;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        double saldo = transactionService.getBalance();
        double totaleEntrate = transactions.stream()
                                           .filter(t -> t.getType() == Transaction.TransactionType.ENTRATA)
                                           .mapToDouble(Transaction::getAmount)
                                           .sum();
        
        double totaleUscite = transactions.stream()
                                          .filter(t -> t.getType() == Transaction.TransactionType.USCITA)
                                          .mapToDouble(Transaction::getAmount)
                                          .sum();

        // Preleva le ultime 5 transazioni (se esistono)
        List<Transaction> ultimeTransazioni = transactions.size() > 5 
                ? transactions.subList(0, 5) 
                : transactions;

        // Ordina le transazioni per data decrescente
        ultimeTransazioni.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        // Attributi per la view
        model.addAttribute("saldo", saldo);
        model.addAttribute("totaleEntrate", totaleEntrate);
        model.addAttribute("totaleUscite", totaleUscite);
        model.addAttribute("ultimeTransazioni", ultimeTransazioni);

        return "index";
    }

    @GetMapping("/transactions")
    public String transactionsPage(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);

        // Dati per il grafico a torta (distribuzione per categoria)
        Map<String, Double> categoryData = transactionService.getCategorySummary();
        model.addAttribute("categories", categoryData.keySet());
        model.addAttribute("categoryAmounts", categoryData.values());


        // Calcolo di entrate e uscite
        double totaleEntrate = categoryData.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .mapToDouble(Map.Entry::getValue)
                .sum();

        double totaleUscite = categoryData.entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .mapToDouble(Map.Entry::getValue)
                .sum();

        model.addAttribute("totaleEntrate", totaleEntrate);
        model.addAttribute("totaleUscite", totaleUscite);

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
}