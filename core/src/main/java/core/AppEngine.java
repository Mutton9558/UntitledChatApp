package core;
import io.socket.client.Socket;
import java.net.URI;
import io.socket.client.IO;
import java.util.Collections;
import java.io.File;
import java.util.UUID;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AppEngine {

    private volatile String userId;
    private volatile String deviceId;
    private volatile String jwt;
    private NetworkThread networkThread;
    private UpdaterThread updaterThread;
    private Socket clientSocket;
    private boolean activeSession;

    private final File configFile = new File("paged_config.json");
    private final ObjectMapper mapper = new ObjectMapper();



    // public void getUserFromDB(String username){
    //     System.out.println("Attempting to retrieve user from database");
    //     // code
    // }

    // // will fill later once design is settled
    // public void registerUser(){}

    private void generateDeviceID(){
        this.deviceId = UUID.randomUUID().toString();
    }

    private void createConfig(){
        try{
            UserConfig newConfig = new UserConfig(this.userId, this.deviceId, this.jwt);
            mapper.writeValue(configFile, newConfig);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createSocket(){
        IO.Options options = IO.Options.builder()
        .setAuth(Collections.singletonMap("token", this.jwt))
        .build();
        // socket to backend server
        this.clientSocket = IO.socket(URI.create("https://pagedbackend.firebase.whatever"), options);
    }

    public void loginUser(String username, String password){
        if(!this.activeSession){
            // get user id
            // get jwt from server
            generateDeviceID();
            createConfig();
            createSocket();
        }
    }
    
    private void loadConfig(){
        try{
            if(configFile.exists()){
                UserConfig config = mapper.readValue(configFile, UserConfig.class);
                if(!(config.userId == null)){
                    this.userId = config.userId;
                    this.deviceId = config.deviceId;
                    this.jwt = config.jwtToken;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public AppEngine(){
        this.activeSession = false;
        this.userId = null;
        this.deviceId = null;
        this.jwt = null;
        loadConfig();
        if(!(this.userId == null)){
            // check if jwt valid or if user id and device id has been tampered 
            // if jwt valid and user id & device id not tampered, this.activeSession = true;
        }
        if(this.activeSession){
            createSocket();
        }
    }

    // public void syncMessages(){

    // }

    public void start() {
        // network = msg, updater = db+cache
        this.networkThread = new NetworkThread();
        this.updaterThread = new UpdaterThread();

        Thread network = new Thread(this.networkThread);
        Thread updater = new Thread(this.updaterThread);

        network.start();
        updater.start(); 
    }
}
