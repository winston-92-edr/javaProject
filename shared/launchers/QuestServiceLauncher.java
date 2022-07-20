package com.mynet.shared.launchers;

import com.mynet.questservice.QuestController;
import com.mynet.shared.analytics.AnalyticsLogController;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.connection.ProxyToQuest;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.*;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.DatabaseWorker;
import com.mynet.shared.shutdown.QuestShutDownManager;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestServiceLauncher {
    private static Logger logger;
    public static NodeData currentNode;

    public static void launch() {
        logger = LoggerFactory.getLogger(QuestServiceLauncher.class);

        CacheController.init();
        DatabaseWorker.getInstance().init();
        ServerGlobalVariables.init();
        RabbitMQLogController.init();
        AnalyticsLogController.init();

        MessageProcessController.init();

        String groupId = ServerConfiguration.get("group.id");
        if(groupId == null)
        {
            System.out.println("group id not found, exiting..");
            System.exit(2);
        }

        try {
            currentNode = DBController.getInstance().getServerNode(NodesType.quest, groupId, ServerType.NONE);
        }catch (Exception e){
            logger.error(e.getMessage());
            System.exit(1);
        }

        String host = ServerConfiguration.get("TCP.host");
        int port = ServerConfiguration.getInt("TCP.port", -1);
        if (host == null || port == -1) {
            System.out.println("server.host or server.port not found, exiting..");
            System.exit(2);
        }

        ProxyToQuest pq = new ProxyToQuest();
        initServer(pq, host, port);

        QuestController.init();
        QuestController.getInstance().initHealthCheck();

        CacheController.getInstance().setQuestNode(currentNode, groupId);
        CacheController.getInstance().publishQuestNodeAddEvent(currentNode);
        Runtime.getRuntime().addShutdownHook(new QuestShutDownManager(currentNode.getId(), groupId));
    }

    private static void initServer(ServerToServerMessageProcessor messageProcessor, String host, int port) {
        ServerToServerTCPServer server = new ServerToServerTCPServer(ServerToServer.ServerType.QUEST, messageProcessor, host, port);
        server.start();
    }

}
