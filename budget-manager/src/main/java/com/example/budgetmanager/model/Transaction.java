package com.example.budgetmanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String category;
    private String description;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    // Costruttore di default
    public Transaction() {
    }

    // Costruttore con validazione
    public Transaction(Double amount, String category, String description, LocalDate date, TransactionType type) {
        validateAmount(amount, type);
        validateCategory(category);
        validateDate(date);
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    // Getter e setter
    public Long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        validateAmount(amount, this.type); 
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        validateCategory(category); 
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        validateDate(date);
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    // Metodi di validazione
    private void validateAmount(Double amount, TransactionType type) {
        if (amount == null || amount == 0) {
            throw new IllegalArgumentException("The amout can't be 0 or null.");
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("The category can't be null or empty.");
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("The date can't be null.");
        }
    }

    // Enum per definire il tipo di transazione
    public enum TransactionType {
        INCOME, OUTCOME
    }
}