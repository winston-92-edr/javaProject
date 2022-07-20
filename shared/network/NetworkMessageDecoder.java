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

public class NetworkMessageDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(NetworkMessageDecoder.class);

    // 4byte Length | 2byte protocol | 2byte CMD | 2byte Type | 2byte Flags  | Message

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

            in.skipBytes(4); // skip length
            short protocol = in.readShort();

//            if(protocol == NetworkConstants.PING_PROTOCOL){
//                ByteBuf message = ByteBufAllocator.DEFAULT.buffer(2);
//                message.writeShort(0);
//                ctx.channel().writeAndFlush(message);
//            }else {

                int cmd = in.readShort();
                int type = in.readShort();
                int flags = in.readShort();
                ByteBuf message = ByteBufAllocator.DEFAULT.buffer(in.readableBytes());
                in.readBytes(message, in.readableBytes());

                String mes = message.toString(CharsetUtil.UTF_8);
                ReferenceCountUtil.release(message);

                NetworkMessage req = new NetworkMessage();
                req.setCmd(GameCommands.forCode(cmd));
                req.setData(mes);

                req.setProtocol(protocol);

                out.add(req);

                logger.debug("[C: " + cmd + "]<< " + req);
//            }

    }
}
