
# Budget Manager

**Budget Manager** è un'applicazione Java progettata per aiutare gli utenti a gestire il proprio budget personale, permettendo loro di registrare transazioni, monitorare entrate e uscite, e calcolare il saldo attuale in tempo reale. Utilizza Spring Boot per il backend, H2 come database in memoria e supporta operazioni di base come l'aggiunta di transazioni e il calcolo del saldo.

## Features

- **Aggiunta di transazioni**: Gli utenti possono inserire transazioni di tipo entrata o uscita con un importo specificato.
- **Calcolo del saldo**: Il sistema calcola automaticamente il saldo in tempo reale sommando tutte le entrate e sottraendo le uscite.
- **Visualizzazione delle transazioni**: È possibile visualizzare tutte le transazioni registrate.
- **Database H2**: Il progetto utilizza H2 come database in memoria per la gestione delle transazioni.

## Tecnologie Utilizzate

- **Java 17** (o superiore)
- **Spring Boot 3.4.2**
- **H2 Database**
- **Spring Data JPA** per la gestione delle transazioni
- **JUnit 5** per i test
- **Spring Web** per le API REST

## Requisiti

Per eseguire questo progetto, assicurati di avere:

- **Java 21+** installato sul tuo sistema.
- **Maven** per la gestione delle dipendenze e la compilazione.
  
## Come Eseguire il Progetto

### 1. Clona il repository

```bash
git clone https://github.com/tuo-username/budget-manager.git
cd budget-manager
```

### 2. Compila e avvia l'applicazione

Usa Maven per compilarlo ed eseguirlo:

```bash
mvn spring-boot:run
```

L'applicazione sarà in esecuzione sulla porta predefinita `8080`.

### 3. Interagire con le API

L'applicazione espone alcune API REST per gestire le transazioni:

- **Aggiungere una transazione**:
  - **URL**: `POST /api/transactions`
  - **Body**:
    ```json
    {
      "amount": 100.0,
      "type": "INCOME"
    }
    ```
  - **Risposta**: Stato HTTP 201 (Created)

- **Visualizzare il saldo**:
  - **URL**: `GET /api/transactions/balance`
  - **Risposta**: Stato HTTP 200 (OK) con il saldo attuale.

- **Visualizzare tutte le transazioni**:
  - **URL**: `GET /api/transactions`
  - **Risposta**: Stato HTTP 200 (OK) con l'elenco delle transazioni.

- **Visualizzare una transazione specifica**:
    - **URL**: `GET /api/transactions/{id}`
    - **Risposta**: Stato HTTP 200 (OK) con i dettagli della transazione.

### 4. Esegui i test

Per eseguire i test, utilizza Maven con il seguente comando:

```bash
mvn test
```
