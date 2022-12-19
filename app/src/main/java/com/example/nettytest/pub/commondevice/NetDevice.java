package com.example.nettytest.pub.commondevice;

public class NetDevice {
    final static int UDP_NET_DEVICE  = 1;
    final static int TCP_NET_DEVICE = 2;
    final static int RAW_TCP_NET_DEVICE = 3;
    final static int UNKNOW_NET_DEVICE = 0xff;
    protected String id;
    protected String serverAddress="";
    int netType;

    public NetDevice(String id,String address){
        this.id = id;
        serverAddress = address;
        netType = UNKNOW_NET_DEVICE;
    }

    public NetDevice(String id){
        this.id = id;
        netType = UNKNOW_NET_DEVICE;
   }

    public String GetServerAddress(){
        return serverAddress;
    }

    public int GetNetMode(){
        return netType;
    }

    public String GetNetAddress(){
        return "";
    }

    public int SendBuffer(byte[] data){
        return  0;
    }

    public void Close(){

    }

    public void Stop(){

    }
}
