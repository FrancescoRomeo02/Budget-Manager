package com.example.budgetmanager.controller;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.service.TransactionService;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("saldo", transactionService.getBalance());
        return "index";
    }

    @GetMapping("/transactions")
    public String transactionsPage(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "transactions";
    }

    @PostMapping("/transactions/add")
    public String addTransaction(@RequestParam("type") String type,
                                  @RequestParam("amount") double amount,
                                  @RequestParam("description") String description, 
                                  @RequestParam("date") LocalDate date, 
                                  @RequestParam("category") String category) {
        Transaction transaction = new Transaction();

        transaction.setType(Transaction.TransactionType.valueOf(type));
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDate(date);
        transaction.setCategory(category);
        transactionService.addTransaction(transaction);
        return "redirect:/transactions";
    }

    @PostMapping("/transactions/delete/")
    public String deleteTransaction(@RequestParam("id") Long id) {
        System.out.println("Deleting transaction with id: " + id);
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }
    
}