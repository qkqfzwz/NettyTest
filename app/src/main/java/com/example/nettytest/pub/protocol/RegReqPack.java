package com.example.nettytest.pub.protocol;

public class RegReqPack extends ProtocolPacket{
    public int devType;
    public String devID;
    public String address;
    public int expireTime;
    public int hasBackupServer;
    
    public RegReqPack(){
        super();
        
        expireTime = 60;
        hasBackupServer = 0;
    }
}


