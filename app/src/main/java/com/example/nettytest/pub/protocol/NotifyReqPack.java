package com.example.nettytest.pub.protocol;


public class NotifyReqPack extends ProtocolPacket {
    public int notifyType;
    public String notifyId;
    public String notifyTitle;
    public String notifyContent;
    public String notifySender;
    public String notifyReceiver;

    public NotifyReqPack(){
        super();
        notifyType = 0;
        notifyId = "";
        notifyTitle = "";
        notifyContent = "";
        notifySender = "";
        notifyReceiver = "";
    }
    
}

