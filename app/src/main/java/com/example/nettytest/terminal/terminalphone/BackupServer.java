package com.example.nettytest.terminal.terminalphone;


import com.example.nettytest.pub.HandlerMgr;
import com.example.nettytest.pub.LogWork;
import com.example.nettytest.pub.commondevice.PhoneDevice;
import com.example.nettytest.pub.protocol.ProtocolPacket;
import com.example.nettytest.pub.protocol.RegResPack;
import com.example.nettytest.userinterface.UserInterface;

public class BackupServer extends TerminalPhone {
    
    public BackupServer(String id,int type){
        super(id,type);
    }

    @Override
    public void UpdateRegStatus(int status, RegResPack resP){
        super.UpdateRegStatus(status,resP);
        if(status == ProtocolPacket.STATUS_OK) {
            LogWork.Print(LogWork.TERMINAL_PHONE_MODULE, LogWork.LOG_DEBUG, "BackServer %s Update Reg Status Success, try to suspend Backup Call Server", id);
            HandlerMgr.ActiveBackupServer(false);
            HandlerMgr.BackupAreaInfo(areaId);
        }else if(status==ProtocolPacket.STATUS_TIMEOVER){
            LogWork.Print(LogWork.TERMINAL_PHONE_MODULE, LogWork.LOG_DEBUG, "BackServer %s Update Reg TimeOver , try to resume Backup Call Server", id);
            HandlerMgr.ActiveBackupServer(true);
        }
    }    
}


