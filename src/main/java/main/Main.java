package main;
import java.sql.Blob;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;

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
    private String phoneNum;

    public User(String targetUser, int targetId, Blob targetProfile, String targetStatus, List<User> targetFriends){
        super(targetId, targetUser, targetProfile, targetStatus);
        this.friends = Collections.synchronizedList(new ArrayList<User>(targetFriends));
    }

    // user has linked phone number
    public User(String targetUser, int targetId, Blob targetProfile, String targetStatus, List<User> targetFriends, String linkedNum){
        super(targetId, targetUser, targetProfile, targetStatus);
        this.friends = Collections.synchronizedList(new ArrayList<User>(targetFriends));
        this.phoneNum = linkedNum;
    }

    public List<User> returnFriendList(){ return friends; }
    public String returnPhoneNum(){ return phoneNum; }

    public void addNewNumber(String newNumber){
        this.phoneNum = newNumber;
    }

    public void addFriends(User newFriend){
        friends.add(newFriend);
    }
}

class Roles{
    private String name;
    private int color;

    public Roles(String roleName, int colorHex){
        this.name = roleName;
        this.color = colorHex;
    }

    public String returnRoleName(){ return this.name; }
    public int returnColorHex(){ return this.color; }
}

class GroupMember{
    private User member;
    private List<Roles> userRoles;

    public GroupMember(User user){
        this.member = user;
        this.userRoles = Collections.synchronizedList(new ArrayList<Roles>());
    }

    public GroupMember(User user, List<Roles> rolesInDb){
        this.member = user;
        this.userRoles = Collections.synchronizedList(new ArrayList<Roles>(rolesInDb));
    }

    public User returnMember(){ return this.member; }

    public List<Roles> returnRoleList(){ return this.userRoles; };
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
        this.groupMembers = Collections.synchronizedList(new ArrayList<GroupMember>());
        GroupMember groupOwner = new GroupMember(ownerClient);
        this.groupMembers.add(groupOwner);
    }

    // group in db
    public Groups(String targetName, int targetId, Blob targetProfile, String targetStatus, User ownerClient, List<User> memberList){
        super(targetId, targetName, targetProfile, targetStatus);
        this.owner = ownerClient;
        this.groupMembers = Collections.synchronizedList(new ArrayList<GroupMember>());
        Iterator<User> it = memberList.iterator();
        while(it.hasNext()){
            User userToBeAdded = it.next();
            GroupMember newMember = new GroupMember(userToBeAdded);
            this.groupMembers.add(newMember);
        }
    }

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
    private int senderId;
    private String textContent; 
    private LocalDateTime dateAndTime;

    Messages(int targetSender, String messageContent){
        this.senderId = targetSender;
        this.textContent = messageContent;
        this.dateAndTime = LocalDateTime.now();
    }

    public int returnSenderId(){ return this.senderId; }
    public String returnMessageContent(){ return this.textContent; }
    public LocalDateTime returnTimeSent(){ return this.dateAndTime; }
}

class Conversation{
    private Recipient contactedRecipient;
    private List<Messages> conversationMessages;

    Conversation(Recipient targetRecipient){
        this.contactedRecipient = targetRecipient;
        this.conversationMessages = Collections.synchronizedList(new ArrayList<Messages>());
    }

    public Recipient returnRecipient(){ return this.contactedRecipient; }
    public List<Messages> returnConvoMessages(){ return this.conversationMessages; }

    public void addMessage(Messages newMsg){
        synchronized(this.conversationMessages){
            this.conversationMessages.add(newMsg);
        }
    }
}

class ChatListItem{
    private Conversation chatConvo;
    private LocalDateTime lastContactDateTime;
    private int unreadMessageCount;

   ChatListItem(Recipient targetContact, LocalDateTime loggedContactTime){
        this.chatConvo = new Conversation(targetContact);
        this.lastContactDateTime = loggedContactTime;
        this.unreadMessageCount = 0;
    }

    public void addUnreadMsgCount(){ unreadMessageCount++; }
    
    public Conversation returnConversation(){ return chatConvo; }
    public LocalDateTime returnLastContactTime(){ return lastContactDateTime; }
    public int returnUnreadMsgCount(){ return unreadMessageCount; }
}

class ClassGlobalVariables{
    public static AtomicBoolean userFetched = new AtomicBoolean(false);
    public static Map<Integer, ChatListItem> userContacts = new ConcurrentHashMap<>();
    public static ChatListItem curSelectedContact = null;

    // converts collection of contacts into a stream, sort it by last contact time and return the list
    public static List<ChatListItem> getContactsSortedByRecent() {
        return userContacts.values()
            .stream()
            .sorted(Comparator.comparing(ChatListItem::returnLastContactTime,
                     Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }
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

        while(true){
            
        }
    }
}

public class Main{
    public static void main(String[] args){
        // Two threads, one networking and one db & cache update
        Thread network = new NetworkThread();
        Thread updater = new UpdateThread();

        network.start();
        updater.start();

        Application.launch(UserInterface.class, args);
    }
}