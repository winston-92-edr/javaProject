package com.mynet.shared.launchers;

import com.mynet.proxyserver.network.ProxyServerMessageController;
import com.mynet.proxyserver.network.TestTCPProxyServer;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.connection.NodeToService;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.*;
import com.mynet.shared.node.NodeController;
import com.mynet.shared.resource.workers.ServerCountsWorker;
import com.mynet.shared.shutdown.ProxyShutdownManager;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.login.LoginQueue;
import com.mynet.proxyserver.network.TCPProxyServer;
import com.mynet.proxyserver.network.websocket.WebSocketProxyServer;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.DatabaseWorker;
import java.util.concurrent.atomic.AtomicLong;

public class ProxyServerLauncher {
    static Logger logger;

    public static NetworkMessageQueue userMessageQueue;
    public static NodeData currentNode;
    public static AtomicLong activeWebSocketConnections = new AtomicLong(0);
    public static AtomicLong activeTCPConnections = new AtomicLong(0);

    public static void launch(String type, ServerType serverType, String groupId) {

        ProxyServerLauncher.logger = LoggerFactory.getLogger(ProxyServerLauncher.class);

        int workerCount = ServerConfiguration.getInt("worker.count", 1);

        userMessageQueue = new NetworkMessageQueue(workerCount);
        userMessageQueue.init();

        CacheController.init();
        DatabaseWorker.getInstance().init();
        ServerGlobalVariables.init();
        RabbitMQLogController.init();

        try {

            currentNode = DBController.getInstance().getServerNode(NodesType.proxy, groupId, ServerType.NONE);

        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }

        ServerCountsWorker.init(type, currentNode.getId());

        //TODO: MessageProcessController.init() is in proxyController
        ProxyServerMessageController proxyController = new ProxyServerMessageController();


        if (currentNode == null) {
            logger.error("Can't initialize node data!!");
            System.exit(1);
        }

        NodeController nodeController = new NodeController(currentNode);
        LoginQueue.init(nodeController);
        UserController.init(nodeController, serverType, groupId);
        if (!initTCP(nodeController)) {
            System.exit(1);
        }

        String testing = ServerConfiguration.get("TCP.test");
        if(testing != null){
            initTestServer(nodeController);
        }

        nodeController.reloadNodeMappingProxy();
        nodeController.addRedisEventListeners();

        TournamentLevelController.init();

        Runtime.getRuntime().addShutdownHook(new ProxyShutdownManager(currentNode.getId()));

        boolean wsActive = ServerConfiguration.getInt("websocket.active", 0) == 1;
        if (wsActive) {
            if (!initWebSocket(nodeController)) {
                System.out.println("web socket can not started!");
                System.exit(1);
            }
        }

        // ServerEventsWorker.init(currentNode.getId());
        // ZombieUsersWorker.init(currentNode.getId());


        logger.info("Proxy server node is: " + currentNode.toString());
    }

    private static boolean initTCP(NodeController nodeController) {
        String tcpHost = ServerConfiguration.get("TCP.host");
        int tcpPort = ServerConfiguration.getInt("TCP.port");

        if (tcpHost == null) {
            logger.error("TCP.host and TCP.port not found in server.properties");
            return false;
        }

        TCPProxyServer server = new TCPProxyServer(nodeController, tcpHost, tcpPort);
        server.startServer();

        return true;
    }

    private static boolean initWebSocket(NodeController nodeController) {
        String websocketHost = ServerConfiguration.get("websocket.host");
        int websocketPort = ServerConfiguration.getInt("websocket.port");
        String websocketCert = ServerConfiguration.get("websocket.cert");
        String websocketKey = ServerConfiguration.get("websocket.key");

        if (websocketHost == null) {
            logger.error("websocket.host or websocket.port not found in server.properties");
            return false;
        }

        if (websocketKey == null || websocketCert == null) {
            logger.error("websocket.cert or websocket.key not found, starting insecure websocket server");
            websocketCert = null;
            websocketKey = null;
        }


        WebSocketProxyServer websocketServer = new WebSocketProxyServer(nodeController, websocketHost, websocketPort, websocketCert, websocketKey);
        websocketServer.startServer();
        return true;

    }

    private static void initTestServer(NodeController controller) {
        String host = ServerConfiguration.get("TCP.host");
        int port = ServerConfiguration.getInt("TCP.testPort");
        TestTCPProxyServer server = new TestTCPProxyServer(controller, host, port);
        server.start();
    }
}
