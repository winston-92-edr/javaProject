package com.mynet.shared.network;

import com.mynet.questservice.QuestController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerToServerTCPChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerToServerTCPChannelHandler.class);
    private ServerToServer connection;
    private ServerToServerMessageProcessor messageProcessor;
    private ServerToServer.ServerType serverType;

    public ServerToServerTCPChannelHandler(ServerToServerMessageProcessor messageProcessor, ServerToServer.ServerType serverType){
        this.messageProcessor = messageProcessor;
        this.serverType = serverType;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if(serverType.equals(ServerToServer.ServerType.QUEST)){
            QuestController questController = QuestController.getInstance();
            questController.incrementProxyCount();
            if(!questController.isRabbitConnected()) {
                questController.controlConsumer(true);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if(serverType.equals(ServerToServer.ServerType.QUEST)){
            QuestController.getInstance().decrementProxyCount();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg != null) {
            try {
                if(connection == null){
                    connection = ServerToServerController.getInstance().registerConnection(ctx.channel(), (NetworkMessage) msg, messageProcessor, serverType);
                    if(connection == null){
                        ctx.channel().close();
                    }
                }else{
                    connection.messageProcessor.processMessage((NetworkMessage) msg);
                }
            }catch (Exception e){
              //  logger.error(e.getMessage(), e.getCause());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable error) throws Exception {
        logger.error(error.getMessage(), error.getCause());
    }
}
