package com.example.nettytest.backend.backenddevice;


import com.example.nettytest.pub.LogWork;
import com.example.nettytest.pub.commondevice.PhoneDevice;
import com.example.nettytest.pub.commondevice.TcpNetDevice;

import io.netty.channel.Channel;

public class BackEndTcpDevice extends TcpNetDevice {
    // for udp device
    int devType;

    public BackEndTcpDevice(String ID,int devType){
        super(ID);
        this.devType = devType;
    }

    @Override
    public boolean UpdateChannel(Channel ch){
        boolean result = false;
        if(ch==null) {
            channel= ch;
            LogWork.Print(LogWork.BACKEND_NET_MODULE, LogWork.LOG_ERROR, "BackupServer Device %s Reset the Connection",id);
            result = true;
        }else {
        if(channel==null) {
                LogWork.Print(LogWork.BACKEND_NET_MODULE, LogWork.LOG_DEBUG, "BackupServer Device %s update Connection to %s",id,GetChannelAddress(ch));
            channel= ch;
            result = true;
            }else if(GetChannelAddress(channel).compareToIgnoreCase(GetChannelAddress(ch))==0){
                LogWork.Print(LogWork.BACKEND_NET_MODULE, LogWork.LOG_DEBUG, "BackupServer Device %s update Connection Remain Same Address ",id,GetChannelAddress(channel));
            channel = ch;
            result = true;
        }else if(devType!=PhoneDevice.SERVER_BACKUP_DEVICE){
                LogWork.Print(LogWork.BACKEND_NET_MODULE, LogWork.LOG_DEBUG, "BackupServer Device %s update Connection from %s to %s",id,GetChannelAddress(channel),GetChannelAddress(ch));
            channel = ch;
            result = true;
        }else {
                LogWork.Print(LogWork.BACKEND_NET_MODULE, LogWork.LOG_ERROR, "BackupServer Device %s had Connect from %s, Server reject connect from %s",id,GetChannelAddress(channel),GetChannelAddress(ch));
            }
        }
        return result;
    }

}
