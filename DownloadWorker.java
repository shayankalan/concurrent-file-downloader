import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DownloadWorker implements Runnable {

    private final FileServer server;
    private final String filename;
    private final int blockIndex;

    private final ReentrantLock lock;
    private final Condition condition;

    private final AtomicInteger ticket;
    private final AtomicInteger turn;
    private final AtomicInteger retryLock;
    private final AtomicInteger checksum;
    private final BlockingQueue<Integer> missingblock;
    private final BlockingQueue<Logs> LogsQueue;
    private boolean b = false;

    public DownloadWorker(FileServer server, String filename, int blockIndex,
                          AtomicInteger checksum,
                          BlockingQueue<Integer> missingblock,
                          BlockingQueue<Logs> LogsQueue,
                          ReentrantLock lock, Condition condition,
                          AtomicInteger ticket, AtomicInteger turn, AtomicInteger retryLock) {

        this.server = server;
        this.filename = filename;
        this.blockIndex = blockIndex;
        this.checksum = checksum;
        this.missingblock = missingblock;
        this.LogsQueue = LogsQueue;
        this.lock = lock;
        this.condition = condition;
        this.ticket = ticket;
        this.turn = turn;
        this.retryLock = retryLock;
    }

    @Override
    public void run() {

        Logs logs = new Logs();
        logs.setWorkerId(Thread.currentThread().threadId());
        logs.setBlockIndex(blockIndex);

        Block block = server.get(filename, blockIndex);

        int check = 0;
        for (byte by : block.data()) {
            check ^= (by & 0xFF);
        }

        if (check != block.checksum()) {
            try {
                missingblock.put(blockIndex);
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        int myTicket = ticket.getAndIncrement();

        while (turn.get() != myTicket) {
            Thread.yield();
        }

        long startWaitForLock = System.nanoTime();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                b = lock.tryLock();
                if (b) break;
            }
            if (b) break;

            if (i == 1) {
                System.out.println("Error Bonbast" +"number of index"+ blockIndex);
                System.exit(1);
            }

            retryLock.addAndGet(1);
            try {
                Thread.sleep(new Random().nextInt(100, 300));

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        logs.setTicketNumber(myTicket);
        logs.setTry_to_get_lock(System.nanoTime() - startWaitForLock);
        if (b) {
            try {
                while (turn.get() != myTicket) {
                    lock.unlock();
                    condition.await();
                }
                long t0 = System.nanoTime();
                checksum.getAndUpdate(x -> x ^ blockIndex);
                turn.incrementAndGet();
                logs.setDurationLock(System.nanoTime() - t0);
                LogsQueue.put(logs);
                condition.signalAll();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }
}
