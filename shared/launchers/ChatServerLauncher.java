package com.mynet.shared.launchers;

import com.mynet.chatserver.ChatController;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.connection.ProxyToChat;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.HttpServer.MonitorServer;
import com.mynet.shared.network.MessageProcessController;
import com.mynet.shared.network.ServerToServer;
import com.mynet.shared.network.ServerToServerMessageProcessor;
import com.mynet.shared.network.ServerToServerTCPServer;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.DatabaseWorker;
import com.mynet.shared.shutdown.ChatShutDownManager;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatServerLauncher {
    static Logger logger;
    public static NodeData currentNode;

    public static void launch(String groupId) {
        ChatServerLauncher.logger = LoggerFactory.getLogger(ChatServerLauncher.class);

        CacheController.init();
        DatabaseWorker.getInstance().init();
        ServerGlobalVariables.init();
        RabbitMQLogController.init();

        MessageProcessController.init();

        try {
            currentNode = DBController.getInstance().getServerNode(NodesType.chat, groupId, ServerType.NONE);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }

        int monitorPort = ServerConfiguration.getInt("monitor.port", -1);
        if (monitorPort > -1) {
            MonitorServer monitorServer = new MonitorServer(monitorPort, currentNode.getId());
            monitorServer.addChatServiceServlets();
            monitorServer.startServer();
        }

        ChatController.init(currentNode.getId(), groupId);

        String host = ServerConfiguration.get("TCP.host");
        int port = ServerConfiguration.getInt("TCP.port", -1);
        if (host == null || port == -1) {
            System.out.println("server.host or server.port not found, exiting..");
            System.exit(2);
        }

        ProxyToChat proxyToChat = new ProxyToChat();
        initServer(proxyToChat, host, port);

        CacheController.getInstance().setChatNode(currentNode, groupId);
        CacheController.getInstance().publishChatNodeAddEvent(currentNode);
        Runtime.getRuntime().addShutdownHook(new ChatShutDownManager(currentNode.getId(), groupId));
    }

    private static void initServer(ServerToServerMessageProcessor messageProcessor, String host, int port) {
        ServerToServerTCPServer server = new ServerToServerTCPServer(ServerToServer.ServerType.CHAT, messageProcessor, host, port);
        server.start();
    }

}
