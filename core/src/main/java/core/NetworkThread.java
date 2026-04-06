package core;
import java.util.concurrent.*;

public class NetworkThread implements Runnable{
    private final BlockingQueue<FutureTask<?>> OutgoingMessageRequests = new LinkedBlockingQueue<>();
    
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        OutgoingMessageRequests.offer(futureTask);
        return futureTask;
    }

    @Override
    public void run(){
        while (!Thread.currentThread().isInterrupted()) {
            try {
                FutureTask<?> task = OutgoingMessageRequests.take();
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
