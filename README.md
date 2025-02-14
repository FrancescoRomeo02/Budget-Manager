# Budget Manager

## Introduzione
Budget Manager è una web app sviluppata in **Java** utilizzando **Spring Boot** che permette la gestione delle finanze personali. 
Attraverso il sito possiamo: **registrare le nostre transazioni**, **monitorare le ostre entrate e uscite** e **visualizzare il saldo** in tempo reale.

## Tecnologie Utilizzate
- **Java 21**
- **Spring Boot 3.4.2** (MVC, Data JPA)
- **Thymeleaf** (per la parte View)
- **H2 Database** (per la persistenza dei dati)
- **Bootstrap 5** (UI semplice e responsive)
- **Chart.js** (grafico a torta per la distribuzione delle spese)
- **JUnit 5** (test unitari e di integrazione)

---

## Architettura del Progetto
Il progetto segue le linee guida dell'architettura **MVC (Model-View-Controller)** per garantire una separazione chiara tra la logica di business, la presentazione e la gestione delle richieste HTTP e API.

### **Struttura del Progetto**
```
budget-manager/
├── src/main/java/com/example/budgetmanager/
│   ├── controller/      # Gestisce le richieste HTTP e API
│   ├── model/           # Definisce le entità del database
│   ├── repository/      # Interfaccia per l'accesso ai dati
│   ├── service/         # Contiene la logica di business
│   └── BudgetManagerApplication.java  # Classe principale
│
├── src/main/resources/
│   ├── templates/              # Pagine HTML con Thymeleaf
│   ├── application.properties  # Configurazione del database H2
│
├── src/test/java/com/example/budgetmanager/
│   ├── TransactionControllerTest.java   # Test Controller
│   ├── TransactionServiceTest.java      # Test Service
```

---

## Dettaglio del Pattern MVC

### **Model** - `Transaction.java`
Questo file definisce la struttura della tabella `Transaction` nel database oltre che definire getter e setter ed alcuni metodi per la validazione degli input.

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
Le pagine HTML sono dinamiche e si trovano in `src/main/resources/templates/`.
- `index.html` → Dashboard con saldo e ultime transazioni.
- `view_transactions.html` → Lista delle transazioni + Grafico a torta delle spese.
- `add_transaction.html` → Form per aggiungere nuove transazioni, una alla volta.
- `error.html` → Pagina di errore personalizzata.


### **Controller** - `TransactionController.java`
Gestisce le richieste API REST per il recupero e la gestione delle transazioni.

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
I meotodi implementati sono:
- `addTransaction(Transaction transation)` per aggiungere una nuova transazione.
- `getAllTransactions()` per recuperare tutte le transazioni.
- `deleteTransaction(Long id)` per eliminare una transazione.
- `getTransactionById(Long id)` per recuperare una transazione per id.

**View Controller** - `TransactionsViewController.java`
Gestisce la navigazione tra le pagine HTML.
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
I metodi implementati sono:
- `homePage(Model model)` per visualizzare la home page ed ottenere i dati necessari.
- `transactionsPage(Model model)` per visualizzare la pagina delle transazioni ed ottenere i dati necessari.
- `addTransactionPage(Model model)` per navigare alla pagina di aggiunta di una transazione.
- `addTransaction(Transaction transaction)` per gestire l'aggiunta una nuova transazione.
- `deleteTransaction(Long id)` per gestire l'eliminazione una transazione.

**Custom Error Controller** - `CustomErrorController.java`
Gestisce le eccezioni che si verificano durante la navigazione.
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
Grazie a questo controller è possibile gestire le eccezioni 404 e visualizzare un messaggio di errore personalizzato e non la pagina di default.

### **Service** - `TransactionService.java`
Implementa la logica di business per la gestione delle transazioni e lo svolgimento di alcuni calcoli su di esse.

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
I metodi implementati sono:
- `addTransaction(Transaction transaction)` per aggiungere una nuova transazione.
- `getAllTransactions()` per recuperare tutte le transazioni.
- `getTransactionById(Long id)` per recuperare una transazione per id.
- `getBalance()` per recuperare il saldo.
- `getTotalRevenue()` per recuperare il totale delle entrate.
- `getTotalExpenses()` per recuperare il totale delle uscite.
- `getExpensesByCategorySummary()` per recuperare le uscite per categoria.
- `deleteTransaction(Long id)` per eliminare una transazione.

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
Il repository contiene alcune query personalizzate per ottenere il totale delle entrate, il totale delle spese e un riepilogo per categoria delle spese in modo da semplificare la logica di business.

---

## Flusso delle Richieste HTTP

1. **L'utente visita la home (`/`)**
   - `TransactionsViewController` carica il saldo e le ultime transazioni.
   - `index.html` mostra i dati.

2. **L'utente accede alla lista transazioni (`/transactions`)**
   - `TransactionsViewController` carica tutte le transazioni.
   - `view_transactions.html` mostra la tabella + grafico a torta.
   - **L'utente elimina una transazione**
    -`TransactionController.deleteTransaction()` elimina la transazione dal database.

3. **L'utente aggiunge una nuova transazione**
   - `TransactionController.addTransaction()` salva la transazione nel database.
   - L'utente viene reindirizzato a `/transactions`.

4. **L'utente tenta di visitare una pagina non esistente**
    - `TransactionsViewController` gestisce l'errore 404.
    - `error.html` mostra il messaggio di errore.
---

## Come Eseguire il Progetto
### Clonare il repository
```sh
git clone <https://gitlab.com/f.romeo23/budget-manager>
cd budget-manager
```

### Compilare ed eseguire
```sh
mvn spring-boot:run
```

### Accedere all'applicazione
- **Dashboard:** [http://localhost:8080/](http://localhost:8080/)
- **API REST:** [http://localhost:8080/api/transactions](http://localhost:8080/api/transactions)

### Come eseguire i test
```sh
cd budget-manager
mvn test
```

---

**Autore:** Francesco Romeo, mat. 885880

