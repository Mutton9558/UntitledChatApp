import java.sql.Blob;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;

import javafx.application.Application;
import javafx.application.Platform;

class ClassGlobalVariables{
    public static AtomicBoolean userFetched = new AtomicBoolean(false);
}

class Recipient{
    protected int id;
    protected String name;
    // we will convert blob to url
    protected Blob profilePicture;
    protected String status;

    Recipient(int targetId, String targetName, Blob targetProfile, String targetStatus){
        this.id = targetId;
        this.name = targetName;
        this.profilePicture = targetProfile;
        this.status = targetStatus;
    }

    public int returnId(){ return id;}

    public String returnName(){ return name;}

    public Blob returnProfileBlob(){ return profilePicture;}

    public String returnStatus(){ return status;}
}

class User extends Recipient{   
    private List<User> friends;

    public User(String targetUser, int targetId, Blob targetProfile, String targetStatus, List<User> targetFriends){
        super(targetId, targetUser, targetProfile, targetStatus);
        this.friends = Collections.synchronizedList(targetFriends);
    }

    public List<User> returnFriendList(){ return friends; }
}

class Roles{
    private String name;
    private int color;

    public Roles(String roleName, int colorHex){
        this.name = roleName;
        this.color = colorHex;
    }
}

class GroupMember{
    private User member;
    private AtomicBoolean isAdmin;
    private List<Roles> userRoles;

    public GroupMember(User user){
        this.member = user;
        this.isAdmin = new AtomicBoolean(false);
        this.userRoles = new ArrayList<Roles>();
    }

    public User returnMember(){ return this.member; }

    public void setAdmin(){
        this.isAdmin.set(true);
    }
}

class Groups extends Recipient{
    private User owner;
    // user: isAdmin
    // Might just make a new class inheriting users this is getting annoying
    private List<GroupMember> groupMembers;

    // Permission stuff?

    // group not in db
    public Groups(String targetName, int targetId, Blob targetProfile, String targetStatus, User ownerClient){
        super(targetId, targetName, targetProfile, targetStatus);
        this.owner = ownerClient;
        this.groupMembers = new ArrayList<GroupMember>();
        GroupMember groupOwner = new GroupMember(ownerClient);
        this.groupMembers.add(groupOwner);
        groupOwner.setAdmin();
    }

    public Groups(String targetName, int targetId, Blob targetProfile, String targetStatus, User ownerClient, List<User> memberList){
        super(targetId, targetName, targetProfile, targetStatus);
        this.owner = ownerClient;
        this.groupMembers = new ArrayList<GroupMember>();
        Iterator<User> it = memberList.iterator();
        while(it.hasNext()){
            User userToBeAdded = it.next();
            GroupMember newMember = new GroupMember(userToBeAdded);
            this.groupMembers.add(newMember);
            if(userToBeAdded == ownerClient){
                newMember.setAdmin();
            }
        }
    }

    // group in db

    public void addMembers(List<User> newMembers){
        Iterator<User> it = newMembers.iterator();
        while(it.hasNext()){
            GroupMember newMember = new GroupMember(it.next());
            groupMembers.add(newMember);
        }
    }

    public boolean isOwner(User target){
        if(target == this.owner){
            return true;
        }
        return false;
    }
}   

class Messages{
    private User senderId;
    private String textContent; 
    private LocalDateTime dateAndTime;
    private int recipientId;
}

// This thread sends messages to clients on our P2P network
class NetworkThread extends Thread{
    public void run(){
        System.out.println("This is the network thread");

        while(!ClassGlobalVariables.userFetched.get()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// This thread will be in charge of db + cache update
class UpdateThread extends Thread{
    public void run(){
        System.out.println("This is the update thread");

        // pulls item from db
        ClassGlobalVariables.userFetched.set(true);

        // Check if UI is alive before calling it
        if (UserInterface.getInstance() != null) {
            Platform.runLater(() -> UserInterface.getInstance().updateStatus("Done"));
        } else {
            System.out.println("UI not ready yet, skipping direct update.");
        }
    }
}

public class Main{
    public static void main(String[] args){
        System.out.println("Hello World");

        // Two threads, one networking and one db & cache update
        Thread network = new NetworkThread();
        Thread updater = new UpdateThread();

        network.start();
        updater.start();

        Application.launch(UserInterface.class, args);
    }
}