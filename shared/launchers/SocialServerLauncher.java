package com.mynet.shared.launchers;

import com.mynet.shared.analytics.AnalyticsLogController;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.*;
import com.mynet.shared.shutdown.SocialShutDownManager;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.DatabaseWorker;
import com.mynet.socialserver.SocialController;
import com.mynet.shared.connection.ProxyToSocial;

public class SocialServerLauncher {
    static Logger logger;
    public static NodeData currentNode;

    public static void launch(String type, ServerType serverType, String groupId) {
        SocialServerLauncher.logger = LoggerFactory.getLogger(SocialServerLauncher.class);

        CacheController.init();
        DatabaseWorker.getInstance().init();
        ServerGlobalVariables.init();
        RabbitMQLogController.init();
        AnalyticsLogController.init();

        MessageProcessController.init();

        try {
            currentNode = DBController.getInstance().getServerNode(NodesType.social, groupId, serverType);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }


        SocialController.init(currentNode.getId());

        //Timerlar burada

        String host = ServerConfiguration.get("TCP.host");
        int port = ServerConfiguration.getInt("TCP.port", -1);
        if (host == null || port == -1) {
            System.out.println("server.host or server.port not found, exiting..");
            System.exit(2);
        }

        TournamentLevelController.init();

        ProxyToSocial proxyToSocial = new ProxyToSocial(serverType);
        initServer(proxyToSocial, host, port);

        CacheController.getInstance().setSocialNode(currentNode, groupId);
        CacheController.getInstance().publishSocialNodeAddEvent(currentNode);
        Runtime.getRuntime().addShutdownHook(new SocialShutDownManager(currentNode.getId(), groupId));
    }

    private static void initServer(ServerToServerMessageProcessor messageProcessor, String host, int port) {
        ServerToServerTCPServer server = new ServerToServerTCPServer(ServerToServer.ServerType.SOCIAL, messageProcessor, host, port);
        server.start();
    }

}
