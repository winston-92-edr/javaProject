package com.mynet.shared.node;

import com.mynet.observer.ObserverCenter;
import com.mynet.observer.ObserverEvents;
import com.mynet.observer.ObserverRunnable;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.network.NetworkMessageQueue;
import com.mynet.shared.network.NetworkMessageWrapper;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class Node {
    final Logger logger = LoggerFactory.getLogger(Node.class);

    private int id;
    private String host;
    private int port;
    private NodeConnection connection;
    private UserController userController;
    private String type;
    private NetworkMessageQueue messageQueue;
    private NodeController nodeController;
    private NodeData serverNode;
    private AtomicBoolean delayMessages;
    private String groupId;
    private final ServerType serverType;

    public Node(int id, String host, int port, UserController userController, String type, NodeData nodeData, NodeController nodeController, String groupId, ServerType serverType) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.serverNode = nodeData;
        this.userController = userController;
        this.nodeController = nodeController;
        this.connection = new NodeConnection(this, userController, nodeController, serverNode);
        this.type = type;
        this.delayMessages = new AtomicBoolean(false);
        messageQueue = new NetworkMessageQueue(1);
        messageQueue.init();

        ObserverCenter.getInstance().addObserver(ObserverEvents.CHANNEL_ACTIVE, new ChannelActive());

        this.groupId = groupId;
        this.serverType = serverType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public ServerType getServerType() {
        return serverType;
    }

    void connect() {
        this.connection.connect();
    }

    public void sendRequest(NetworkMessage request, ProxyUser user) {
        if(user != null) {
            request.setId(user.getId());
        }

        messageQueue.addMessage(new NetworkMessageWrapper(request, connection.getChannel()));
    }

    public boolean isConnected(){
        return this.connection.isConnected();
    }

    public void reconnect() {
        if(this.connection.isConnected()){
            logger.info("shared.node connected already, disconnecting first!");
            this.connection.close();
        }
        this.connection = new NodeConnection(this, userController, nodeController, serverNode);
        this.connection.connect();
    }

    public void disconnect() {
        if(this.connection.isConnected()){
            logger.info("shared.node disconnected " + this);
            this.connection.close();
        }
    }

    public void addUser(ProxyUser user){
        if(isConnected()) {
            NetworkMessage addReq = new NetworkMessage(GameCommands.ADD_USER);
            sendRequest(addReq, user);
        }else{
            logger.error("no longer connected to shared.node, can't add");
        }
    }

    public void addUser(ProxyUser user, String data){
        if(isConnected()) {
            NetworkMessage addReq = new NetworkMessage(GameCommands.ADD_USER);
            addReq.setData(data);
            sendRequest(addReq, user);
        }else{
            logger.error("no longer connected to shared.node, can't add");
        }
    }

    public void removeUser(ProxyUser user) {
        if(isConnected()){
            NetworkMessage removeReq = new NetworkMessage(GameCommands.REMOVE_USER);
            sendRequest(removeReq, user);
        }else{
            logger.error("no longer connected to shared.node, can't remove");
        }
    }

    public String getGroupId() {
        return groupId;
    }

    @Override
    public String toString() {
        return "[ ID: " + this.id + "] -> " + this.host + " " + this.port + " " + (this.isConnected() ? "CONNECTED" : "NOT CONNECTED");
    }

    class ChannelActive extends ObserverRunnable{

        private final String id;

        public ChannelActive() {
            this.id = "NodeChannel_" + System.currentTimeMillis();
        }

        @Override
        protected void setData(Object data) {

        }

        @Override
        protected String getId() {
            return id;
        }

        @Override
        public void run() {
            delayMessages.set(true);
        }
    }
}
