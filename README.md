# Multithreaded Concurrent File Downloader

A robust, high-performance network client simulation built in **Java** that demonstrates advanced asynchronous computing and **Multithreading** concepts. The system downloads large files concurrently by splitting them into smaller data blocks, processing them across multiple worker threads, verifying internal checksums, and safely aggregating statistics without dynamic race conditions.

## 🛠️ Concurrency Concepts Implemented
- **Thread Pool Management:** Utilizes `ExecutorService` with a fixed thread pool to efficiently manage and reuse dynamic worker threads.
- **Custom Synchronization & Ticket Lock:** Implements a strict sequencing mechanism using `AtomicInteger` (`ticket` and `turn`) to control thread order.
- **Advanced Locking (`ReentrantLock` & `Condition`):** Uses explicit locks with non-blocking `tryLock()` sequences to handle mutual exclusion and prevent deadlocks.
- **Backoff & Retry Strategy:** Workers waiting for critical sections execute a random interval sleep backoff scheme to reduce thread contention.
- **Thread-Safe Data Structures:** Employs `BlockingQueue` (`LinkedBlockingQueue`) for async logging and compiling corrupt/missing block records safely across threads.
- **Data Integrity:** Computes bitwise XOR checksums per block to evaluate data corruption prior to saving state.

## 📂 Project Structure
- `Main.java`: The application entry point; triggers the download client for a specific asset with a set thread count.
- `DownloadClient.java`: Handles configuration, fetches metadata from the remote `FileServer`, orchestrates the executor workspace, and validates final checksums.
- `DownloadWorker.java`: The core `Runnable` engine. Downloads assigned blocks, validates data integrity, executes lock retry strategies, and maintains logs.
- `Logs.java`: A custom POJO used to profile thread behavior, storing metric snapshots like lock acquisition duration (in nanoseconds) and ticket identifiers.
- `FileServer.jar`: The pre-compiled remote server library serving file blocks and validation rules.

## 🚀 How to Run and Test

### 1. Clone the repository
```bash
git clone [https://github.com/YOUR_USERNAME/concurrent-file-downloader.git](https://github.com/YOUR_USERNAME/concurrent-file-downloader.git)
cd concurrent-file-downloader

Compile the source code:
Make sure to include the FileServer.jar in your compilation classpath:
javac -cp .:FileServer.jar *.java

Run the App:
Execute the main program with the server library attached:
java -cp .:FileServer.jar Main
