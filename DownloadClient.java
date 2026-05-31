import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DownloadClient {

    public ReentrantLock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();
    public FileServer server;
    public AtomicInteger ticket;
    public AtomicInteger retryLock;
    public AtomicInteger turn;
    public AtomicInteger checksum;
    public BlockingQueue<Integer> missingblock;
    public BlockingQueue<Logs> logsQueue;


    public DownloadClient(long studentId) {
        this.server = new FileServer(studentId);
        this.logsQueue = new LinkedBlockingQueue<>();
        this.missingblock = new LinkedBlockingQueue<>();
        this.ticket  = new AtomicInteger(0);
        this.retryLock  = new AtomicInteger(0);
        this.turn  = new AtomicInteger(0);
        this.checksum  = new AtomicInteger(0);

    }

    public void download(String filename, int threadCount) {

        System.out.println("Downloading " + filename + " with " + threadCount + " threads...");


        List<FileServer.FileInfo> files = server.list();
        //find filename
        FileServer.FileInfo targetFile = files.stream()
                .filter(f -> f.getFileName().equals(filename))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found: " + filename));

        int totalBlocks = targetFile.getBlockCount();


        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        //send block whith thread to downloadworker
            for (int i = 0; i < totalBlocks; i++) {
                int blockIndex = i;
                executor.submit(new DownloadWorker(server, filename, blockIndex, checksum ,missingblock,
                        logsQueue,lock ,condition, ticket, turn,retryLock));
            }

            //end thread
            executor.shutdown();
            try {
                if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    throw new RuntimeException("Download timed out!");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                throw new RuntimeException("Download interrupted!", e);
            }

            boolean isValid = checksum.get() == targetFile.getFileChecksum();


        logsQueue.forEach(System.out::println);
        System.out.print("missingblock:");
        missingblock.forEach(integer -> System.out.print(integer + ","));
        System.out.println();
        System.out.println("count of retry lock:" + retryLock);

            System.out.println("File checksum: " + (isValid ? "✅ Valid" : "❌ CORRUPTED!  final checksum: ") + checksum.get());


    }
}