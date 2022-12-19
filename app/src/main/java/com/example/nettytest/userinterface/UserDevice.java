package com.example.nettytest.userinterface;

public class    UserDevice {
    public int type;
    public String devid;
    public String devName;
    public String areaId;
    public String areaName;
    public String bedName;
    public String roomId;
    public String roomName;
    public boolean isRegOk;
    public int netMode;

    public UserDevice(){
        devid = "";
        areaId = "";
        areaName = "";
        bedName = "";
        roomId = "";
        roomName = "";
        isRegOk = false;
    }
}
