# Responsible for the budget

## Introduction
Budget Manager is a web app developed in **Java** using **Spring Boot** that allows you to manage personal finances. 
Through the site we can: **record our transactions**, **monitor our income and expenses** and **view the balance** in real time.

## Technologies Used
- **Java21**
- **Spring Boot 3.4.2** (MVC, JPA data)
- **Thymeleaf** (part view)
- **H2 Database** (for data persistence)
- **Bootstrap 5** (simple and responsive user interface)
- **Chart.js** (pie chart for expense distribution)
- **JUnit 5** (unit and integration tests)

---

## Project Architecture
The project follows **MVC (Model-View-Controller)** architecture guidelines to ensure a clear separation between business logic, presentation and management of HTTP and API requests.

### **Project Structure**
```
budget-manager/
├── src/main/java/com/example/budgetmanager/
│ ├── controller/ # Handles HTTP and API requests
│ ├── model/ # Defines the database entities
│ ├── repository/ # Interface for accessing data
│ ├── service/ # Contains the business logic
│ └── BudgetManagerApplication.java # Main class
│
├── src/main/resources/
│ ├── templates/ # HTML pages with Thymeleaf
│ ├── application.properties # H2 database configuration
│
├── src/test/java/com/example/budgetmanager/
│ ├── TransactionControllerTest.java # Test Controller
│ ├── TransactionServiceTest.java # Test Service
```

---

## Detail of the MVC Pattern

### **Model** - `Transaction.java`
This file defines the structure of the `Transaction` table in the database as well as defining getters and setters and some methods for validating inputs.

```java
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
}
```
### **View** - Thymeleaf
HTML pages are dynamic and are located in `src/main/resources/templates/`.
- `index.html` → Dashboard with balance and latest transactions.
- `view_transactions.html` → List of transactions + Pie chart of expenses.
- `add_transaction.html` → Form to add new transactions, one at a time.
- `error.html` → Custom error page.


### **Controller** - `TransactionController.java`
Handles REST API requests for retrieving and managing transactions.

```java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
 @PostMapping
    public ResponseEntity<?> addTransaction(@RequestBody Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getType() == null) {
            return ResponseEntity.badRequest().body("Error: amount and type are mandatory.");
        }
        Transaction savedTransaction = transactionService.addTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

  @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(transactions);
    }
}
```
The methods implemented are:
- `addTransaction(Transaction transaction)` to add a new transaction.
- `getAllTransactions()` to retrieve all transactions.
- `deleteTransaction(Long id)` to delete a transaction.
- `getTransactionById(Long id)` to retrieve a transaction by id.

**View Controller** - `TransactionsViewController.java`
Manages navigation between HTML pages.
```java
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
}
```
The implemented methods are:
- `homePage(Model model)` to display the home page and obtain the necessary data.
- `transactionsPage(Model model)` to display the transactions page and obtain the necessary data.
- `addTransactionPage(Model model)` to navigate to the add transaction page.
- `addTransaction(Transaction transaction)` to handle adding a new transaction.
- `deleteTransaction(Long id)` to handle deleting a transaction.

**Custom Error Controller** - `CustomErrorController.java`
Handles exceptions that occur during browsing.
```java
@Controller
public class CustomErrorController implements ErrorController {
    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            String errorMessage = statusCode == 404 ? "Oops! Page not found" : "Oops! An error occurred";
            model.addAttribute("statusCode", statusCode);
            model.addAttribute("errorMessage", errorMessage);

        }

        return "error"; 
    }
}
```
Thanks to this controller it is possible to manage 404 exceptions and display a customized error message and not the default page.

### **Service** - `TransactionService.java`
It implements business logic for handling transactions and performing some calculations on them.

```java
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    public Map<String, Double> getExpenseCategorySummary() {
            return transactionRepository.getExpenseCategorySummary().stream()
                    .collect(Collectors.toMap(
                            obj -> (String) obj[0], 
                            obj -> (Double) obj[1] 
                    ));
        }

}
```
The implemented methods are:
- `addTransaction(Transaction transaction)` to add a new transaction.
- `getAllTransactions()` to retrieve all transactions.
- `getTransactionById(Long id)` to retrieve a transaction by id.
- `getBalance()` to get the balance.
- `getTotalRevenue()` to get the total revenue.
- `getTotalExpenses()` to get the total expenses.
- `getExpensesByCategorySummary()` to retrieve expenses by category.
- `deleteTransaction(Long id)` to delete a transaction.

### **Repository** - `TransactionRepository.java`
```java
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Query per ottenere il totale delle entrate
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = 'INCOME'")
    Double getTotalRevenue();

    // Query per ottenere il totale delle spese
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = 'EXPENSE'")
    Double getTotalExpenses();

    // Query per ottenere un riepilogo per categoria delle spese
    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.type = 'EXPENSE' GROUP BY t.category")
    List<Object[]> getExpenseCategorySummary();
}
```
The repository contains some custom queries to get total revenue, total expenses, and a category summary of expenses to simplify your business logic.

---

## HTTP Request Flow

1. **The user visits the home page (`/`)**
   - `TransactionsViewController` loads the balance and latest transactions.
   - `index.html` displays data.

2. **User accesses the transactions list (`/transactions`)**
   - `TransactionsViewController` loads all transactions.
   - `view_transactions.html` shows table + pie chart.
   - **User deletes a transaction**
    -`TransactionController.deleteTransaction()` deletes the transaction from the database.

3. **User adds a new transaction**
   - `TransactionController.addTransaction()` saves the transaction to the database.
   - The user is redirected to `/transactions`.

4. **The user tries to visit a non-existing page**
    - `TransactionsViewController` handles 404 error.
    - `error.html` shows the error message.
---
## How to Run the Project
### Clone the repository
```sh
git clone https://gitlab.com/f.romeo23/budget-manager
cd budget-manager/budget-manager
```

### Compile and run
```sh
mvn spring-boot:run
```

### Log in to the application
- **Dashboard:** [http://localhost:8080/](http://localhost:8080/)
- **REST API:** [http://localhost:8080/api/transactions](http://localhost:8080/api/transactions)

### How to run tests
```sh
cd budget-manager/budget-manager
mvn test
```

---

**Author:** Francesco Romeo, mat. 885880

