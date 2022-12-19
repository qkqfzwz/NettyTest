package com.example.nettytest.backend.backenddevice;

import com.example.nettytest.pub.LogWork;
import com.example.nettytest.pub.commondevice.UdpNetDevice;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class BackEndUdpDevice extends UdpNetDevice {
    int devType;
    
    public BackEndUdpDevice(String id,int devType){
        super(id);
        this.devType = devType;
    }

    @Override
    public boolean UpdatePeerAddress(DatagramSocket socket, InetAddress address, int port) {
        LogWork.Print(LogWork.BACKEND_NET_MODULE,LogWork.LOG_DEBUG,"Server Update Peer %s:%d for Dev %s",address.getHostAddress(),port,id);
        return super.UpdatePeerAddress(socket, address, port);
    }

    @Override
    public int SendBuffer(byte[] data) {
        if(peerAddress!=null){
            LogWork.Print(LogWork.BACKEND_NET_MODULE,LogWork.LOG_DEBUG,"Server Dev %s Send Data to %s:%d",id,peerAddress.getHostAddress(),peerPort);
            return super.SendBuffer(data);
        }else{
            return -1;
        }
    }
}
