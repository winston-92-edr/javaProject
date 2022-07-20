package com.mynet.shared.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocalNetworkMessageDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(LocalNetworkMessageDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {


        in.skipBytes(4); // skip length
        short protocol = in.readShort();
        int cmd = in.readShort();
       // int type = in.readShort();
        int flags = in.readShort();
        boolean isSuccess = in.readByte() == 1;
        long timestamp = in.readLong();

        String id = null;

        int idLen = in.readShort();
        if (idLen > 0) {
            ByteBuf idBuf = ByteBufAllocator.DEFAULT.buffer(idLen);
            in.readBytes(idBuf, idLen);
            id = idBuf.toString(CharsetUtil.UTF_8);
            ReferenceCountUtil.release(idBuf);
        }

        ByteBuf message = ByteBufAllocator.DEFAULT.buffer(in.readableBytes());
        in.readBytes(message, in.readableBytes());
        String mes = message.toString(CharsetUtil.UTF_8);
        ReferenceCountUtil.release(message);

        NetworkMessage req = new NetworkMessage(GameCommands.forCode(cmd));
        req.setProtocol(protocol);
        req.setId(id);
        req.setSuccess(isSuccess);
        req.setData(mes);
        req.setTimestamp(timestamp);

        logger.info("<< " + req);

        out.add(req);


    }
}