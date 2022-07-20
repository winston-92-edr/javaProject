package com.mynet.shared.node;

import com.mynet.observer.ObserverCenter;
import com.mynet.observer.ObserverEvents;
import com.mynet.shared.network.MessageProcessController;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.ProxyUser;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.network.RegisterProxyMessage;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.types.MessageTypes;
import com.mynet.shared.types.RequestType;
import com.mynet.shared.user.UserController;

public class NodeConnectionHandler extends ChannelInboundHandlerAdapter {
    final Logger logger = LoggerFactory.getLogger(NodeConnectionHandler.class);
    Node node;
    NodeController nodeController;
    NodeData serverNode;

    public NodeConnectionHandler(Node node, NodeController nodeController, NodeData nodeData) {
        this.node = node;
        this.nodeController = nodeController;
        this.serverNode = nodeData;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("node connection active");

        // register node
        RegisterProxyMessage data = new RegisterProxyMessage(serverNode.getId());
        NetworkMessage message = new NetworkMessage(GameCommands.REGISTER_NODE);
        message.setDataAsJSON(data);
        node.sendRequest(message, null);

        ObserverCenter.getInstance().emit(ObserverEvents.CHANNEL_ACTIVE);

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.info("node connection registered");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        NetworkMessage response = (NetworkMessage) msg;

        if (response != null) {

            if(response.getId() != null){
                ProxyUser user = UserController.getInstance().getUser(response.getId());
                if(user != null){
                    if(response.getCmd().getType() != RequestType.SERVER) {
                        user.send(response);
                    }
                }else{
                    // mevcut proxy üzerinde olmayan bir user node'ta mevcut
                    // user yok ya da başka proxy'de
                }
            }

            if(response.getCmd().getType() == RequestType.SERVER){
                processServerMessages(response);
            }


        }

    }

    private void processServerMessages(NetworkMessage response) {
        //Node TCP channel reads a message
        //it means: this node (proxy) get a message as a client from another node
        MessageProcessController controller = MessageProcessController.getInstance();
        if(controller == null) return;

        try {
            controller.processMessage(response);
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(NodeConnectionHandler.class.getName(), cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println ("node connection closed");
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("node connection closed");
    }
}
