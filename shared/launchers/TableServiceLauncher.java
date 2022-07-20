package com.mynet.shared.launchers;

import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.connection.NodeToService;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.network.*;
import com.mynet.shared.network.HttpServer.MonitorServer;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.shutdown.TableShutDownManager;
import com.mynet.shared.types.ServerType;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableServiceLauncher {
    static Logger logger;
    public static NodeData currentNode;

    public static void launch(String groupId){
        TableServiceLauncher.logger = LoggerFactory.getLogger(TableServiceLauncher.class);

        MessageProcessController.init();
        CacheController.init();
        ServerGlobalVariables.init();

        NodeToService nodeToService = new NodeToService();

        String host = ServerConfiguration.get("TCP.host");
        int port = ServerConfiguration.getInt("TCP.port", -1);

        if(host == null || port == -1)
        {
            System.out.println("server.host or server.port not found, exiting..");
            System.exit(2);
        }

        try {
            currentNode = DBController.getInstance().getServerNode(NodesType.table, groupId, ServerType.GENERIC);
        }catch (Exception e){
            logger.error(e.getMessage());
            System.exit(1);
        }

        TableService.init(groupId, CacheController.getInstance());
        initServer(nodeToService, host, port);
        CacheController.getInstance().setTableNode(currentNode,groupId);
        CacheController.getInstance().publishTableNodeAddEvent(currentNode);
        Runtime.getRuntime().addShutdownHook(new TableShutDownManager(currentNode.getId(),groupId));

        int monitorPort = ServerConfiguration.getInt("monitor.port", -1);
        if (monitorPort > -1) {
            MonitorServer monitorServer = new MonitorServer(monitorPort, currentNode.getId());
            monitorServer.addTableServiceServlets();
            monitorServer.startServer();
        }
    }

    private static void initServer(ServerToServerMessageProcessor messageProcessor, String host, int port) {
        ServerToServerTCPServer server = new ServerToServerTCPServer(ServerToServer.ServerType.TABLE, messageProcessor, host, port);
        server.start();
    }
}
