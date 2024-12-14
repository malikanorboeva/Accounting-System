# Accounting System

This is a Java-based concurrent accounting system that implements double-entry bookkeeping principles. The system supports multiple accountants processing transactions simultaneously while maintaining data consistency and preventing deadlocks.

## Features

- Double-entry bookkeeping implementation
- Concurrent transaction processing
- Thread-safe account operations
- Automatic transaction rollback on errors
- Support for both assets and liabilities
- Random journal entry generation for testing

## Project Structure

### Core Classes

- `Account`: Abstract base class for all accounts
- `Asset`: Implementation of asset accounts
- `Liability`: Implementation of liability accounts
- `Accountant`: Handles transaction processing
- `AccountManager`: Manages account creation and lookup
- `JournalEntryQueue`: Thread-safe queue for journal entries

### Key Packages

```
ledger/
├── Account.java
├── Accountant.java
├── AccountManager.java
├── Asset.java
├── JournalEntryQueue.java
└── Liability.java

exceptions/
├── AccountClosedException.java
├── AccountException.java
├── AccountNotFoundException.java
├── AmountInsufficientException.java
├── InvalidJournalEntryException.java
├── InvalidSyntaxException.java
└── UnequalBalanceException.java

main/
└── Main.java

util/
└── Pair.java
```

## Usage

### Basic Setup

```java
// Initialize the account manager
AccountManager.init();

// Create a queue for journal entries
JournalEntryQueue entryQueue = new JournalEntryQueue();

// Create and start accountants
Accountant accountant = new Accountant(entryQueue);
accountant.start();
```

### Journal Entry Format

Journal entries follow this format:
```
DebitAccount1 Amount1, DebitAccount2 Amount2; CreditAccount1 Amount1, CreditAccount2 Amount2
```

Example:
```
Cash 100, Equipment 200; Accounts_Payable 300
```

### Processing Transactions

```java
// Add entry to queue
entryQueue.addEntry("Cash 100, Equipment 200; Accounts_Payable 300");

// Accountant threads will automatically process entries from the queue
```

## Thread Safety

The system ensures thread safety through:
- Account-level locking
- Ordered lock acquisition to prevent deadlocks
- Atomic transactions
- Thread-safe journal entry queue

## Error Handling

The system handles various error conditions:
- Invalid journal entry syntax
- Insufficient account balances
- Unequal debits and credits
- Concurrent access conflicts
- Transaction rollback on errors

## Testing

The system includes functionality to generate random journal entries for testing:
```java
String entry = AccountManager.getRandomJournalEntry();
```

## Initial Account Setup

Default accounts are created with these categories:

### Assets
- Cash
- Inventory
- Supplies
- Land
- Equipment
- Vehicles
- Buildings
- Operating_Revenues
- Other_Revenues

### Liabilities
- Accounts_Payable
- Wages_Payable
- Interest_Payable
- Unearned_Payable
- Bonds_Payable
- Equity
- Cost_of_Goods_Sold
- Payroll_Expenses
- Marketing_Expenses
- Other_Expenses

## Dependencies

The project requires:
- Java Development Kit (JDK) 8 or higher
- No external libraries required

## Building and Running

1. Compile the project:
```bash
javac main/Main.java
```

2. Run the application:
```bash
java main.Main
```

## Performance Considerations

- Uses ReentrantLock for account locking
- Implements ordered lock acquisition to prevent deadlocks
- Provides retry mechanism for failed transactions
- Maintains transaction atomicity
- Uses concurrent collections for thread safety
