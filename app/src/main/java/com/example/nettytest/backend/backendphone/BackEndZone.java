package com.example.nettytest.backend.backendphone;

import com.example.nettytest.pub.AlertConfig;
import com.example.nettytest.pub.CallParams;
import com.example.nettytest.pub.HandlerMgr;
import com.example.nettytest.pub.LogWork;
import com.example.nettytest.pub.UniqueIDManager;
import com.example.nettytest.pub.commondevice.PhoneDevice;
import com.example.nettytest.pub.phonecall.CommonCall;
import com.example.nettytest.pub.protocol.ListenCallReqPack;
import com.example.nettytest.pub.protocol.ListenClearReqPack;
import com.example.nettytest.pub.protocol.NotifyReqPack;
import com.example.nettytest.pub.protocol.ProtocolPacket;
import com.example.nettytest.pub.protocol.TransferChangeReqPack;
import com.example.nettytest.pub.result.FailReason;
import com.example.nettytest.pub.transaction.Transaction;
import com.example.nettytest.userinterface.PhoneParam;
import com.example.nettytest.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BackEndZone {
    public String areaId;
    String areaName;
    CallParams params;
    public String transferAreaId;
    public ArrayList<AlertConfig> alertConfigList;
    public String backupServerDeviceId;
    public static String DEFAULT_AREA_ID = "Default Area";

    HashMap<String,BackEndPhone> phoneList;

    public BackEndZone(String id,String name){
        areaId = id;
        areaName = name;
        transferAreaId = "";
        phoneList = new HashMap<>();
        params = new CallParams();
        alertConfigList = new ArrayList<>();
        backupServerDeviceId = "";
    }

    public BackEndPhone GetDevice(String id){
        return phoneList.get(id);
    }

    public AlertConfig GetAlertConfig(int alertType){
        AlertConfig matchecConfig = null;
        for(AlertConfig config:alertConfigList){
            if(config.alertType == alertType-CommonCall.ALERT_TYPE_BEGIN){
                matchecConfig = config;
            }
        }
        return matchecConfig;
    }


    public ArrayList<BackEndPhone> GetBackEndPhoneList(){
        PhoneDevice dev;
        ArrayList<BackEndPhone> lists = new ArrayList<>();
        for(BackEndPhone phone:phoneList.values()){            
            lists.add(phone);
        }
        return lists;
    }

    public boolean CheckBackupServerRegisted(String devId){
        boolean result = true;
        for(BackEndPhone phone:phoneList.values()){
            if(phone.type==PhoneDevice.SERVER_BACKUP_DEVICE){
                if(phone.isReg){
                    if(devId.compareToIgnoreCase(phone.id)!=0){
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }
    

    public int RemoveAllDevices(){
        for(Iterator<Map.Entry<String, BackEndPhone>> it = phoneList.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, BackEndPhone>item = it.next();
            BackEndPhone phone = item.getValue();
            phone.paramList.clear();
            it.remove();
        }
        return FailReason.FAIL_REASON_NO;
    }

    public int RejectAllDevice(){
        
        for(Iterator<Map.Entry<String, BackEndPhone>> it = phoneList.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, BackEndPhone>item = it.next();
            BackEndPhone phone = item.getValue();
            if(phone.type==PhoneDevice.SERVER_BACKUP_DEVICE)
                continue;
            if(!phone.isReg)
                continue;
            NotifyReqPack notifyReq = BuildNotifyPacket(PhoneParam.CALL_SERVER_ID,phone.id, UserInterface.NOTIFY_MAIN_SERVER_ACTIVE);
            Transaction notifyTransaction = new Transaction(phone.id,notifyReq, Transaction.TRANSCATION_DIRECTION_S2C);
            LogWork.Print(LogWork.BACKEND_PHONE_MODULE,LogWork.LOG_DEBUG,"BackServer Send  Main Server Active to Device %s!",phone.id);
            HandlerMgr.AddBackEndTrans(notifyReq.msgID,notifyTransaction);
        }

        
        return 0;
    }

    public int RemoveDevice(String id){
        int result = FailReason.FAIL_REASON_NO;
        BackEndPhone phone = phoneList.remove(id);
        if(phone==null){
            result = FailReason.FAIL_REASON_NOTFOUND;
        }else{
            phone.paramList.clear();
        }
        return result;
    }

    public int NotifyTransferChangExcept(String devId){
        for(BackEndPhone phone:phoneList.values()){
            if(phone.type==PhoneDevice.NURSE_CALL_DEVICE||phone.type==PhoneDevice.DOCTOR_CALL_DEVICE){
                if(devId.compareToIgnoreCase(phone.id)!=0){
                    TransferChangeReqPack req = new TransferChangeReqPack(phone.id);
                    req.sender = PhoneParam.CALL_SERVER_ID;
                    req.receiver = phone.id;
                    if(!transferAreaId.isEmpty()){
                        req.transferAreaId = transferAreaId;
                        req.state = true;
                    }
                    req.msgID = UniqueIDManager.GetUniqueID(phone.id,UniqueIDManager.MSG_UNIQUE_ID);
                    Transaction trans = new Transaction(phone.id,req,Transaction.TRANSCATION_DIRECTION_S2C);
                    HandlerMgr.AddBackEndTrans(req.msgID, trans);
                }
            }
        }
        return 0;
    }

    public String GetTransferAreaId(){
        return transferAreaId;
    }

    public String GetWorkAreaId(){
        return areaId;
    }

    public int IncreaseRegTick(){
        for(BackEndPhone phone:phoneList.values()) {
            phone.IncreaseRegTick();
            if(!phone.isReg){
                if(backupServerDeviceId.compareToIgnoreCase(phone.id)==0){
                    backupServerDeviceId = "";
                }
            }
        }
        return 0;
    }

    public String GetBackupDeviceId(){
        return backupServerDeviceId;
    }

    public boolean SetBackupDeviceId(String server){
        boolean result = false;
        if(server.isEmpty()){
            backupServerDeviceId = server;
            result= true;
        }else if(backupServerDeviceId.isEmpty()){
            backupServerDeviceId = server;
            result = true;
        }else {
            if(backupServerDeviceId.compareToIgnoreCase(server)==0) {
                result = true;
            }
        }
        return result;
    }

    public int ClearAllListenExcept(String id){
        for(BackEndPhone phone:phoneList.values()) {
            if(phone.type == PhoneDevice.BED_CALL_DEVICE) {
                if (phone.enableListen&&id.compareToIgnoreCase(phone.id)!=0) {
                    HandlerMgr.BackEndCancelListenCall(phone.id);
                    ListenClearReqPack clearReqP = BuildListenClearPacket(phone.id);
                    Transaction clearTrans = new Transaction(phone.id,clearReqP,Transaction.TRANSCATION_DIRECTION_S2C);
                    HandlerMgr.AddBackEndTrans(clearReqP.msgID,clearTrans);
                    phone.enableListen = false;
                    LogWork.Print(LogWork.BACKEND_PHONE_MODULE,LogWork.LOG_DEBUG,"BackEnd Send Cmd to Clear Listen Flag on Dev %s",phone.id);
                }
            }
        }
        return 0;
    }

    public void AddDevice(String id,BackEndPhone phone){
        phoneList.put(id, phone);
    }


    public void GetListenDevices(ArrayList<BackEndPhone> devices, int callType){
            for(String devid:phoneList.keySet()){
                boolean isAdd = false;
                BackEndPhone phone = phoneList.get(devid);
                if(phone==null)
                    break;
                switch(callType){
                    case CommonCall.CALL_TYPE_BROADCAST:
                        switch(phone.type){
                            case BackEndPhone.BED_CALL_DEVICE:
                                if(params.boardCallToBed)
                                    isAdd = true;
                                break;
                            case BackEndPhone.DOOR_CALL_DEVICE:
                                if(params.boardCallToRoom)
                                    isAdd = true;
                                break;
                            case BackEndPhone.CORRIDOR_CALL_DEVICE:
                                if(params.boardCallToCorridor)
                                    isAdd = true;
                                break;
                            case BackEndPhone.NURSE_CALL_DEVICE:
                            case BackEndPhone.DOCTOR_CALL_DEVICE:
                                isAdd = false;
                                break;
                            case BackEndPhone.SERVER_BACKUP_DEVICE:
                                isAdd = false;
                                break;
                            case BackEndPhone.TV_CALL_DEVICE:
                                if(params.boardCallToTV)
                                    isAdd = true;
                                break;
                        }
                        break;
                    case CommonCall.CALL_TYPE_EMERGENCY:
                        switch(phone.type){
                            case BackEndPhone.BED_CALL_DEVICE:
                                if(params.emerCallToBed)
                                    isAdd = true;
                                else if(phone.enableListen)
                                    isAdd = true;
                                break;
                            case BackEndPhone.DOOR_CALL_DEVICE:
                                if(params.emerCallToRoom)
                                    isAdd = true;
                                break;
                            case BackEndPhone.CORRIDOR_CALL_DEVICE:
                                if(params.emerCallToCorridor)
                                    isAdd = true;
                                break;
                            case BackEndPhone.NURSE_CALL_DEVICE:
                            case BackEndPhone.DOCTOR_CALL_DEVICE:
                                isAdd = true;
                                break;
                            case BackEndPhone.TV_CALL_DEVICE:
                                if(params.emerCallToTV)
                                    isAdd = true;
                                break;
                            case BackEndPhone.SERVER_BACKUP_DEVICE:
                                isAdd = false;
                                break;
                        }
                        break;
                    case CommonCall.CALL_TYPE_NORMAL:
                    case CommonCall.CALL_TYPE_ASSIST:
                        switch(phone.type){
                            case BackEndPhone.BED_CALL_DEVICE:
                                if(params.normalCallToBed)
                                    isAdd = true;
                                else if(phone.enableListen)
                                    isAdd = true;
                                break;
                            case BackEndPhone.DOOR_CALL_DEVICE:
                                if(params.normalCallToRoom)
                                    isAdd = true;
                                break;
                            case BackEndPhone.CORRIDOR_CALL_DEVICE:
                                if(params.normalCallToCorridor)
                                    isAdd = true;
                                break;
                            case BackEndPhone.NURSE_CALL_DEVICE:
                                isAdd = true;
                                break;
                            case BackEndPhone.DOCTOR_CALL_DEVICE:
                                if(callType==CommonCall.CALL_TYPE_ASSIST)
                                    isAdd = true;
                                else
                                    isAdd = false;
                                break;
                            case BackEndPhone.TV_CALL_DEVICE:
                                if(params.normalCallToTV)
                                    isAdd = true;
                                break;
                            case BackEndPhone.SERVER_BACKUP_DEVICE:
                                isAdd = false;
                                break;
                        }
                        break;
                }
                if(CommonCall.IsAlertCall(callType)){
                    switch(phone.type){
                        case BackEndPhone.BED_CALL_DEVICE:
                            if(params.normalCallToBed)
                                isAdd = true;
                            else if(phone.enableListen)
                                isAdd = true;
                            break;
                        case BackEndPhone.DOOR_CALL_DEVICE:
                            if(params.normalCallToRoom)
                                isAdd = true;
                            break;
                        case BackEndPhone.CORRIDOR_CALL_DEVICE:
                            if(params.normalCallToCorridor)
                                isAdd = true;
                            break;
                        case BackEndPhone.NURSE_CALL_DEVICE:
                            isAdd = true;
                            break;
                        case BackEndPhone.DOCTOR_CALL_DEVICE:
                            isAdd = false;
                            break;
                        case BackEndPhone.TV_CALL_DEVICE:
                            if(params.normalCallToTV)
                                isAdd = true;
                            break;
                        case BackEndPhone.SERVER_BACKUP_DEVICE:
                            isAdd = false;
                            break;
                    }
                }
                if(isAdd){
                    devices.add(phone);
                }
            }
    }

    private ListenClearReqPack BuildListenClearPacket(String devid){
        ListenClearReqPack listenClearReqP = new ListenClearReqPack(devid);

        listenClearReqP.sender = PhoneParam.CALL_SERVER_ID;
        listenClearReqP.receiver = devid;
        listenClearReqP.msgID = UniqueIDManager.GetUniqueID(devid,UniqueIDManager.MSG_UNIQUE_ID);
        return listenClearReqP;
    }

    private NotifyReqPack BuildNotifyPacket(String sender,String receiver, int notifyType){
        NotifyReqPack notifyPack = new NotifyReqPack();

        notifyPack.sender = sender;
        notifyPack.receiver = receiver;
        notifyPack.type = ProtocolPacket.NOTIFY_SEND_REQ;
        notifyPack.msgID = UniqueIDManager.GetUniqueID(receiver,UniqueIDManager.MSG_UNIQUE_ID);

        notifyPack.notifyType = notifyType;
        notifyPack.notifySender = sender;
        notifyPack.notifyReceiver = receiver;

        return notifyPack;
    }
}


