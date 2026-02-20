package core;

public class AppEngine {

    public void start() {

        Thread network = new NetworkThread();
        Thread updater = new UpdateThread();

        network.start();
        updater.start();
    }
}
