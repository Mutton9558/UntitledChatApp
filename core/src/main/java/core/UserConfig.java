package core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserConfig {
    @JsonProperty("userId")
    public String userId;

    @JsonProperty("deviceId")
    public String deviceId;

    @JsonProperty("jwtToken")
    public String jwtToken;

    public UserConfig(){
        this.userId = null;
        this.deviceId = null;
        this.jwtToken = null;
    }

    public UserConfig(String userId, String deviceId, String jwtToken){
        this.userId = userId;
        this.deviceId = deviceId;
        this.jwtToken = jwtToken;
    }
}
