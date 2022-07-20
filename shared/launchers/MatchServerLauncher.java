package com.mynet.shared.launchers;

import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.connection.ProxyToMatch;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.*;
import com.mynet.matchserver.MatchMakingController;
import com.mynet.matchserver.model.GameTypeInfo;
import com.mynet.shared.shutdown.MatchShutDownManager;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.DatabaseWorker;

import static com.mynet.shared.utils.Utils.getGameTypes;

public class MatchServerLauncher {
    static Logger logger;
    public static NodeData currentNode;

    public static void launch(String type, ServerType serverType, String groupId){
        MatchServerLauncher.logger = LoggerFactory.getLogger(MatchServerLauncher.class);

        CacheController.init();
        DatabaseWorker.getInstance().init();
        ServerGlobalVariables.init();
        RabbitMQLogController.init();

        MessageProcessController.init();

        GameTypeInfo[] info = getGameTypes();
        if(info == null){
            logger.error("Can't find game types to run queue !!!");
            System.exit(1);
        }

        try {
            currentNode = DBController.getInstance().getServerNode(NodesType.match, groupId, ServerType.TOURNAMENT);
        }catch (Exception e){
            logger.error(e.getMessage());
            System.exit(1);
        }

        String host = ServerConfiguration.get("TCP.host");
        int port = ServerConfiguration.getInt("TCP.port", -1);
        if(host == null || port == -1)
        {
            System.out.println("server.host or server.port not found, exiting..");
            System.exit(2);
        }

        ProxyToMatch proxyToMatch = new ProxyToMatch();
        initServer(proxyToMatch, host, port);

        MatchMakingController.init(info,groupId);

        if(serverType.equals(ServerType.TOURNAMENT)){
            TournamentLevelController.init(info);
        }

        CacheController.getInstance().setMatchNode(currentNode,groupId);
        CacheController.getInstance().publishMatchNodeAddEvent(currentNode);
        Runtime.getRuntime().addShutdownHook(new MatchShutDownManager(currentNode.getId(),groupId));
    }

    private static void initServer(ServerToServerMessageProcessor messageProcessor, String host, int port) {
        ServerToServerTCPServer server = new ServerToServerTCPServer(ServerToServer.ServerType.MATCH, messageProcessor, host, port);
        server.start();
    }

}
