package com.example.nettytest.pub.protocol;

public class NotifyResPack extends ProtocolPacket{

    public String notifyId;
    public String notifyReceiver;

    public NotifyResPack(){
        notifyId = "";
        notifyReceiver = "";
    }

    public NotifyResPack(NotifyReqPack req){

        super();
        ExchangeCopyData(req);
        type = NOTIFY_SEND_RES;
        notifyId = req.notifyId;
        notifyReceiver = "";
        
    }
}


