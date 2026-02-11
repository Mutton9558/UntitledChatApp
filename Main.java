import java.sql.Blob;

class User{
    private String username;
    private int id;
    // we will convert blob to url
    private Blob profilePicture;
    private String status;

    public String returnName(){ return username;}
    
    public int returnId(){ return id;}

    public Blob returnProfileBlob(){ return profilePicture;}

    public String returnStatus(){ return status;}

}

// This is where most of the UI is handled
class UserInterface extends Thread{
    public void run(){
        System.out.println("This is the UI thread");
    }
}

// This thread sends messages to clients on our P2P network
class NetworkThread extends Thread{
    public void run(){
        System.out.println("This is the network thread");
    }
}

// This thread will be in charge of db + cache update
class UpdateThread extends Thread{
    public void run(){
        System.out.println("This is the update thread");
    }
}

public class Main{
    public static void main(String[] args){
        System.out.println("Hello World");

        // Three threads, one UI, one networking and one db & cache update
        Thread ui = new UserInterface();
        Thread network = new NetworkThread();
        Thread updater = new UpdateThread();

        ui.start();
        network.start();
        updater.start();
    }
}