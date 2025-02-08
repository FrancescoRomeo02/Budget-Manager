package com.example.budgetmanager.controller;

import com.example.budgetmanager.model.Transaction;
import com.example.budgetmanager.service.TransactionService;

import java.time.LocalDate;
import java.util.List;

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
        double saldo = transactionService.getBalance();
        List<Transaction> Transazioni = transactionService.getAllTransactions();
        double totaleEntrate = 0;
        double totaleUscite = 0;
        for (Transaction t : Transazioni) {
            if (t.getType() == Transaction.TransactionType.ENTRATA) {
                totaleEntrate += t.getAmount();
            } else {
                totaleUscite += t.getAmount();
            }
        }

        List<Transaction> ultimeTransazioni = Transazioni.subList(0, Math.min(Transazioni.size(), 5));

        model.addAttribute("saldo", saldo);
        model.addAttribute("totaleEntrate", totaleEntrate);
        model.addAttribute("totaleUscite", totaleUscite);
        model.addAttribute("ultimeTransazioni", ultimeTransazioni);
        return "index";
    }

    @GetMapping("/transactions")
    public String transactionsPage(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "view_transactions";
    }

    @GetMapping("/add_transactions")
    public String addTransactionPage() {
        return "add_transaction";
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