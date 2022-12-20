package com.example.nettytest.backend.servernet;

import com.example.nettytest.pub.HandlerMgr;
import com.example.nettytest.pub.LogWork;
import com.example.nettytest.pub.protocol.ProtocolFactory;
import com.example.nettytest.pub.protocol.ProtocolPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;

public class NettyTestServerHandler extends ChannelInboundHandlerAdapter {

    String devId = "";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf)msg;
        try {
            ProtocolPacket packet = ProtocolFactory.ParseData(buf);
            if(packet!=null) {
                devId = packet.sender;
                if(HandlerMgr.UpdateBackEndDevChannel(packet.sender,ctx.channel())){
                    HandlerMgr.BackEndProcessPacket(packet);
                }
            }
        }finally {
            buf.release();
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
//        cause.printStackTrace();
        if(cause instanceof ReadTimeoutException) {
            LogWork.Print(LogWork.BACKEND_NET_MODULE,LogWork.LOG_TEMP_DBG,"Netty Sever Dev %s Caught err %s",devId,cause.toString());
            HandlerMgr.CleanBackEndChannel(devId, ctx.channel());
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(!devId.isEmpty()){
            LogWork.Print(LogWork.BACKEND_NET_MODULE,LogWork.LOG_ERROR,"Dev %s TCP link Inactive ",devId);
//            HandlerMgr.UpdateBackEndDevChannel(devId,null);
            HandlerMgr.CleanBackEndChannel(devId, ctx.channel());
        }
        super.channelInactive(ctx);
    }

}
