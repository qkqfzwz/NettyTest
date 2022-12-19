package com.example.nettytest.pub.commondevice;


import java.net.SocketAddress;

import com.example.nettytest.pub.LogWork;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class TcpNetDevice extends NetDevice{
    // for netty device
    protected Channel channel;


    public TcpNetDevice(String id,String address){
        super(id,address);
        netType = TCP_NET_DEVICE;
    }

    public TcpNetDevice(String id){
        super(id);
        netType = TCP_NET_DEVICE;
    }

    public void SendBuffer(ByteBuf buf){
        if(channel!=null) {
            if(channel.isActive()&&channel.isWritable()) {
                channel.writeAndFlush(buf);
            }
        }
    }


    @Override
    public String GetNetAddress() {
        String address= GetChannelAddress(channel);

        return address;
    }

    protected String GetChannelAddress(Channel chn){
        String address  = "";
        if(chn!=null){
            address = chn.remoteAddress().toString();
            if(address.contains("/")){
                address = address.substring(address.indexOf('/')+1);
            }
            if(address.contains(":")){
                address = address.substring(0,address.indexOf(':'));
            }
        }

        return address;
    }

    protected int GetChannelPort(Channel chn) {
        int port = 0;
        String address="";
        String portStr;
        if(chn!=null) {
            address = chn.remoteAddress().toString();
            if(address.contains("/")){
                address = address.substring(address.indexOf('/')+1);
            }
            if(address.contains(":")){
                portStr = address.substring(address.indexOf(':')+1);
                port= Integer.parseInt(portStr);
            }
        }
        
        return port;
    }

    public boolean UpdateChannel(Channel ch){
        boolean result = false;
        if(channel==null) {
            channel = ch;
            result = true;
        }else if(GetChannelAddress(channel).compareToIgnoreCase(GetChannelAddress(ch))==0){
            channel = ch;
            result = true;
        }else{
            channel = ch;
            result = true;
        }
        return result;
    }

    public boolean CleanChannel(Channel ch) {
        boolean result = false;
        
        if(channel!=null) {
            if(GetChannelAddress(channel).compareToIgnoreCase(GetChannelAddress(ch))==0&&GetChannelPort(channel)==GetChannelPort(ch)) {
                LogWork.Print(LogWork.BACKEND_NET_MODULE,LogWork.LOG_DEBUG,"Clear Dev %s Channel %s:%d ",id,GetChannelAddress(channel),GetChannelPort(channel));
                channel = null;
            }else {
                LogWork.Print(LogWork.BACKEND_NET_MODULE,LogWork.LOG_ERROR,"Not Clear Dev %s Channel %s:%d when the request is %s:%d",id,GetChannelAddress(channel),GetChannelPort(channel),GetChannelAddress(ch),GetChannelPort(ch));
            }
        }
        
        return result;       
    }


    @Override
    public void Close() {
        super.Close();
        if(channel!=null) {
            channel.close();
            channel = null;
        }
    }
}
