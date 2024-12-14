# Concurrent Bookkeeping System

A thread-safe Java implementation of a concurrent bookkeeping system that handles multiple concurrent financial transactions with account management, transaction validation, and deadlock prevention.

## Overview

The system implements a concurrent ledger processing system that can:
- Handle multiple simultaneous financial transactions
- Maintain transaction integrity and account consistency
- Prevent deadlocks through ordered account locking
- Scale effectively with multiple processing threads
- Handle various error conditions gracefully

## Components

### Core Classes

- **Accountant**: Implements Runnable, processes journal entries concurrently
- **JournalEntryQueue**: Thread-safe queue for storing and managing journal entries
- **Account**: Abstract base class for different account types
  - **Asset**: Implementation for asset accounts
  - **Liability**: Implementation for liability accounts
- **AccountManager**: Manages account creation and lookup

### Supporting Classes

- **Pair**: Generic utility class for handling paired values
- **Various Exception classes**: For handling different error conditions

## Performance

The system has been tested with different configurations:

### Test Results

Entries | Threads | Time (ms) | Successful Postings | Success Rate
--------|----------|-----------|-------------------|-------------
10K     | 1        | 272       | 9,509            | 95.09%
10K     | 2        | 124       | 9,746            | 97.46%
10K     | 5        | 72        | 9,878            | 98.78%
10K     | 10       | 54        | 9,829            | 98.29%
100K    | 1        | 882       | 98,695           | 98.70%
100K    | 2        | 509       | 99,005           | 99.01%
100K    | 5        | 333       | 99,374           | 99.37%
100K    | 10       | 255       | 99,656           | 99.66%
1M      | 1        | 8,191     | 996,985          | 99.70%
1M      | 2        | 5,207     | 998,410          | 99.84%
1M      | 5        | 3,102     | 999,027          | 99.90%
1M      | 10       | 2,612     | 998,995          | 99.90%

### Key Performance Characteristics

- Near-linear scaling up to 5 threads
- Continued performance improvements up to 10 threads
- Higher success rates with larger batch sizes
- Processing speed of up to ~382,000 entries per second

## Usage

### Basic Usage

```java
// Initialize the account manager
AccountManager.init();

// Create a queue with desired capacity
JournalEntryQueue queue = new JournalEntryQueue(10000);

// Add entries to the queue
queue.addEntry("Cash 100, Equipment 200; Accounts_Payable 300");

// Create and start accountants
Accountant accountant = new Accountant(queue);
Thread thread = new Thread(accountant);
thread.start();
```

### Running Performance Tests

```java
// Test configurations
int[] entryCounts = {10000, 100000, 1000000};
int[] threadCounts = {1, 2, 5, 10};

// Run tests
for (int entryCount : entryCounts) {
    for (int threadCount : threadCounts) {
        runTest(entryCount, threadCount);
    }
}
```

## Thread Safety Features

1. **Account Locking**:
   - Accounts are locked in a consistent order to prevent deadlocks
   - Only one accountant can modify an account at a time

2. **Queue Synchronization**:
   - Thread-safe entry addition and removal
   - Proper wait/notify mechanism for queue operations

3. **Error Handling**:
   - Transaction rollback on errors
   - Proper cleanup of locks in all cases
   - Retry mechanism for temporary failures

## Error Handling

The system handles several types of errors:
- Syntax errors in journal entries
- Unbalanced transactions
- Insufficient funds
- Account access conflicts
- Non-existent accounts

Failed transactions are either:
- Discarded (for unrecoverable errors)
- Requeued (for temporary conflicts)
- Rolled back (for failed validations)

## Dependencies

- Java 8 or higher
- No external libraries required
