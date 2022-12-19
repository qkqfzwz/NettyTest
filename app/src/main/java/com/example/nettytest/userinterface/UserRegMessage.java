package com.example.nettytest.userinterface;

public class UserRegMessage extends UserMessage {
    public boolean isReg;
    public int regExpire;
    public String areaId;
    public String areaName;
    public String transferAreaId;
    public String backupServerAddress;
    public boolean enableListenCall;
    public int snapPort;

    public UserRegMessage(){
        super();
        areaId = "";
        transferAreaId = "";
        areaName = "";
        backupServerAddress = "";
        enableListenCall = false;
        snapPort = 11005;
    }
}
