package core;
import java.util.concurrent.*;

public class DatabaseThread implements Runnable {
    private final BlockingQueue<FutureTask<?>> persistentEventQueue = new LinkedBlockingQueue<>();
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        persistentEventQueue.offer(futureTask);
        return futureTask;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                FutureTask<?> task = persistentEventQueue.take();
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}