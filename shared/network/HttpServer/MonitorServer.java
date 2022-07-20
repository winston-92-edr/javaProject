package com.mynet.shared.network.HttpServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorServer extends Server {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServer.class);
    private final int port;
    private final int nodeId;
    private final ServletContextHandler handler;

    public MonitorServer(int port, int nodeId) {
        super(port);
        this.port = port;
        this.nodeId = nodeId;
        handler = new ServletContextHandler(this, "/table");

    }

    public void startServer(){
        try {
            this.start();
            logger.info("Monitor server started at port: " + port);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void addGameServlets(){
        handler.addServlet(MonitorNodeHandler.class, "/status");
        handler.addServlet(TournamentGameStartHandler.class, "/start");
        handler.addServlet(GameTableListHandler.class, "/list");
        handler.addServlet(GameRoomCountsHandler.class, "/counts");
    }

    public void addTableServiceServlets(){
        handler.addServlet(TableServiceListHandler.class, "/list");
    }

    public void addChatServiceServlets(){
        handler.addServlet(BadWordHandler.class,"/badWord");
    }
}
