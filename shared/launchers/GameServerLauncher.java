package com.mynet.shared.launchers;

import com.mynet.matchserver.GameUser;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.analytics.AnalyticsLogController;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.*;
import com.mynet.gameserver.GameController;
import com.mynet.shared.connection.ProxyToGame;
import com.mynet.shared.network.HttpServer.MonitorServer;
import com.mynet.shared.node.NodeController;
import com.mynet.shared.resource.workers.ServerCountsWorker;
import com.mynet.shared.shutdown.GameShutdownManager;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.DatabaseWorker;

public class GameServerLauncher {
    private static Logger logger;
    public static NodeData currentNode;

    public static void launch(String type, ServerType serverType, String groupId) {
        logger = LoggerFactory.getLogger(GameServerLauncher.class);

        CacheController.init();
        DatabaseWorker.getInstance().init();
        ServerGlobalVariables.init();
        RabbitMQLogController.init();
        AnalyticsLogController.init();

        MessageProcessController.init();

        try {

            currentNode = DBController.getInstance().getServerNode(NodesType.game, groupId, serverType);

        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }


        String host = ServerConfiguration.get("TCP.host");
        int port = ServerConfiguration.getInt("TCP.port", -1);
        if (host == null || port == -1) {
            System.out.println("server.host or server.port not found, exiting..");
            System.exit(2);
        }

        ProxyToGame pg = new ProxyToGame(serverType);
        initServer(pg, host, port);

        int monitorPort = ServerConfiguration.getInt("monitor.port", -1);
        if (monitorPort > -1) {
            MonitorServer monitorServer = new MonitorServer(monitorPort, currentNode.getId());
            monitorServer.addGameServlets();
            monitorServer.startServer();
        }

        currentNode.setHttpPort(monitorPort);

        NodeController nodeController = new NodeController(currentNode);

        if(serverType.equals(ServerType.GENERIC)) {
            nodeController.reloadNodeMapping();
        }

        GameController.init(serverType, nodeController, currentNode.getId(), groupId);
        ServerCountsWorker.init(type, currentNode.getId());
        GameController.getInstance().initRooms();


        if (serverType == ServerType.TOURNAMENT) {
            TournamentLevelController.init();
        }

        Runtime.getRuntime().addShutdownHook(new GameShutdownManager(currentNode.getId(), groupId));

        CacheController.getInstance().setAsAvailableGameNode(currentNode, groupId);
        CacheController.getInstance().publishGameNodeAddEvent(currentNode);
        //GameController.getInstance().startHalfGames();

        logger.info("Game SERVER Main Started");


    }

    private static void initServer(ServerToServerMessageProcessor messageProcessor, String host, int port) {
        ServerToServerTCPServer server = new ServerToServerTCPServer(ServerToServer.ServerType.GAME, messageProcessor, host, port);
        server.start();
    }

}
