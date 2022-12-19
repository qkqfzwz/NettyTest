package com.example.nettytest.pub.protocol;

public class RegResPack extends ProtocolPacket{
    public int status;
    public String result;
    public String areaId;
    public String areaName;
    public String transferAreaId;
    public String backupServerAddress;
    public int snapPort;
    public boolean listenCallEnable;

    public RegResPack(){
        super();
        type = ProtocolPacket.REG_RES;
        result = "";
        areaId = "";
        transferAreaId = "";
        areaName = "";
        backupServerAddress = "";
        listenCallEnable = false;
        snapPort = 11005;
    }

    public RegResPack(int status,RegReqPack regPack){
        ExchangeCopyData(regPack);
        type = ProtocolPacket.REG_RES;
        this.status = status;
        result = ProtocolPacket.GetResString(status);
        areaId = "";
        areaName = "";
        transferAreaId = "";
        backupServerAddress = "";
        listenCallEnable = false;
        snapPort = 11005;
    }
}
