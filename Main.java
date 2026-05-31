import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        long studentId = 40241463;

        DownloadClient client = new DownloadClient(studentId);


        client.download("large.txt", 300);

    }
}