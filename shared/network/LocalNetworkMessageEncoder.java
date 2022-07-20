package com.mynet.shared.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalNetworkMessageEncoder extends MessageToByteEncoder<NetworkMessage> {
    private static final Logger logger = LoggerFactory.getLogger(LocalNetworkMessageEncoder.class);

    // 4byte Length | 2byte protocol | 2byte CMD | 2byte Type | 2byte Flags | 1byte isSuccess | 8byte Timestamp | 4byte delay
    // | 2byte ID-Length | ID-Length byte ID | Message
    @Override
    protected void encode(ChannelHandlerContext ctx, NetworkMessage msg, ByteBuf out) throws Exception {

        out.writeShort(msg.getProtocol());
        out.writeShort(msg.getCmd().getValue());
        out.writeShort(msg.getCmd().getType().getValue());
        if(msg.isSuccess())
            out.writeByte(1);
        else
            out.writeByte(0);


        out.writeLong(msg.getTimestamp());

        if(msg.getId() == null)
        {
            out.writeShort(0);
        }else {
            byte[] idAr = msg.getId().getBytes(CharsetUtil.UTF_8);
            out.writeShort(idAr.length);
            out.writeBytes(idAr, 0, idAr.length);
        }

        long jsonSize = 0;

//        msg.serialize();
        if(msg.getData() != null) {
            byte[] d = msg.getData().getBytes(CharsetUtil.UTF_8);
            out.writeBytes(d, 0, d.length);
            jsonSize = d.length;
        }

        logger.debug("[J:" +jsonSize + "] >> " + msg);
    }

}
