# Accounting-System

A Java-based accounting system that implements a concurrent booking system for financial transactions. This project combines both basic booking functionality and thread-safe concurrent operations.

## Project Structure

### Core Components

#### Basic Booking System
- **Account**: Abstract base class for different account types
  - **Asset**: Implementation for asset accounts
  - **Liability**: Implementation for liability accounts
- **AccountManager**: Manages account creation and access
- **JournalEntry**: Represents individual booking entries
- **Accountant**: Handles the booking process

#### Concurrent Extensions
- **JournalEntryQueue**: Thread-safe queue for storing pending bookings
- **Concurrent Accountant**: Extended version with thread safety mechanisms
- **Deadlock Prevention**: Implements strategies to prevent deadlocks in concurrent bookings

### Key Features

1. **Basic Booking Operations**
   - Account creation and management
   - Transaction validation
   - Debit and credit operations
   - Balance checking
   - Syntax validation for booking entries

2. **Concurrent Processing**
   - Multiple accountants working simultaneously
   - Thread-safe queue management
   - Deadlock prevention
   - Transaction atomicity
   - Account locking mechanisms

## Technical Requirements

- Java Development Kit (JDK) 11 or higher
- IDE supporting Java development (e.g., Eclipse, IntelliJ IDEA)

## Usage

### Basic Booking Example
```java
// Create a booking entry
String bookingEntry = "Equity 27, Bonds_Payable 385; Land 137, Other_Revenues 275";
accountant.postEntry(bookingEntry);
```

### Concurrent Processing Example
```java
// Create the queue and accountants
JournalEntryQueue queue = new JournalEntryQueue();
Accountant[] accountants = new Accountant[5];

// Start processing
for (Accountant accountant : accountants) {
    accountant.start();
}
```

## Testing

The system can be tested with various parameters:
- Number of transactions: 10,000, 100,000, 1,000,000
- Number of concurrent accountants: 1, 2, 5, 10
- Error ratio can be adjusted in AccountManager (set to 0 for testing)

Performance metrics are measured in milliseconds for different combinations of transactions and accountants.

## Error Handling

The system handles several types of errors:
1. Syntax errors in booking entries
2. Unbalanced debit and credit amounts
3. Non-existent accounts
4. Concurrent access conflicts
5. Insufficient account balances

## Implementation Notes

### Account Locking
- Only one accountant can modify an account at a time
- Accounts must be explicitly opened and closed
- Deadlock prevention through ordered resource acquisition

### Transaction Processing
1. Check syntax and balance
2. Open required accounts
3. Prepare bookings
4. Commit or rollback
5. Close accounts
