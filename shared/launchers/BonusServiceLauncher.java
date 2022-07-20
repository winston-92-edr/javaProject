package com.mynet.shared.launchers;

import com.mynet.bonusservice.BonusService;
import com.mynet.shared.analytics.AnalyticsLogController;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.connection.ProxyToBonus;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.MessageProcessController;
import com.mynet.shared.network.ServerToServer;
import com.mynet.shared.network.ServerToServerMessageProcessor;
import com.mynet.shared.network.ServerToServerTCPServer;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.DatabaseWorker;
import com.mynet.shared.shutdown.BonusShutDownManager;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BonusServiceLauncher {
    static Logger logger;
    public static NodeData currentNode;

    public static void launch(String groupId) {
        BonusServiceLauncher.logger = LoggerFactory.getLogger(BonusServiceLauncher.class);

        CacheController.init();
        DatabaseWorker.getInstance().init();
        ServerGlobalVariables.init();
        RabbitMQLogController.init();
        AnalyticsLogController.init();

        MessageProcessController.init();

        try {
            currentNode = DBController.getInstance().getServerNode(NodesType.bonus, groupId, ServerType.NONE);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }

        BonusService.init(CacheController.getInstance());

        String host = ServerConfiguration.get("TCP.host");
        int port = ServerConfiguration.getInt("TCP.port", -1);
        if (host == null || port == -1) {
            System.out.println("server.host or server.port not found, exiting..");
            System.exit(2);
        }

        ProxyToBonus proxyToBonus = new ProxyToBonus();
        initServer(proxyToBonus, host, port);

        CacheController.getInstance().setBonusNode(currentNode, groupId);
        CacheController.getInstance().publishBonusNodeAddEvent(currentNode);
        Runtime.getRuntime().addShutdownHook(new BonusShutDownManager(currentNode.getId(), groupId));
    }

    private static void initServer(ServerToServerMessageProcessor messageProcessor, String host, int port) {
        ServerToServerTCPServer server = new ServerToServerTCPServer(ServerToServer.ServerType.BONUS, messageProcessor, host, port);
        server.start();
    }

}
