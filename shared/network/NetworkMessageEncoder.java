package com.mynet.shared.network;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class NetworkMessageEncoder extends MessageToByteEncoder<NetworkMessage> {

    private static final Logger logger = LoggerFactory.getLogger(NetworkMessageEncoder.class);


    // 4byte Length | 2byte protocol | 2byte CMD | 2byte Type | 2byte Flags | 1byte isSuccess | 8byte Timestamp | 4byte delay | Message


    @Override
    protected void encode(ChannelHandlerContext ctx, NetworkMessage msg, ByteBuf out) throws Exception {

        //System.out.println("NetworkMessage: " + msg);
        out.writeShort(msg.getProtocol());
        //System.out.println("out protocol is here: " + msg.getProtocol());
        out.writeShort(msg.getCmd().getValue());
        //System.out.println("out cmd is here: " + msg.getCmd().getValue());
        out.writeShort(msg.getCmd().getType().getValue());
        //System.out.println("out cmd type is here: " + msg.getCmd().getType().getValue());
        out.writeShort(0); //Game Type
        if(msg.isSuccess())
            out.writeByte(1);
        else
            out.writeByte(0);

        // TODO: wrong change this.. it depends on jvm process not actual time
        long now = System.nanoTime();
        long latency = now - msg.getTimestamp();

        out.writeLong(now);

        // old delay message
        out.writeInt(0);

        //  msg.serialize();
        if(msg.getData() != null) {
            byte[] d = msg.getData().getBytes(CharsetUtil.UTF_8);
            out.writeBytes(d, 0, d.length);

        }

        // delimiter
//        String delimiter = "</messages>";
//        byte[] del = delimiter.getBytes(CharsetUtil.UTF_8);
//        out.writeBytes(del, 0, del.length);

        logger.debug("[C] [L:" + latency  + "] >> " + msg);
    }
}
